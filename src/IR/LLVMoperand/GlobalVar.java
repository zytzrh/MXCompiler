package IR.LLVMoperand;

import IR.TypeSystem.LLVMtype;

public class GlobalVar extends Operand{
    private String GlobalId;

    public GlobalVar(LLVMtype llvMtype, String GlobalId) {
        super(llvMtype);
        this.GlobalId = GlobalId;
    }

    @Override
    public String toString() {
        return "@" + GlobalId;
    }

    @Override
    public boolean isConst() {
        return false;
    }
}
