package IR.LLVMoperand;

import IR.TypeSystem.LLVMtype;

public interface Constant{
    abstract public Constant castToType(LLVMtype objectType);
}
