package Type;

public interface Type {
    public boolean equal(Type other);
    public boolean hasVarMember(String memberName);
    public Type getMemberType(String memberName);
}
