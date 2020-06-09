package IR.TypeSystem;

import IR.LLVMoperand.Operand;

public class LLVMNullType extends LLVMtype{     //not used?
    @Override
    public String toString() {
        assert false;
        return "";
    }

    @Override
    public int getByte() {
        assert false;
        return 0;
    }

    @Override
    public Operand DefaultValue() {
        assert false;
        return null;
    }
}
