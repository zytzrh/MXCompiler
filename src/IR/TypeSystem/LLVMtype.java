package IR.TypeSystem;

import IR.LLVMoperand.Operand;

public abstract class LLVMtype {
    abstract public String toString();
    abstract public int getByte();
    abstract public Operand DefaultValue();
}
