package Optimization;

import IR.Block;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;

import java.util.*;

public class SSAConstructor extends Pass {
    private ArrayList<AllocInst> allocaInst;
    private Map<Block, Map<AllocInst, PhiInst>> phiInstMap;
    private Map<LoadInst, AllocInst> useAlloca;
    private Map<StoreInst, AllocInst> defAlloca;
    private Map<Block, Map<AllocInst, Operand>> renameTable;
    private Set<Block> visit;

    public SSAConstructor(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {
            if (!function.isFunctional())
                return false;
        }
        for (LLVMfunction function : module.getFunctionMap().values())
            constructSSA(function);
        return true;
    }

    private void constructSSA(LLVMfunction function) {
        allocaInst = function.getAllocaInstructions();
        phiInstMap = new HashMap<>();
        useAlloca = new HashMap<>();
        defAlloca = new HashMap<>();
        renameTable = new HashMap<>();

        for (Block block : function.getBlocks()) {
            phiInstMap.put(block, new HashMap<>());
            renameTable.put(block, new HashMap<>());
        }

        for (AllocInst alloca : allocaInst) {
            ArrayList<StoreInst> defs = new ArrayList<>();
            for (LLVMInstruction useInst : alloca.getResult().getUse().keySet()) {
                assert useInst instanceof LoadInst || useInst instanceof StoreInst;
                if (useInst instanceof LoadInst)
                    useAlloca.put((LoadInst) useInst, alloca);
                else {
                    defs.add((StoreInst) useInst);
                    defAlloca.put((StoreInst) useInst, alloca);
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
                assert block != null;

                for (Block df : block.getDF()) {
                    if (!phiSet.contains(df)) {
                        addPhiInst(df, alloca);
                        phiSet.add(df);
                        if (!visitSet.contains(df)) {
                            queue.offer(df);
                            visitSet.add(df);
                        }
                    }
                }
            }

            alloca.removeFromBlock();
        }

        // Remove some redundant loads first to insure no exception occurs during renaming.
        // A very simple dead code elimination which only removes loads.
        // Why? Avoid using a variable before any of its definition.
        loadInstElimination(function);

        visit = new HashSet<>();
        rename(function.getInitBlock(), null);
    }

    private void addPhiInst(Block block, AllocInst alloca) {
        String name = alloca.getResult().getName().split("\\$")[0];
        Register result = new Register(alloca.getLlvMtype(), name);
        phiInstMap.get(block).put(alloca, new PhiInst(block, new LinkedHashSet<>(), result));
        block.getFunction().registerVar(result.getName(), result);
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

    private void rename(Block block, Block predecessor) {
        Map<AllocInst, PhiInst> map = phiInstMap.get(block);
        for (AllocInst alloca : map.keySet()) {
            PhiInst phiInst = map.get(alloca);
            Operand value;
            if (!renameTable.get(predecessor).containsKey(alloca)
                    || renameTable.get(predecessor).get(alloca) == null) {
                value = alloca.getLlvMtype().DefaultValue();
            } else
                value = renameTable.get(predecessor).get(alloca);
            phiInst.addBranch(value, predecessor);
        }
        if (predecessor != null) {
            for (AllocInst alloca : allocaInst) {
                if (!map.containsKey(alloca))
                    renameTable.get(block).put(alloca, renameTable.get(predecessor).get(alloca));
            }
        }

        if (visit.contains(block))
            return;
        visit.add(block);

        for (AllocInst alloca : map.keySet())
            renameTable.get(block).put(alloca, map.get(alloca).getResult());

        ArrayList<LLVMInstruction> instructions = block.getInstructions();
        for (LLVMInstruction instruction : instructions) {
            if (instruction instanceof LoadInst && useAlloca.containsKey(instruction)) {
                AllocInst alloca = useAlloca.get(instruction);
                assert renameTable.containsKey(block);
                assert renameTable.get(block).containsKey(alloca);
                Operand value = renameTable.get(block).get(alloca);
                ((LoadInst) instruction).getResult().beOverriden(value);
                instruction.removeFromBlock();
            } else if (instruction instanceof StoreInst && defAlloca.containsKey(instruction)) {
                AllocInst alloca = defAlloca.get(instruction);
                if (!renameTable.get(block).containsKey(alloca))
                    renameTable.get(block).put(alloca, ((StoreInst) instruction).getValue());
                else
                    renameTable.get(block).replace(alloca, ((StoreInst) instruction).getValue());
                instruction.removeFromBlock();
            }
        }

        for (Block successor : block.getSuccessors())
            rename(successor, block);

        for (PhiInst phiInst : map.values())
            block.addInstFront(phiInst);
    }
}
