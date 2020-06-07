package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;

public class BinaryOpInst extends LLVMInstruction{
    public enum BinaryOpName {
        add, sub, mul, sdiv, srem,          // Binary Operations
        shl, ashr, and, or, xor             // Bitwise Binary Operations
    }

    private BinaryOpName op;
    private Operand lhs;
    private Operand rhs;
    private Register result;

    public BinaryOpInst(Block block, BinaryOpName op, Operand lhs, Operand rhs, Register result) {
        super(block);
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        this.result = result;
    }

    public BinaryOpName getOp() {
        return op;
    }

    public void setOp(BinaryOpName op) {
        this.op = op;
    }

    public Operand getLhs() {
        return lhs;
    }

    public void setLhs(Operand lhs) {
        this.lhs = lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    public void setRhs(Operand rhs) {
        this.rhs = rhs;
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = " +
                op.name() + " " + result.getLlvMtype().toString() + " " + lhs.toString() + ", " + rhs.toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
