package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.*;
import IR.TypeSystem.LLVMIntType;
import IR.TypeSystem.LLVMtype;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class IcmpInst extends LLVMInstruction{
    public enum IcmpName {
        eq, ne, sgt, sge, slt, sle
    }

    private IcmpName operator;
    private LLVMtype compareType;
    private Operand op1;
    private Operand op2;
    private Register result;

    public IcmpInst(Block block, IcmpName operator, LLVMtype compareType, Operand op1, Operand op2, Register result) {
        super(block);
        this.operator = operator;
        this.compareType = compareType;
        this.op1 = op1;
        this.op2 = op2;
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = icmp "
                + operator.name() + " " + compareType.toString() + " " + op1.toString() + ", " + op2.toString();
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        op1.removeUse(this);
        op2.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(op1 == oldUse){
            op1.removeUse(this);
            op1 = (Operand) newUse;
            op1.addUse(this);
        }
        if(op2 == oldUse){
            op2.removeUse(this);
            op2 = (Operand) newUse;
            op2.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public IcmpName getOperator() {
        return operator;
    }

    public void setOperator(IcmpName operator) {
        this.operator = operator;
    }

    public LLVMtype getCompareType() {
        return compareType;
    }

    public void setCompareType(LLVMtype compareType) {
        this.compareType = compareType;
    }

    public Operand getOp1() {
        return op1;
    }

    public void setOp1(Operand op1) {
        this.op1 = op1;
    }

    public Operand getOp2() {
        return op2;
    }

    public void setOp2(Operand op2) {
        this.op2 = op2;
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


    public void swapOpIfNeed(){
        if(op1 instanceof Constant){
            switch (operator) {
                case eq:
                    //do nothing
                    break;
                case ne:
                    //do nothing
                    break;
                case sgt:
                    operator = IcmpName.slt;
                    break;
                case sge:
                    operator = IcmpName.sle;
                    break;
                case slt:
                    operator = IcmpName.sgt;
                    break;
                case sle:
                    operator = IcmpName.sge;
                    break;
            }
            Operand tmp = op1;
            op1 = op2;
            op2 = tmp;
        }
    }

    public void removeEqual() {
        if (op2 instanceof ConstBool)
            return;
        assert op2 instanceof ConstInt;
        if (operator == IcmpName.sle) {
            operator = IcmpName.slt;
            assert ((ConstInt) op2).getValue() != Integer.MAX_VALUE;
            this.op2 = new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), ((ConstInt) op2).getValue() + 1);
        } else if (operator == IcmpName.sge) {
            operator = IcmpName.sgt;
            assert ((ConstInt) op2).getValue() != Integer.MIN_VALUE;
            this.op2 = new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), ((ConstInt) op2).getValue() - 1);
        }
    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        if (scopeMap.get(result) != SideEffectChecker.Scope.local) {
            scopeMap.replace(result, SideEffectChecker.Scope.local);
            return true;
        } else
            return false;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        op1.markBaseAsLive(live, queue);
        op2.markBaseAsLive(live, queue);
    }

    @Override
    public LLVMInstruction makeCopy() {
        IcmpInst icmpInst = new IcmpInst(this.getBlock(), this.operator, this.compareType,
                this.op1, this.op2, this.result.makeCopy());
        icmpInst.result.setDef(icmpInst);
        return icmpInst;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        if (op1 instanceof Register) {
            assert operandMap.containsKey(op1);
            op1 = operandMap.get(op1);
        }
        if (op2 instanceof Register) {
            assert operandMap.containsKey(op2);
            op2 = operandMap.get(op2);
        }
        op1.addUse(this);
        op2.addUse(this);
    }

    @Override
    public Object clone() {
        IcmpInst icmpInst = (IcmpInst) super.clone();
        icmpInst.operator = this.operator;
        icmpInst.compareType = this.compareType;
        icmpInst.op1 = this.op1;
        icmpInst.op2 = this.op2;
        icmpInst.result = (Register) this.result.clone();

        icmpInst.result.setDef(icmpInst);
        return icmpInst;
    }
}
