package IR.LLVMoperand;

import IR.Instruction.LLVMInstruction;
import IR.TypeSystem.LLVMtype;

public class Register extends Operand{
    private String name;
    private LLVMInstruction def;
    private boolean isParameter;

    public Register(LLVMtype llvMtype, String name) {
        super(llvMtype);
        this.name = name;
        this.def = null;
        isParameter = false;
    }


    @Override
    public String toString() {
        return "%" + name;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LLVMInstruction getDef() {
        return def;
    }

    public void setDef(LLVMInstruction def) {
        this.def = def;
    }

    public String getNameWithoutDot() {
        if (name.contains(".")) {
            String[] strings = name.split("\\.");
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < strings.length - 2; i++)
                res.append(strings[i]).append('.');
            res.append(strings[strings.length - 2]);
            return res.toString();
        } else
            throw new RuntimeException();
    }

    public boolean isParameter() {
        return isParameter;
    }

    public void setParameter(boolean parameter) {
        isParameter = parameter;
    }
}
