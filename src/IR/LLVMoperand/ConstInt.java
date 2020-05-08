package IR.LLVMoperand;

import IR.TypeSystem.LLVMtype;

public class ConstInt extends Operand{
    private long value;

    public ConstInt(LLVMtype llvMtype, long value) {
        super(llvMtype);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean isConst() {
        return true;
    }

    public long getValue() {
        return value;
    }
}
