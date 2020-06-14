package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import Optimization.ConstOptim;
import Optimization.Loop.LoopAnalysis;
import Optimization.SideEffectChecker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

abstract public class LLVMInstruction implements Cloneable{
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

    public boolean hasResult() {
        if (this instanceof CallInst)
            return !((CallInst) this).isVoidCall();
        return this instanceof AllocInst
                || this instanceof BinaryOpInst
                || this instanceof BitCastInst
                || this instanceof GEPInst
                || this instanceof IcmpInst
                || this instanceof LoadInst
                || this instanceof PhiInst
                || this instanceof MoveInst;
    }

    abstract public boolean replaceResultWithConstant(ConstOptim constOptim);


    abstract public Register getResult();

    abstract public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap,
                                              Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope);

    abstract public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue);

    public boolean dceRemoveFromBlock(LoopAnalysis loopAnalysis) {
        removeFromBlock();
        return true;
    }

    abstract public LLVMInstruction makeCopy();

    abstract public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap);

    @Override
    public Object clone() {
        LLVMInstruction instruction;
        try {
            instruction = (LLVMInstruction) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

//        instruction.block = this.block;
//        instruction.preInst = this.preInst;
//        instruction.postInst = this.postInst;
        return instruction;
    }
}
