package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;

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
        assert addr.getLlvMtype() instanceof LLVMPointerType;
        LLVMtype valueType = ((LLVMPointerType) addr.getLlvMtype()).getBaseType();
        return "store " + valueType.toString() + " " + value.toString() + ", " +
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
