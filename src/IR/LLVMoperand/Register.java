package IR.LLVMoperand;

import IR.Instruction.LLVMInstruction;
import IR.TypeSystem.LLVMtype;

public class Register extends Operand{
    private String registerId;
    private LLVMInstruction def;

    public Register(LLVMtype llvMtype, String registerId) {
        super(llvMtype);
        this.registerId = registerId;
        this.def = null;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public LLVMInstruction getDef() {
        return def;
    }

    public void setDef(LLVMInstruction def) {
        this.def = def;
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
