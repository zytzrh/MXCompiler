package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMtype;

public class AllocInst extends LLVMInstruction {
    private Register result;
    private LLVMtype llvMtype;

    public AllocInst(Block block, Register result, LLVMtype llvMtype) {
        super(block);
        this.result = result;
        this.llvMtype = llvMtype;
    }


    @Override
    public String toString() {
        return result.toString() + " = alloca " + llvMtype.toString();
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) { }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }

    public LLVMtype getLlvMtype() {
        return llvMtype;
    }

    public void setLlvMtype(LLVMtype llvMtype) {
        this.llvMtype = llvMtype;
    }


}
