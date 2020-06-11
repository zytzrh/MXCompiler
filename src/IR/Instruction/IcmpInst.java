package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.*;
import IR.TypeSystem.LLVMIntType;
import IR.TypeSystem.LLVMtype;
import Optimization.ConstOptim;

public class IcmpInst extends LLVMInstruction{
    public enum IcmpName {
        eq, ne, sgt, sge, slt, sle
    }

    private IcmpName operator;
    private LLVMtype irType;
    private Operand op1;
    private Operand op2;
    private Register result;

    public IcmpInst(Block block, IcmpName operator, LLVMtype irType, Operand op1, Operand op2, Register result) {
        super(block);
        this.operator = operator;
        this.irType = irType;
        this.op1 = op1;
        this.op2 = op2;
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = icmp "
                + operator.name() + " " + irType.toString() + " " + op1.toString() + ", " + op2.toString();
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

    public LLVMtype getIrType() {
        return irType;
    }

    public void setIrType(LLVMtype irType) {
        this.irType = irType;
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

    public boolean shouldSwap(boolean assertOrNot) {
        if (assertOrNot)
            assert !(op1 instanceof Constant) || !(op2 instanceof Constant);
        else {
            if (op1 instanceof Constant && op2 instanceof Constant)
                return false;
        }
        return op1 instanceof Constant;
    }

    public void swapOps() {
        operator = operator == IcmpName.sgt ? IcmpName.slt
                : operator == IcmpName.slt ? IcmpName.sgt
                : operator == IcmpName.sge ? IcmpName.sle
                : operator == IcmpName.sle ? IcmpName.sge
                : operator;
        Operand tmp = op1;
        op1 = op2;
        op2 = tmp;
    }

    public void convertLeGeToLtGt() {
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
}
