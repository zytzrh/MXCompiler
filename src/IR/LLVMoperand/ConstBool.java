package IR.LLVMoperand;

import IR.TypeSystem.LLVMIntType;
import IR.TypeSystem.LLVMtype;

public class ConstBool extends Operand implements Constant{
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

    @Override
    public Constant castToType(LLVMtype objectType) {
        if (objectType instanceof LLVMIntType) {
            if (((LLVMIntType) objectType).getBitWidth() == LLVMIntType.BitWidth.int1)
                return new ConstBool(value);
            else if (((LLVMIntType) objectType).getBitWidth() == LLVMIntType.BitWidth.int8)
                return new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int8), value ? 1 : 0);
            else if (((LLVMIntType) objectType).getBitWidth() == LLVMIntType.BitWidth.int32)
                return new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), value ? 1 : 0);
        }
        throw new RuntimeException("ConstBool cast to " + objectType.toString());
    }
}
