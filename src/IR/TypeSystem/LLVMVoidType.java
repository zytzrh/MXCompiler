package IR.TypeSystem;

public class LLVMVoidType extends LLVMtype{
    @Override
    public String toString() {
        return "void";
    }

    @Override
    public int getByte() {
        return 0;
    }
}
