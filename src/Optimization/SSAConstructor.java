package Optimization;

import IR.Block;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;

import java.util.*;

public class SSAConstructor extends IRPass {
    //for a function
    //allocInst can represent a variable
    private ArrayList<AllocInst> allocaInsts;
    private Map<LoadInst, AllocInst> useAlloca;
    private Map<StoreInst, AllocInst> defAlloca;
    private Map<Block, Map<AllocInst, PhiInst>> phiInstMap;



    private Map<Block, Map<AllocInst, Operand>> renameTable;

    public SSAConstructor(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        if(!module.checkNormalFunctional()) return false;
        for (LLVMfunction function : module.getFunctionMap().values())
            constructSSA(function);
        return true;
    }

    public ArrayList<AllocInst> getAllocaInsts(LLVMfunction llvMfunction) {
        ArrayList<AllocInst> allocaInst = new ArrayList<>();
        LLVMInstruction currentInst = llvMfunction.getInitBlock().getInstHead();
        while (currentInst != null) {
            if (currentInst instanceof AllocInst)
                allocaInst.add((AllocInst) currentInst);
            currentInst = currentInst.getPostInst();
        }
        return allocaInst;
    }

    private void constructSSA(LLVMfunction function) {
        allocaInsts = getAllocaInsts(function);
        phiInstMap = new HashMap<>();
        useAlloca = new HashMap<>();
        defAlloca = new HashMap<>();
        renameTable = new HashMap<>();

        for (Block block : function.getBlocks()) {
            phiInstMap.put(block, new HashMap<>());
            renameTable.put(block, new HashMap<>());
        }

        for (AllocInst allocaInst : allocaInsts) {
            addPhiForVar(allocaInst);
        }

        loadInstElimination(function);
        rename(function.getInitBlock(), null, new HashSet<>());
    }

    void addPhiForVar(AllocInst allocInst){
        ArrayList<StoreInst> defs = new ArrayList<>();
        for (LLVMInstruction useInst : allocInst.getResult().getUse().keySet()) {
            if (useInst instanceof LoadInst)
                useAlloca.put((LoadInst) useInst, allocInst);
            else if(useInst instanceof StoreInst){
                defs.add((StoreInst) useInst);
                defAlloca.put((StoreInst) useInst, allocInst);
            }else{
                throw new RuntimeException();
            }
        }
        Queue<Block> queue = new LinkedList<>();
        HashSet<Block> visitSet = new HashSet<>();
        HashSet<Block> phiSet = new HashSet<>();
        for (StoreInst def : defs) {
            queue.offer(def.getBlock());
            visitSet.add(def.getBlock());
        }
        while (!queue.isEmpty()) {
            Block block = queue.poll();
            for (Block df : block.getDF()) {
                if (!phiSet.contains(df)) {
                    addPhiInst(df, allocInst);
                    phiSet.add(df);
                    if (!visitSet.contains(df)) {
                        queue.offer(df);
                        visitSet.add(df);
                    }
                }
            }
        }
        allocInst.removeFromBlock();        //optimize
    }

    private void addPhiInst(Block block, AllocInst allocInst) {
        String name = allocInst.getResult().getName().split("\\$")[0];
        Register result = new Register(allocInst.getLlvMtype(), name);
        block.getFunction().registerVar(result.getName(), result);
        phiInstMap.get(block).put(allocInst, new PhiInst(block, new LinkedHashSet<>(), result));
    }

    private void loadInstElimination(LLVMfunction function) {
        for (Block block : function.getBlocks()) {
            ArrayList<LLVMInstruction> instructions = block.getInstructions();
            for (LLVMInstruction instruction : instructions) {
                if (instruction instanceof LoadInst && ((LoadInst) instruction).getResult().getUse().isEmpty())
                    instruction.removeFromBlock();
            }
        }
    }

    private void rename(Block block, Block predecessor, Set<Block> visit) {
        Map<AllocInst, PhiInst> map = phiInstMap.get(block);
        for (AllocInst allocInst : map.keySet()) {
            PhiInst phiInst = map.get(allocInst);
            Operand value;
            if(renameTable.get(predecessor).containsKey(allocInst)){
                assert renameTable.get(predecessor).get(allocInst) != null;
                value = renameTable.get(predecessor).get(allocInst);
                phiInst.addBranch(value, predecessor);
            }else{
                throw new RuntimeException();
            }
        }

        if (predecessor != null) {
            for (AllocInst allocInst : allocaInsts) {
                if (!map.containsKey(allocInst))
                    renameTable.get(block).put(allocInst, renameTable.get(predecessor).get(allocInst));
            }
        }

        if (visit.contains(block))
            return;
        visit.add(block);

        for (AllocInst allocInst : map.keySet())
            renameTable.get(block).put(allocInst, map.get(allocInst).getResult());

        ArrayList<LLVMInstruction> instructions = block.getInstructions();
        for (LLVMInstruction instruction : instructions) {
            if (instruction instanceof LoadInst && useAlloca.containsKey(instruction)) {
                AllocInst allocInst = useAlloca.get(instruction);
                assert renameTable.containsKey(block);
                assert renameTable.get(block).containsKey(allocInst);
                Operand value = renameTable.get(block).get(allocInst);
                ((LoadInst) instruction).getResult().beOverriden(value);
                instruction.removeFromBlock();
            } else if (instruction instanceof StoreInst && defAlloca.containsKey(instruction)) {
                AllocInst allocInst = defAlloca.get(instruction);
                if (!renameTable.get(block).containsKey(allocInst)){
                    Operand newdef = ((StoreInst) instruction).getValue();
                    renameTable.get(block).put(allocInst, newdef);
                } else{
                    Operand newdef = ((StoreInst) instruction).getValue();
                    renameTable.get(block).replace(allocInst, newdef);
                }
                instruction.removeFromBlock();
            }
        }

        for (Block successor : block.getSuccessors())
            rename(successor, block, visit);

        for (PhiInst phiInst : map.values())
            block.addInstFront(phiInst);
    }
}
