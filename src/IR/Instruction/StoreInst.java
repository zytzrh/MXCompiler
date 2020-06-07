package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;

public class StoreInst extends LLVMInstruction{
    private Operand value;
    private Operand addr;   //pointType

    public StoreInst(Block block, Operand value, Operand addr) {
        super(block);
        this.value = value;
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "store " + value.getLlvMtype().toString() + " " + value.toString() + ", " +
                addr.getLlvMtype().toString() + " " + addr.toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Operand getValue() {
        return value;
    }

    public Operand getAddr() {
        return addr;
    }
}
