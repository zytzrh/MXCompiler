package IR.LLVMoperand;

import IR.Instruction.LLVMInstruction;
import IR.Instruction.ReturnInst;
import IR.TypeSystem.LLVMtype;

import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.Set;

public class Register extends Operand implements Cloneable{
    private String name;
    private LLVMInstruction def;
    private boolean isParameter;

    public Register(LLVMtype llvMtype, String name) {
        super(llvMtype);
        this.name = name;
        this.def = null;
        isParameter = false;
    }

    public Register(LLVMtype llvMtype, String name, LLVMInstruction def, boolean isParameter) {
        super(llvMtype);
        this.name = name;
        this.def = def;
        this.isParameter = isParameter;
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

    @Override
    public void markBaseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        if(isParameter())
            return;
        assert def != null;
        if (!live.contains(def)) {
            live.add(def);
            queue.offer(def);
        }
        if (!(def.getBlock().getInstTail() instanceof ReturnInst) && !live.contains(def.getBlock().getInstTail())) {
            //not exit block
            live.add(def.getBlock().getInstTail());
            queue.offer(def.getBlock().getInstTail());
        }
    }

    public Register makeCopy(){
        Register newRegister = new Register(this.getLlvMtype(), this.name, this.def, this.isParameter);
        newRegister.setUse(new LinkedHashMap<>());
        return newRegister;
    }

    @Override
    public Object clone() {
        Register register;
        try {
            register = (Register) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        register.setUse(new LinkedHashMap<>());         //gugu changed
        register.name = this.name;
        register.def = this.def;
        register.isParameter = this.isParameter;
        return register;
    }

}
