package IR.TypeSystem;

public class LLVMIntType extends LLVMtype{
    public enum BitWidth{
        int1, int8, int32
    }
    private BitWidth bitWidth;

    public LLVMIntType(BitWidth bitWidth) {
        this.bitWidth = bitWidth;
    }

    public BitWidth getBitWidth() {
        return bitWidth;
    }

    public void setBitWidth(BitWidth bitWidth) {
        this.bitWidth = bitWidth;
    }

    @Override
    public String toString() {
        switch (bitWidth){
            case int1:
                return "i1";
            case int8:
                return "i8";
            default:
                return "i32";
        }
    }

    @Override
    public int getByte() {
        if(bitWidth == BitWidth.int32)
            return 4;
        else
            return 1;
    }
}
