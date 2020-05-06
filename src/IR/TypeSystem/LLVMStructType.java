package IR.TypeSystem;

import java.util.ArrayList;

public class LLVMStructType extends LLVMtype {
    private String className;
    private ArrayList<LLVMtype> members;

    public LLVMStructType(String className, ArrayList<LLVMtype> members) {
        this.className = className;
        this.members = members;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<LLVMtype> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<LLVMtype> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "%" + className;
    }
}
