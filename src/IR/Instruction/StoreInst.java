package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;
import Optimization.ConstOptim;

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

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        value.removeUse(this);
        addr.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(value == oldUse){
            value.removeUse(this);
            value = (Operand) newUse;
            value.addUse(this);
        }
        if(addr == oldUse){
            addr.removeUse(this);
            addr = (Operand) newUse;
            addr.addUse(this);
        }
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

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        // Do nothing.
        return false;
    }

    @Override
    public Register getResult() {
        throw new RuntimeException("Get result of store instruction");
    }
}
