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
            return "ret " + "void";
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        if(returnValue != null)
            returnValue.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(returnValue == oldUse){
            returnValue.removeUse(this);
            returnValue = (Operand) newUse;
            returnValue.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public LLVMtype getReturnType() {
        return returnType;
    }

    public Operand getReturnValue() {
        return returnValue;
    }
}
