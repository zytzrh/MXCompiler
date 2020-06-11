package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMtype;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BitCastInst extends LLVMInstruction{
    private Operand source;
    private LLVMtype ObjectType;
    private Register result;

    public BitCastInst(Block block, Operand source, LLVMtype objectType, Register result) {
        super(block);
        this.source = source;
        ObjectType = objectType;
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = bitcast " + source.getLlvMtype().toString() + " " + source.toString() + " to "
                + ObjectType.toString();
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        source.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(source == oldUse){
            source.removeUse(this);
            source = (Operand) newUse;
            source.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Operand getSource() {
        return source;
    }

    public void setSource(Operand source) {
        this.source = source;
    }

    public LLVMtype getObjectType() {
        return ObjectType;
    }

    public void setObjectType(LLVMtype objectType) {
        ObjectType = objectType;
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
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
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        if (source instanceof ConstNull) {
            if (scopeMap.get(result) != SideEffectChecker.Scope.local) {
                scopeMap.replace(result, SideEffectChecker.Scope.local);
                return true;
            } else
                return false;
        }
        SideEffectChecker.Scope scope = scopeMap.get(source);
        assert scope != SideEffectChecker.Scope.undefined;
        if (scopeMap.get(result) != scope) {
            scopeMap.replace(result, scope);
            return true;
        } else
            return false;

    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        source.markBaseAsLive(live, queue);
    }
}
