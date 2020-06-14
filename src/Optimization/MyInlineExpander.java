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

public class MyInlineExpander extends Pass {
    private final int instructionLimit = 120;

    private Map<LLVMfunction, Integer> instructionCnt;
    private Map<LLVMfunction, Set<LLVMfunction>> recursiveCalleeMap;

    public MyInlineExpander(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {
            if (!function.isFunctional())
                return false;
        }

        instructionCnt = new HashMap<>();
        recursiveCalleeMap = new HashMap<>();
        for (LLVMfunction function : module.getFunctionMap().values())
            recursiveCalleeMap.put(function, new HashSet<>());

        for (LLVMfunction function : module.getFunctionMap().values())
            countInstructionsAndCalls(function);
        for (LLVMfunction function : module.getFunctionMap().values())
            computeRecursiveCallees(function);

        changed = false;
        changed = nonRecursiveInline();
        changed |= recursiveInline();
        return false;
    }

    private void countInstructionsAndCalls(LLVMfunction currentFunction) {
        int instructionCnt = 0;
        for (Block block : currentFunction.getBlocks()) {
            LLVMInstruction ptr = block.getInstHead();
            while (ptr != null) {
                instructionCnt++;
                if (ptr instanceof CallInst) {
                    LLVMfunction callee = ((CallInst) ptr).getLlvMfunction();
                    if (!callee.isBuiltIn()) recursiveCalleeMap.get(currentFunction).add(callee);
                }
                ptr = ptr.getPostInst();
            }
        }
        this.instructionCnt.put(currentFunction, instructionCnt);
    }

    private void computeRecursiveCallees(LLVMfunction function) {
        //just update callee
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

    private boolean canBeNonRecursiveInlined(LLVMfunction callee, LLVMfunction caller) {
        if (!caller.isFunctional() || !callee.isFunctional())
            return false;
        return instructionCnt.get(callee) < instructionLimit
                && callee != caller
                && !recursiveCalleeMap.get(callee).contains(callee);
    }

    private boolean canBeRecursiveInlined(LLVMfunction callee, LLVMfunction caller) {
        if (!caller.isFunctional() || !callee.isFunctional())
            return false;
        return instructionCnt.get(callee) < instructionLimit
                && callee == caller;
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
            Block clonedBlock = (Block) block.makeCopy();
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

    private Block cutBlockWithInst(Block targetBlock, LLVMInstruction instruction){
        LLVMfunction function = targetBlock.getFunction();
        Block mergeBlock = new Block( "inlineMergeBlock", function);
        function.registerBlockName(mergeBlock.getName(), mergeBlock);
        for (Block successor : targetBlock.getSuccessors()) {
            mergeBlock.getSuccessors().add(successor);
            successor.getPredecessors().remove(targetBlock);
            successor.getPredecessors().add(mergeBlock);

            LLVMInstruction ptr = successor.getInstHead();
            while (ptr instanceof PhiInst) {
                LLVMInstruction nextInst = ptr.getPostInst();
                Operand operand = null;
                for (Pair<Operand, Block> pair : ((PhiInst) ptr).getBranches()) {
                    if (pair.getSecond() == targetBlock) {
                        operand = pair.getFirst();
                    }
                }
                assert operand != null;
                ((PhiInst) ptr).cutBlock(targetBlock);
                ((PhiInst) ptr).addBranch(operand, mergeBlock);
                ptr = nextInst;
            }
        }

        mergeBlock.setInstHead(instruction.getPostInst());
        mergeBlock.setInstTail(targetBlock.getInstTail());
        targetBlock.setInstTail(instruction);
        instruction.getPostInst().setPreInst(null);
        instruction.setPostInst(null);
        //add block to list
        mergeBlock.setNext(targetBlock.getNext());
        if (targetBlock.getNext() != null)
            targetBlock.setPrev(mergeBlock);
        mergeBlock.setPrev(targetBlock);
        targetBlock.setNext(mergeBlock);
        //reset exit block
        if (function.getExitBlock() == targetBlock)
            function.setExitBlock(mergeBlock);

        targetBlock.setSuccessors(new LinkedHashSet<>());
        mergeBlock.fixBlockOfInstruction();
        return mergeBlock;
    }

    private LLVMInstruction inlineFunction(CallInst callInst) {
        LLVMfunction caller = callInst.getBlock().getFunction();
        LLVMfunction callee = callInst.getLlvMfunction();
        Pair<ArrayList<Block>, ReturnInst> cloneResult = cloneCallee(caller, callee, callInst.getParas());      //
        ArrayList<Block> clonedBlocks = cloneResult.getFirst();
        ReturnInst returnInst = cloneResult.getSecond();

        Block inlineDivergedBlock = callInst.getBlock();
        Block inlineMergedBlock = cutBlockWithInst(inlineDivergedBlock, callInst);

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
        inlineDivergedBlock.addInst(new BranchInst(inlineDivergedBlock, null, clonedBlocks.get(0), null));
        clonedBlocks.get(blocksCnt - 1).addInst(new BranchInst(clonedBlocks.get(blocksCnt - 1), null, inlineMergedBlock, null));

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
                                    && canBeNonRecursiveInlined(callee, function)) {
                                next = inlineFunction(((CallInst) ptr));
                                instructionCnt.replace(function,
                                        instructionCnt.get(function) + instructionCnt.get(callee) - 2);
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
        final int inlineDepth = 3;
        for (int i = 0; i < inlineDepth; i++) {
            for (LLVMfunction function : module.getFunctionMap().values()) {
                for (Block block : function.getBlocks()) {
                    LLVMInstruction ptr = block.getInstHead();
                    while (ptr != null) {
                        LLVMInstruction next = ptr.getPostInst();
                        if (ptr instanceof CallInst) {
                            LLVMfunction callee = ((CallInst) ptr).getLlvMfunction();
                            if (module.getFunctionMap().containsValue(callee)
                                    && canBeRecursiveInlined(callee, function)) {
                                next = inlineFunction(((CallInst) ptr));
                                instructionCnt.replace(function,
                                        instructionCnt.get(function) + instructionCnt.get(callee) - 2);
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
