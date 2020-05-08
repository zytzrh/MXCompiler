package IR.TypeSystem;

import java.util.ArrayList;
import java.util.HashMap;

public class LLVMStructType extends LLVMtype {
    private String className;
    private ArrayList<LLVMtype> members;
    private HashMap<String, Integer> memberIndexMap;

    public LLVMStructType(String className, ArrayList<LLVMtype> members, HashMap<String, Integer> memberIndexMap) {
        this.className = className;
        this.members = members;
        this.memberIndexMap = memberIndexMap;
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

    public HashMap<String, Integer> getMemberIndexMap() {
        return memberIndexMap;
    }

    public void setMemberIndexMap(HashMap<String, Integer> memberIndexMap) {
        this.memberIndexMap = memberIndexMap;
    }

    @Override
    public String toString() {
        return "%" + className;
    }

    @Override
    public int getByte() {
        int size = 0;
        int max = 0;
        for (LLVMtype member : members) {
            int typeSize = member.getByte();
            size = align(size, typeSize) + typeSize;
            max = Math.max(max, typeSize);
        }
        size = align(size, max);
        return size;
    }

    private int align(int size, int base){
        if(base == 0)
            return 0;
        if(size % base == 0)
            return size;
        else
            return size + (base - (size % base));
    }
    public String printInnerStructure() {
        StringBuilder string = new StringBuilder(this.toString());
        string.append(" = type { ");
        for (int i = 0; i < members.size(); i++) {
            string.append(members.get(i).toString());
            if (i != members.size() - 1)
                string.append(", ");
        }
        string.append(" }");
        return string.toString();
    }
}
