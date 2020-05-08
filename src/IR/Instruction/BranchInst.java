package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;


public class BranchInst extends LLVMInstruction{
    private Operand condition;
    private Block thenBlock;
    private Block elseBlock;

    public BranchInst(Block block, Operand condition, Block thenBlock, Block elseBlock) {
        super(block);
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public String toString() {
        if(condition == null)
            return "br label " + thenBlock.toString();
        else
            return "br i1 " + condition.toString() + ", label " + thenBlock.toString() + ", label " + elseBlock.toString();
    }

    public Operand getCondition() {
        return condition;
    }

    public Block getThenBlock() {
        return thenBlock;
    }

    public Block getElseBlock() {
        return elseBlock;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
