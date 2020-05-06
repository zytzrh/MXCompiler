package IR.TypeSystem;

public class LLVMPointerType extends LLVMtype{
    private LLVMtype baseType;

    public LLVMPointerType(LLVMtype baseType) {
        this.baseType = baseType;
    }

    public LLVMtype getBaseType() {
        return baseType;
    }

    public void setBaseType(LLVMtype baseType) {
        this.baseType = baseType;
    }

    @Override
    public String toString() {
        return baseType.toString() + "*";
    }
}
