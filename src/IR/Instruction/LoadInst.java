package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;

public class LoadInst extends LLVMInstruction{
    private Operand addr;
    private Register result;

    public LoadInst(Block block, Operand addr, Register result) {
        super(block);
        this.addr = addr;
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = load " + result.getLlvMtype().toString() + ", "
                + addr.getLlvMtype().toString() + " " + addr.toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Operand getAddr() {
        return addr;
    }

    public void setAddr(Operand addr) {
        this.addr = addr;
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }
}
