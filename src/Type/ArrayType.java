package Type;

public class ArrayType implements Type {
    private NonArrayType baseNonArrayType;
    private int dim;

    public ArrayType(NonArrayType baseNonArrayType, int dim) {
        this.baseNonArrayType = baseNonArrayType;
        this.dim = dim;
    }

    public int getDim(){
        return this.dim;
    }

    @Override
    public boolean equal(Type other) {
        if(other instanceof ArrayType){
            return ((ArrayType) other).baseNonArrayType.equal(this.baseNonArrayType) &&
                    ((ArrayType) other).getDim() == this.dim;
        }else{
            return false;
        }
    }

    @Override
    public boolean hasVarMember(String memberName) {
        return false;
    }

    @Override
    public Type getMemberType(String memberName) {
        return null;
    }
}
