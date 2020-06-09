package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;


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
}
