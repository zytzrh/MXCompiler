package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMtype;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class AllocInst extends LLVMInstruction {
    private Register result;
    private LLVMtype llvMtype;

    public AllocInst(Block block, Register result, LLVMtype llvMtype) {
        super(block);
        this.result = result;
        this.llvMtype = llvMtype;
    }


    @Override
    public String toString() {
        return result.toString() + " = alloca " + llvMtype.toString();
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) { }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }

    public LLVMtype getLlvMtype() {
        return llvMtype;
    }

    public void setLlvMtype(LLVMtype llvMtype) {
        this.llvMtype = llvMtype;
    }

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        ConstOptim.Status status = constOptim.getStatus(result);
        if (status.getOperandStatus() == ConstOptim.Status.OperandStatus.constant) {
            result.beOverriden(status.getOperand());
            this.removeFromBlock();
            return true;
        } else
            return false;
    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap,
                                     Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        if (scopeMap.get(result) != SideEffectChecker.Scope.local) {
            scopeMap.replace(result, SideEffectChecker.Scope.local);
            return true;
        } else
            return false;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        //do nothing
    }
}
