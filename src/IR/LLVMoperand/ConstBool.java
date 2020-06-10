package IR.LLVMoperand;

import IR.TypeSystem.LLVMIntType;

public class ConstBool extends Operand{
    private boolean value;

    public ConstBool(boolean value) {
        super(new LLVMIntType(LLVMIntType.BitWidth.int1));
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

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
