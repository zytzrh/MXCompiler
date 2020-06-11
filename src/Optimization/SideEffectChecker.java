package Optimization;

import IR.Block;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.GlobalVar;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMVoidType;

import java.util.*;

public class SideEffectChecker extends Pass {
    public enum Scope {
        undefined, local, outer
    }

    private Set<LLVMfunction> sideEffect;
    private Map<Operand, Scope> scopeMap;
    private Map<LLVMfunction, Scope> returnValueScope;
    private Boolean ignoreIO;
    private Boolean ignoreLoad;

    public SideEffectChecker(Module module) {
        super(module);
    }

    public void setIgnoreIO(boolean ignoreIO) {
        this.ignoreIO = ignoreIO;
    }

    public void setIgnoreLoad(boolean ignoreLoad) {
        this.ignoreLoad = ignoreLoad;
    }

    public boolean hasSideEffect(LLVMfunction function) {
        return sideEffect.contains(function);
    }

    public boolean isOuterScope(Operand operand) {
        if (operand instanceof ConstNull)
            return false;
        return scopeMap.get(operand) == Scope.outer;
    }

    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {
            if (!function.isFunctional())
                return false;
        }

        assert ignoreIO != null;
        assert ignoreLoad != null;
        computeScope();
        checkSideEffect();
        ignoreIO = null;
        ignoreLoad = null;
        return false;
    }

    static public Scope getOperandScope(Operand operand) {
        assert operand instanceof Register;
        if (operand.getLlvMtype() instanceof LLVMPointerType)
            return Scope.outer;
        else
            return Scope.local;
    }

    private void computeScope() {
        scopeMap = new HashMap<>();
        returnValueScope = new HashMap<>();
        Queue<LLVMfunction> queue = new LinkedList<>();
        Set<LLVMfunction> inQueue = new HashSet<>();

        for (DefineGlobal defineGlobal : module.getDefineGlobals()){
            GlobalVar globalVar = defineGlobal.getGlobalVar();
            scopeMap.put(globalVar, Scope.outer);
        }
        for (LLVMfunction function : module.getFunctionMap().values()) {
            for (Register parameter : function.getParas())
                scopeMap.put(parameter, getOperandScope(parameter));
            for (Block block : function.getBlocks()) {
                LLVMInstruction ptr = block.getInstHead();
                while (ptr != null) {
                    if (ptr.hasResult()) {
                        Register result = ptr.getResult();
                        if (getOperandScope(result) == Scope.local)
                            scopeMap.put(result, Scope.local);
                        else
                            scopeMap.put(result, Scope.undefined);
                    }
                    ptr = ptr.getPostInst();
                }
            }

            if (function.getResultType() instanceof LLVMPointerType)
                returnValueScope.put(function, Scope.outer);
            else
                returnValueScope.put(function, Scope.local);
            queue.offer(function);
            inQueue.add(function);
        }
        for (LLVMfunction function : module.getBuiltInFunctionMap().values())
            returnValueScope.put(function, Scope.local);

        while (!queue.isEmpty()) {
            LLVMfunction function = queue.poll();
            inQueue.remove(function);
            computeScopeInFunction(function);

            boolean local = false;
            if (function.getResultType() instanceof LLVMVoidType)
                local = true;
            else {
                ReturnInst returnInst = ((ReturnInst) function.getExitBlock().getInstTail());
                if (scopeMap.get(returnInst.getReturnValue()) == Scope.local)
                    local = true;
            }

            if (local && returnValueScope.get(function) != Scope.local) {
                returnValueScope.replace(function, Scope.local);
                for (LLVMInstruction callInst : function.getUse().keySet()) {
                    assert callInst instanceof CallInst;
                    LLVMfunction caller = callInst.getBlock().getFunction();
                    if (!inQueue.contains(caller)) {
                        queue.offer(caller);
                        inQueue.add(caller);
                    }
                }
            }
        }

        for (LLVMfunction function : module.getFunctionMap().values()) {
            for (Block block : function.getBlocks()) {
                LLVMInstruction ptr = block.getInstHead();
                while (ptr != null) {
                    assert !ptr.hasResult() || scopeMap.get(ptr.getResult()) != Scope.undefined;
                    ptr = ptr.getPostInst();
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
            Block block = queue.poll();
            boolean changed = false;

            LLVMInstruction ptr = block.getInstHead();
            while (ptr != null) {
                changed |= ptr.updateResultScope(scopeMap, returnValueScope);
                ptr = ptr.getPostInst();
            }

            if (block.getInstTail() instanceof BranchInst) {
                BranchInst branchInst = ((BranchInst) block.getInstTail());
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

    private void checkSideEffect() {
        sideEffect = new HashSet<>();
        Queue<LLVMfunction> queue = new LinkedList<>();

        if (!ignoreIO) {
            for (LLVMfunction externalFunction : module.getBuiltInFunctionMap().values()) {
                if (externalFunction.hasSideEffect()) {
                    sideEffect.add(externalFunction);
                    queue.offer(externalFunction);
                }
            }
        }
        for (LLVMfunction function : module.getFunctionMap().values()) {
            boolean hasSideEffect = false;
            for (Block block : function.getBlocks()) {
                LLVMInstruction ptr = block.getInstHead();
                while (ptr != null) {
                    if (ptr instanceof StoreInst && scopeMap.get(((StoreInst) ptr).getAddr()) == Scope.outer) {
                        hasSideEffect = true;
                        break;
                    }
                    if (!ignoreLoad && ptr instanceof LoadInst
                            && scopeMap.get(((LoadInst) ptr).getAddr()) == Scope.outer) {
                        hasSideEffect = true;
                        break;
                    }
                    ptr = ptr.getPostInst();
                }
                if (hasSideEffect) {
                    sideEffect.add(function);
                    queue.offer(function);
                    break;
                }
            }
        }

        while (!queue.isEmpty()) {
            LLVMfunction function = queue.poll();
            for (LLVMInstruction callInst : function.getUse().keySet()) {
                assert callInst instanceof CallInst;
                LLVMfunction caller = callInst.getBlock().getFunction();
                if (!sideEffect.contains(caller)) {
                    sideEffect.add(caller);
                    queue.offer(caller);
                }
            }
        }
    }
}
