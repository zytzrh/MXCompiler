package Type;


import java.util.HashMap;

abstract public class NonArrayType implements Type{
    private String name;
    private HashMap<String, Type> varMember;
    public NonArrayType(String name, HashMap<String, Type> varMember){
        this.name = name;
        this.varMember = varMember;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public boolean equal(Type other) {
        if(other instanceof NonArrayType){
            return ((NonArrayType) other).getName().equals(this.name);
        }else{
            return false;
        }
    }

    @Override
    public boolean hasVarMember(String memberName) {
        return varMember.containsKey(memberName);
    }

    @Override
    public Type getMemberType(String memberName) {
        return varMember.get(memberName);
    }
}
