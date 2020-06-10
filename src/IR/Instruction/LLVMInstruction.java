package IR.Instruction;

import IR.Block;
import IR.IRVisitor;

abstract public class LLVMInstruction {
    private Block block;
    private LLVMInstruction preInst;
    private LLVMInstruction postInst;
    private String comment;

    public void removeFromBlock(){
        if(preInst == null)
            block.setInstHead(postInst);
        else
            preInst.setPostInst(postInst);

        if(postInst == null)
            block.setInstTail(preInst);
        else
            postInst.setPreInst(preInst);
    }

    abstract public void overrideObject(Object oldUse, Object newUse);

    public boolean isTerminalInst(){
        return this instanceof BranchInst || this instanceof ReturnInst;
    }

    public LLVMInstruction(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public LLVMInstruction getPreInst() {
        return preInst;
    }

    public void setPreInst(LLVMInstruction preInst) {
        this.preInst = preInst;
    }

    public LLVMInstruction getPostInst() {
        return postInst;
    }

    public void setPostInst(LLVMInstruction postInst) {
        this.postInst = postInst;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    abstract public String toString();

    abstract public void accept(IRVisitor irVisitor);
}
