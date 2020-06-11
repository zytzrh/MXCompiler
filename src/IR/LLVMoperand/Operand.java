package IR.LLVMoperand;

import IR.Instruction.BinaryOpInst;
import IR.Instruction.BranchInst;
import IR.Instruction.LLVMInstruction;
import IR.TypeSystem.LLVMtype;

import java.util.ArrayList;
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

    public void beOverriden(Object newUse){
        ArrayList<LLVMInstruction> instructions = new ArrayList<>(use.keySet());
        for(LLVMInstruction instruction : instructions){
            instruction.overrideObject(this, newUse);
        }
        use.clear();
    }

    public String getName(){
        return null;
    }

    abstract public String toString();

    abstract public boolean isConst();


    public LLVMtype getLlvMtype() {
        return llvMtype;
    }

    public void setLlvMtype(LLVMtype llvMtype) {
        this.llvMtype = llvMtype;
    }

    public Map<LLVMInstruction, Integer> getUse() {
        return use;
    }

    public void setUse(Map<LLVMInstruction, Integer> use) {
        this.use = use;
    }

    public boolean onlyHaveOneBranchUse() {
        if (use.size() > 1)
            return false;
        for (Map.Entry<LLVMInstruction, Integer> entry : use.entrySet())
            if (!(entry.getKey() instanceof BranchInst) || entry.getValue() > 1)
                return false;
        return true;
    }

    public int getPrivilege() {
        if (this instanceof Register && ((Register) this).isParameter())
            return 3;
        if (this instanceof Constant)
            return 0;
        assert this instanceof Register;
        if (!(((Register) this).getDef() instanceof BinaryOpInst))
            return 3;

        BinaryOpInst def = ((BinaryOpInst) ((Register) this).getDef());
        if (def.isIntegerNot() || def.isNegative())
            return 1;
        else
            return 2;
    }
}
