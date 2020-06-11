package IR.LLVMoperand;

import IR.Instruction.LLVMInstruction;
import IR.TypeSystem.LLVMIntType;
import IR.TypeSystem.LLVMtype;

import java.util.Queue;
import java.util.Set;

public class ConstInt extends Operand implements Constant{
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

    @Override
    public Constant castToType(LLVMtype objectType) {
        if (objectType instanceof LLVMIntType) {
            if (((LLVMIntType) objectType).getBitWidth() == LLVMIntType.BitWidth.int1)
                return new ConstBool(value == 0);
            else if (((LLVMIntType) objectType).getBitWidth() == LLVMIntType.BitWidth.int8)
                return new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int8), value);
            else if (((LLVMIntType) objectType).getBitWidth() == LLVMIntType.BitWidth.int32)
                return new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), value);
        }
        throw new RuntimeException("ConstBool cast to " + objectType.toString());
    }


}
