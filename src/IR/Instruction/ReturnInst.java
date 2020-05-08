package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMVoidType;
import IR.TypeSystem.LLVMtype;

public class ReturnInst extends LLVMInstruction{
    private LLVMtype returnType;
    private Operand returnValue;

    public ReturnInst(Block block, LLVMtype returnType, Operand returnValue) {
        super(block);
        this.returnType = returnType;
        this.returnValue = returnValue;
    }

    @Override
    public String toString() {
        if(!(returnType instanceof LLVMVoidType))
            return "ret " + returnType.toString() + " " + returnValue.toString();
        else
            return "return " + "void";
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
