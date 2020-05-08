package IR.LLVMoperand;

import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMVoidType;

public class ConstNull extends Operand{
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
}
