package IR.LLVMoperand;

import IR.TypeSystem.LLVMtype;

abstract public class Operand {
    private LLVMtype llvMtype;

    public Operand(LLVMtype llvMtype) {
        this.llvMtype = llvMtype;
    }

    public LLVMtype getLlvMtype() {
        return llvMtype;
    }

    public void setLlvMtype(LLVMtype llvMtype) {
        this.llvMtype = llvMtype;
    }

    abstract public String toString();

    abstract public boolean isConst();
}
