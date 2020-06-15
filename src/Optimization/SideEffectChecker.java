package Optimization;

import IR.Block;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.GlobalVar;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMVoidType;

import java.util.*;

public class SideEffectChecker extends IRPass {
    public enum Scope {
        undefined, localVar, outerVar
    }

    private Set<LLVMfunction> sideEffectFunctions;
    private Map<Operand, Scope> operandScopeMap;

    public SideEffectChecker(Module module) {
        super(module);
    }


    public boolean checkSideEffect(LLVMfunction function) {
        return sideEffectFunctions.contains(function);
    }

    @Override
    public boolean run() {
        if(!module.checkNormalFunctional()) return false;
        operandScopeMap = new HashMap<>();
        returnValueScope = new HashMap<>();

        roughComputeScope();
        preciseComputeScope();
        checkSideEffect();
        return false;
    }

    private void checkSideEffect() {
        checkNoProblem();
        sideEffectFunctions = new HashSet<>();
        Queue<LLVMfunction> queue = new LinkedList<>();

        for (LLVMfunction externalFunction : module.getBuiltInFunctionMap().values()) {
            if (externalFunction.hasSideEffect()) {
                sideEffectFunctions.add(externalFunction);
                queue.offer(externalFunction);
            }
        }
        for (LLVMfunction function : module.getFunctionMap().values()) {
            boolean hasSideEffect = false;
            for (Block block : function.getBlocks()) {
                if (checkBlockEffect(block)) {
                    sideEffectFunctions.add(function);
                    queue.offer(function);
                    break;
                }
            }
        }
        checkRecursive(queue);
    }

    private boolean checkBlockEffect(Block block){
        LLVMInstruction currentInst = block.getInstHead();
        while (currentInst != null) {
            if (currentInst instanceof StoreInst) {
                StoreInst storeInst = (StoreInst) currentInst;
                Operand addr = storeInst.getAddr();
                if (operandScopeMap.get(addr) == Scope.outerVar) {
                    return true;
                }
            }
            currentInst = currentInst.getPostInst();
        }
        return false;
    }

    private void checkRecursive(Queue<LLVMfunction> queue){
        while (!queue.isEmpty()) {
            LLVMfunction function = queue.poll();
            for (LLVMInstruction callInst : function.getUse().keySet()) {
                assert callInst instanceof CallInst;
                LLVMfunction caller = callInst.getBlock().getFunction();
                if (!sideEffectFunctions.contains(caller)) {
                    sideEffectFunctions.add(caller);
                    queue.offer(caller);
                }
            }
        }
    }



    private Map<LLVMfunction, Scope> returnValueScope;

    static public Scope getOperandScope(Operand operand) {
        assert operand instanceof Register;
        if (operand.getLlvMtype() instanceof LLVMPointerType)
            return Scope.outerVar;
        else
            return Scope.localVar;
    }

    private void roughComputeScope() {
        //globalVar outer
        for (DefineGlobal defineGlobal : module.getDefineGlobals()){
            GlobalVar globalVar = defineGlobal.getGlobalVar();
            operandScopeMap.put(globalVar, Scope.outerVar);
        }

        for (LLVMfunction function : module.getFunctionMap().values()) {
            for (Register parameter : function.getParas())
                operandScopeMap.put(parameter, getOperandScope(parameter));
            for (Block block : function.getBlocks()) {
                LLVMInstruction currentInst = block.getInstHead();
                while (currentInst != null) {
                    LLVMInstruction nextInst = currentInst.getPostInst();
                    if (currentInst.hasResult()) {
                        Register result = currentInst.getResult();
                        if (getOperandScope(result) == Scope.localVar)
                            operandScopeMap.put(result, Scope.localVar);
                        else
                            operandScopeMap.put(result, Scope.undefined);
                    }
                    currentInst = nextInst;
                }
            }
        }

        for(LLVMfunction mfunction : module.getFunctionMap().values()){
            if (mfunction.getResultType() instanceof LLVMPointerType)
                returnValueScope.put(mfunction, Scope.outerVar);
            else
                returnValueScope.put(mfunction, Scope.localVar);
        }
        for (LLVMfunction function : module.getBuiltInFunctionMap().values())
            returnValueScope.put(function, Scope.localVar);




    }

    private void preciseComputeScope(){
        Queue<LLVMfunction> queue = new LinkedList<>();
        Set<LLVMfunction> isInQueue = new HashSet<>();
        for(LLVMfunction mfunction : module.getFunctionMap().values()){
            queue.offer(mfunction);
            isInQueue.add(mfunction);
        }
        while (!queue.isEmpty()) {
            LLVMfunction function = queue.poll();
            isInQueue.remove(function);
            computeScopeInFunction(function);
            boolean local = false;
            if (function.getResultType() instanceof LLVMVoidType)
                local = true;
            else {
                ReturnInst returnInst = ((ReturnInst) function.getExitBlock().getInstTail());
                if (operandScopeMap.get(returnInst.getReturnValue()) == Scope.localVar)
                    local = true;
            }
            if (local && returnValueScope.get(function) != Scope.localVar) {
                returnValueScope.replace(function, Scope.localVar);     //object
                for (LLVMInstruction callInst : function.getUse().keySet()) {
                    assert callInst instanceof CallInst;
                    LLVMfunction caller = callInst.getBlock().getFunction();
                    if (!isInQueue.contains(caller)) {
                        queue.offer(caller);
                        isInQueue.add(caller);
                    }
                }
            }
        }
    }

    private void computeScopeInFunction(LLVMfunction function) {
        Queue<Block> queue = new LinkedList<>();
        Set<Block> visit = new HashSet<>();

        queue.offer(function.getInitBlock());
        visit.add(function.getInitBlock());
        while (!queue.isEmpty()) {
            Block currentBlock = queue.poll();
            boolean changed = false;

            LLVMInstruction ptr = currentBlock.getInstHead();
            while (ptr != null) {
                changed |= ptr.updateResultScope(operandScopeMap, returnValueScope);
                ptr = ptr.getPostInst();
            }

            if (currentBlock.getInstTail() instanceof BranchInst) {
                BranchInst branchInst = ((BranchInst) currentBlock.getInstTail());
                if (!visit.contains(branchInst.getIfTrueBlock())) {
                    queue.offer(branchInst.getIfTrueBlock());
                    visit.add(branchInst.getIfTrueBlock());
                } else if (changed)
                    queue.offer(branchInst.getIfTrueBlock());

                if (branchInst.getCondition() != null) {
                    if (!visit.contains(branchInst.getIfFalseBlock())) {
                        queue.offer(branchInst.getIfFalseBlock());
                        visit.add(branchInst.getIfFalseBlock());
                    } else if (changed)
                        queue.offer(branchInst.getIfFalseBlock());
                }
            }
        }
    }

    private void checkNoProblem(){
        for (LLVMfunction function : module.getFunctionMap().values()) {
            for (Block block : function.getBlocks()) {
                LLVMInstruction ptr = block.getInstHead();
                while (ptr != null) {
                    assert !ptr.hasResult() || operandScopeMap.get(ptr.getResult()) != Scope.undefined;
                    ptr = ptr.getPostInst();
                }
            }
        }
    }

}
