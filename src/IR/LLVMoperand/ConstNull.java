package IR.LLVMoperand;

import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMVoidType;
import IR.TypeSystem.LLVMtype;

public class ConstNull extends Operand implements Constant{
    public ConstNull() {
        super( new LLVMPointerType(new LLVMVoidType()));
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public Constant castToType(LLVMtype objectType) {
        if (objectType instanceof LLVMPointerType)
            return new ConstNull();
        throw new RuntimeException("ConstNull cast to " + objectType.toString());
    }
}
