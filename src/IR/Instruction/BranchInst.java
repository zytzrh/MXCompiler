package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import Optimization.ConstOptim;


public class BranchInst extends LLVMInstruction{
    private Operand condition;
    private Block ifTrueBlock;
    private Block ifFalseBlock;

    public BranchInst(Block block, Operand condition, Block ifTrueBlock, Block ifFalseBlock) {
        super(block);
        this.condition = condition;
        this.ifTrueBlock = ifTrueBlock;
        this.ifFalseBlock = ifFalseBlock;
    }

    @Override
    public String toString() {
        if(condition == null)
            return "br label " + ifTrueBlock.toString();
        else
            return "br i1 " + condition.toString() + ", label " + ifTrueBlock.toString() + ", label " + ifFalseBlock.toString();
    }

    public Operand getCondition() {
        return condition;
    }

    public Block getIfTrueBlock() {
        return ifTrueBlock;
    }

    public Block getIfFalseBlock() {
        return ifFalseBlock;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Register getResult() {
        throw new RuntimeException("Error :Get result of branch instruction.");
    }

    public void reset2Unconditional(Block block){
        //delete original link
        Block selfBlock = this.getBlock();
        if(condition != null){
            this.condition.removeUse(this);
            this.ifFalseBlock.removeUse(this);
            selfBlock.cutSuccessor(this.ifFalseBlock);
        }
        this.ifTrueBlock.removeUse(this);
        selfBlock.cutSuccessor(this.ifTrueBlock);
        //add new link
        this.condition = null;
        this.ifTrueBlock = block;
        this.ifFalseBlock = null;
        block.addUse(this);
        block.getPredecessors().add(selfBlock);     //gugu changed: the block must be already registered in function?
        selfBlock.getSuccessors().add(block);
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        if(condition != null){
            condition.removeUse(this);
            ifFalseBlock.removeUse(this);
        }
        ifTrueBlock.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(condition == oldUse){
            condition.removeUse(this);
            condition = (Operand) newUse;
            condition.addUse(this);
        }           //gugu changed the block/operand/function use override can be separate
        if(ifTrueBlock == oldUse){
            ifTrueBlock.removeUse(this);
            ifTrueBlock = (Block) newUse;
            ifTrueBlock.addUse(this);
        }
        if(ifFalseBlock == oldUse){
            ifFalseBlock.removeUse(this);
            ifFalseBlock = (Block) newUse;
            ifFalseBlock.addUse(this);
        }
    }

    public void setCondition(Operand condition) {
        this.condition = condition;
    }

    public void setIfTrueBlock(Block ifTrueBlock) {
        this.ifTrueBlock = ifTrueBlock;
    }

    public void setIfFalseBlock(Block ifFalseBlock) {
        this.ifFalseBlock = ifFalseBlock;
    }

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        // Do nothing.
        return false;
    }
}
