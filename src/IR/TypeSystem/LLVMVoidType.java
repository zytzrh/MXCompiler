package IR.TypeSystem;

import IR.LLVMoperand.Operand;

public class LLVMVoidType extends LLVMtype{
    @Override
    public String toString() {
        return "void";
    }

    @Override
    public int getByte() {
        return 0;
    }

    @Override
    public Operand DefaultValue() {
        assert false;
        return null;
    }
}
