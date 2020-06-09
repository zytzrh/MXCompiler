package IR.TypeSystem;

import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;

public class LLVMPointerType extends LLVMtype{
    private LLVMtype baseType;

    public LLVMPointerType(LLVMtype baseType) {
        this.baseType = baseType;
    }


    @Override
    public String toString() {
        return baseType.toString() + "*";
    }

    @Override
    public int getByte() {
        return 4;
    }

    @Override
    public Operand DefaultValue() {
        return new ConstNull();
    }

    public LLVMtype getBaseType() {
        return baseType;
    }

    public void setBaseType(LLVMtype baseType) {
        this.baseType = baseType;
    }
}
