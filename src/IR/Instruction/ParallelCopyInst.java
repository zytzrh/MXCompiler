package IR.Instruction;

import IR.Block;
import IR.IRVisitor;

import java.util.HashSet;
import java.util.Iterator;
//gugu changed can be changed totally
public class ParallelCopyInst extends LLVMInstruction{
    private HashSet<MoveInst> moves;

    public ParallelCopyInst(Block block) {
        super(block);
        this.moves = new HashSet<MoveInst>();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("parallelCopy" + " ");
        Iterator<MoveInst> it = moves.iterator();
        while (it.hasNext()) {                      //gugu changed:the first??
            MoveInst move = it.next();
            string.append("[ " + move.getResult() + ", " + move.getSource() + " ]");
            if (it.hasNext())
                string.append(", ");
        }
        return string.toString();
    }

    @Override
    public void accept(IRVisitor irVisitor) {
        irVisitor.visit(this);
    }


}
