package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
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

    @Override
    public void accept(IRVisitor irVisitor) {
        irVisitor.visit(this);
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
}
