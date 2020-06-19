package Optimization;

import IR.Block;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;
import IR.TypeSystem.LLVMVoidType;
import Utility.Pair;

import java.util.*;

public class InlineExpansion extends IRPass {
    private final int instructionLimit = 100;
    private final int inlineDepth = 3;
    private Map<LLVMfunction, Integer> instructionCount;
    private Map<LLVMfunction, Set<LLVMfunction>> recursiveCalleeMap;

    public InlineExpansion(Module module) {
        super(module);
        instructionCount = new HashMap<>();
        recursiveCalleeMap = new HashMap<>();
    }

    @Override
    public boolean run() {
        if(!module.checkNormalFunctional()) return false;
        if(!module.checkTrivalCall()) return false;
        initMap();
        changed = false;
        if(nonRecursiveInline()) changed = true;
        if(recursiveInline()) changed = true;
        return false;
    }

    public void initMap(){
        instructionCount = new HashMap<>();
        recursiveCalleeMap = new HashMap<>();
        for (LLVMfunction function : module.getFunctionMap().values())
            recursiveCalleeMap.put(function, new HashSet<>());

        for (LLVMfunction function : module.getFunctionMap().values()){
            //count instrcutionn in function
            int instructionCount = 0;
            for (Block block : function.getBlocks()) {
                LLVMInstruction currentInst = block.getInstHead();
                while (currentInst != null) {
                    instructionCount++;
                    if (currentInst instanceof CallInst) {
                        CallInst callInst = (CallInst) currentInst;
                        LLVMfunction callee = callInst.getLlvMfunction();
                        if(module.getFunctionMap().containsValue(callee))
                            recursiveCalleeMap.get(function).add(callee);
                    }
                    currentInst = currentInst.getPostInst();
                }
            }
            this.instructionCount.put(function, instructionCount);
        }
        for (LLVMfunction function : module.getFunctionMap().values()){
            //init recursive function relationship
            Queue<LLVMfunction> queue = new LinkedList<>();
            Set<LLVMfunction> callees = recursiveCalleeMap.get(function);
            for (LLVMfunction callee : callees)
                queue.offer(callee);

            while (!queue.isEmpty()) {
                LLVMfunction func = queue.poll();
                for (LLVMfunction callee : recursiveCalleeMap.get(func)) {
                    if (!callees.contains(callee)) {
                        callees.add(callee);
                        queue.offer(callee);
                    }
                }
            }
        }
    }

    private boolean shouldNormalInlined(LLVMfunction callee, LLVMfunction caller) {
        if (!caller.isFunctional() || !callee.isFunctional())
            return false;
        return instructionCount.get(callee) < instructionLimit && callee != caller && !recursiveCalleeMap.get(callee).contains(callee);
    }

    private boolean shouldRecursiveInlined(LLVMfunction callee, LLVMfunction caller) {
        if (!caller.isFunctional() || !callee.isFunctional())
            return false;
        return instructionCount.get(callee) < instructionLimit && callee == caller;
    }

    private Pair<ArrayList<Block>, ReturnInst> cloneCallee(LLVMfunction caller,
                                                           LLVMfunction callee,
                                                           ArrayList<Operand> actualParameters) {
        Map<Block, Block> blockMap = new HashMap<>();
        Map<Operand, Operand> operandMap = new HashMap<>();

        for (int i = 0; i < actualParameters.size(); i++)
            operandMap.put(callee.getParas().get(i), actualParameters.get(i));

        ArrayList<Block> clonedBlocks = new ArrayList<>();
        for (Block block : callee.getBlocks()) {
            Block clonedBlock = (Block) block.clone();
            clonedBlock.setFunction(caller);
            clonedBlocks.add(clonedBlock);

            blockMap.put(block, clonedBlock);
            caller.registerBlockName(clonedBlock.getNameWithoutDot(), clonedBlock);

            LLVMInstruction ptr = block.getInstHead();
            LLVMInstruction clonedPtr = clonedBlock.getInstHead();
            while (ptr != null && clonedPtr != null) {
                if (ptr.hasResult()) {
                    assert clonedPtr.hasResult();
                    Register result = ptr.getResult();
                    Register clonedResult = clonedPtr.getResult();
                    assert (result == null && clonedResult == null) || (result != null && clonedResult != null);
                    if (result != null) {
                        operandMap.put(result, clonedResult);
                        caller.registerVar(clonedResult.getNameWithoutDot(), clonedResult);
                    }
                }
                ptr = ptr.getPostInst();
                clonedPtr = clonedPtr.getPostInst();
            }
            assert ptr == null && clonedPtr == null;
        }

        for (int i = 0; i < clonedBlocks.size(); i++) {
            Block clonedBlock = clonedBlocks.get(i);
            if (i != 0)
                clonedBlock.setPrev(clonedBlocks.get(i - 1));
            if (i != clonedBlocks.size() - 1)
                clonedBlock.setNext(clonedBlocks.get(i + 1));

            Set<Block> predecessors = new LinkedHashSet<>();
            Set<Block> successors = new LinkedHashSet<>();
            for (Block predecessor : clonedBlock.getPredecessors()) {
                assert blockMap.containsKey(predecessor);
                predecessors.add(blockMap.get(predecessor));
            }
            for (Block successor : clonedBlock.getSuccessors()) {
                assert blockMap.containsKey(successor);
                successors.add(blockMap.get(successor));
            }
            clonedBlock.setPredecessors(predecessors);
            clonedBlock.setSuccessors(successors);


            LLVMInstruction clonedPtr = clonedBlock.getInstHead();
            while (clonedPtr != null) {
                clonedPtr.clonedUseReplace(blockMap, operandMap);
                clonedPtr = clonedPtr.getPostInst();
            }
        }

        ReturnInst returnInst = null;
        for (Block clonedBlock : clonedBlocks) {
            if (clonedBlock.getInstTail() instanceof ReturnInst) {
                assert returnInst == null;
                returnInst = (ReturnInst) clonedBlock.getInstTail();
            }
        }
        assert returnInst != null;
        return new Pair<>(clonedBlocks, returnInst);
    }

    private LLVMInstruction inlineFunction(CallInst callInst) {
        LLVMfunction caller = callInst.getBlock().getFunction();
        LLVMfunction callee = callInst.getLlvMfunction();
        Pair<ArrayList<Block>, ReturnInst> cloneResult = cloneCallee(caller, callee, callInst.getParas());
        ArrayList<Block> clonedBlocks = cloneResult.getFirst();
        ReturnInst returnInst = cloneResult.getSecond();

        Block inlineDivergedBlock = callInst.getBlock();
        Block inlineMergedBlock = inlineDivergedBlock.split(callInst);

        int blocksCnt = clonedBlocks.size();
        inlineDivergedBlock.setNext(clonedBlocks.get(0));
        clonedBlocks.get(0).setPrev(inlineDivergedBlock);
        inlineMergedBlock.setPrev(clonedBlocks.get(blocksCnt - 1));
        clonedBlocks.get(blocksCnt - 1).setNext(inlineMergedBlock);

        if (!(callee.getResultType() instanceof LLVMVoidType)) {
            assert !callInst.isVoidCall();
            assert returnInst.getReturnValue() != null;
            callInst.getResult().beOverriden(returnInst.getReturnValue());
        }

        returnInst.removeFromBlock();
        callInst.removeFromBlock();
        inlineDivergedBlock.addInst
                (new BranchInst(inlineDivergedBlock, null, clonedBlocks.get(0), null));
//        LLVMInstruction tmpInst = clonedBlocks.get(0).getInstHead();
//        while(tmpInst instanceof PhiInst){
//            PhiInst phiInst = (PhiInst) tmpInst;
//            for(Pair<Operand, Block>branch : phiInst.getBranches()){
//                if(branch.getSecond() == inlineDivergedBlock)
//                    inlineDivergedBlock.addUse(tmpInst);
//            }
//            tmpInst = tmpInst.getPostInst();
//        }

        clonedBlocks.get(blocksCnt - 1).addInst
                (new BranchInst(clonedBlocks.get(blocksCnt - 1), null, inlineMergedBlock, null));

        return inlineMergedBlock.getInstHead();
    }

    private boolean nonRecursiveInline() {
        boolean changed = false;
        while (true) {
            boolean loopChanged = false;
            for (LLVMfunction function : module.getFunctionMap().values()) {
                for (Block block : function.getBlocks()) {
                    LLVMInstruction ptr = block.getInstHead();
                    while (ptr != null) {
                        LLVMInstruction next = ptr.getPostInst();
                        if (ptr instanceof CallInst) {
                            LLVMfunction callee = ((CallInst) ptr).getLlvMfunction();
                            if (module.getFunctionMap().containsValue(callee)
                                    && shouldNormalInlined(callee, function)) {
                                next = inlineFunction(((CallInst) ptr));
                                instructionCount.replace(function,
                                        instructionCount.get(function) + instructionCount.get(callee) - 2);
                                loopChanged = true;
                            }
                        }
                        ptr = next;
                    }
                }
            }
            if (loopChanged)
                changed = true;
            else
                break;
        }
        return changed;
    }

    private boolean recursiveInline() {
        boolean changed = false;
        for (int i = 0; i < inlineDepth; i++) {
            for (LLVMfunction function : module.getFunctionMap().values()) {
                for (Block block : function.getBlocks()) {
                    LLVMInstruction ptr = block.getInstHead();
                    while (ptr != null) {
                        LLVMInstruction next = ptr.getPostInst();
                        if (ptr instanceof CallInst) {
                            LLVMfunction callee = ((CallInst) ptr).getLlvMfunction();
                            if (module.getFunctionMap().containsValue(callee)
                                    && shouldRecursiveInlined(callee, function)) {
                                next = inlineFunction(((CallInst) ptr));
                                instructionCount.replace(function,
                                        instructionCount.get(function) + instructionCount.get(callee) - 2);
                                changed = true;
                            }
                        }
                        ptr = next;
                    }
                }
            }
        }
        return changed;
    }
}
