package IR.LLVMoperand;

import IR.Instruction.LLVMInstruction;
import IR.TypeSystem.LLVMtype;

import java.util.LinkedHashMap;
import java.util.Map;

abstract public class Operand {
    private LLVMtype llvMtype;
    private Map<LLVMInstruction, Integer> use;

    public Operand(LLVMtype llvMtype) {
        this.llvMtype = llvMtype;
        this.use = new LinkedHashMap<>();   //why linkedHashMap ? gugu changed
    }

    public void addUse(LLVMInstruction instruction){
        if(!use.containsKey(instruction)){
            use.put(instruction,1);
        }else{
            use.put(instruction, use.get(instruction) + 1);
        }
    }

    public void removeUse(LLVMInstruction instruction){
        int cnt = use.get(instruction);
        if(cnt == 1)
            use.remove(instruction);
        else
            use.replace(instruction, cnt-1);
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
