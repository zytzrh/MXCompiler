package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMtype;

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

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
