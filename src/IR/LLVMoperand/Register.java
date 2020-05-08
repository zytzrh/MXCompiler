package IR.LLVMoperand;

import IR.TypeSystem.LLVMtype;

public class Register extends Operand{
    private String registerId;

    public Register(LLVMtype llvMtype, String registerId) {
        super(llvMtype);
        this.registerId = registerId;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    @Override
    public String toString() {
        return "%" + registerId;
    }

    @Override
    public boolean isConst() {
        return false;
    }
}
