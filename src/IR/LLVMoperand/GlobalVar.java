package IR.LLVMoperand;

import IR.TypeSystem.LLVMtype;

public class GlobalVar extends Operand{
    private String name;

    public GlobalVar(LLVMtype llvMtype, String name) {
        super(llvMtype);
        this.name = name;
    }

    @Override
    public String toString() {
        return "@" + name;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
