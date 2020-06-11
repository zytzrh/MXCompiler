package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import Optimization.ConstOptim;
import Utility.Pair;

import java.util.Set;

public class PhiInst extends LLVMInstruction {
    private Set<Pair<Operand, Block>> branches;   //gugu changde: Set can be changed
    private Register result;

    public PhiInst(Block block, Set<Pair<Operand, Block>> branches, Register result) {
        super(block);
        this.branches = branches;
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(result.toString() + " = phi " + result.getLlvMtype().toString() + " ");
//        //gugu changed: replaced
//        Iterator<Pair<Operand, Block>> iterator = branches.iterator();
//        while(iterator.hasNext()){
//            Pair<Operand, Block> branch = iterator.next();
//            Operand operand = branch.getFirst();
//            Block block = branch.getSecond();
//            string.append("[ " + operand.toString() + ", " + block.toString() + " ]");
//            if (iterator.hasNext())
//                string.append(", ");
//        }
        int size = branches.size();
        int cnt = 0;
        for (Pair<Operand, Block> pair : branches) {
            Operand operand = pair.getFirst();
            Block block = pair.getSecond();
            string.append("[ " + operand.toString() + ", " + block.toString() + " ]");
            if (++cnt != size)
                string.append(", ");
        }
        return string.toString();
    }

    public void cutBlock(Block block){
        for(Pair<Operand, Block> branch : this.branches){
            if(branch.getSecond() == block){
                cutBranch(branch);
                break;
            }
        }
    }

    public void cutBranch(Pair<Operand, Block> branch){             //gugu changed: maybe can be combined with cutBlock
        Operand operand = branch.getFirst();
        Block block = branch.getSecond();
        operand.removeUse(this);
        block.removeUse(this);
        this.branches.remove(branch);
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        for(Pair<Operand, Block> branch : branches){
            Operand operand = branch.getFirst();
            Block block = branch.getSecond();
            operand.removeUse(this);
            block.removeUse(this);
        }
    }

    @Override
    public void accept(IRVisitor irVisitor) {
        irVisitor.visit(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        for(Pair<Operand, Block> branch : branches){
            if(branch.getFirst() == oldUse){
                branch.getFirst().removeUse(this);
                branch.setFirst((Operand) newUse);
                branch.getFirst().addUse(this);
            }
            if(branch.getSecond() == oldUse){
                branch.getSecond().removeUse(this);
                branch.setSecond((Block) newUse);
                branch.getSecond().addUse(this);
            }
        }
    }

    public Set<Pair<Operand, Block>> getBranches() {
        return branches;
    }

    public void setBranches(Set<Pair<Operand, Block>> branches) {
        this.branches = branches;
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }

    public void addBranch(Operand operand, Block block) {
        branches.add(new Pair<>(operand, block));
        operand.addUse(this);
        block.addUse(this);
    }

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        ConstOptim.Status status = constOptim.getStatus(result);
        if (status.getOperandStatus() == ConstOptim.Status.OperandStatus.constant) {
            result.beOverriden(status.getOperand());
            this.removeFromBlock();
            return true;
        } else
            return false;
    }
}
