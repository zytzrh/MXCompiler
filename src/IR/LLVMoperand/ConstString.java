package IR.LLVMoperand;

import IR.TypeSystem.LLVMtype;

public class ConstString extends Operand{
    private String value;   //the type does not concern?

    public ConstString(LLVMtype llvMtype, String value) {
        super(llvMtype);
        this.value = value;
    }

    @Override
    public String toString() {
        String text = value;
        text = text.replace("\\", "\\5C");
        text = text.replace("\n", "\\0A");
        text = text.replace("\"", "\\22");
        text = text.replace("\0", "\\00");

        return "c\"" + text + "\"";
    }

    @Override
    public boolean isConst() {
        return true;
    }
}
