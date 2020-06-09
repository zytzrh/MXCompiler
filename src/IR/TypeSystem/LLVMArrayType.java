package IR.TypeSystem;

import IR.LLVMoperand.Operand;

public class LLVMArrayType extends LLVMtype {
    private int length;
    private LLVMtype baseType;

    public LLVMArrayType(int length, LLVMtype baseType) {
        this.length = length;
        this.baseType = baseType;
    }

    @Override
    public int getByte() {
        return baseType.getByte() * length;
    }

    @Override
    public String toString() {
        return "[" + length + " " + "x" + " " + baseType.toString() +  "]";
    }

    @Override
    public Operand DefaultValue() {
        assert false;
        return null;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public LLVMtype getBaseType() {
        return baseType;
    }

    public void setBaseType(LLVMtype baseType) {
        this.baseType = baseType;
    }


}
