package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.*;

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

    @Override
    public void overrideObject(Object oldUse, Object newUse) { }

    public HashSet<MoveInst> getMoves() {
        return moves;
    }

    public void removeMove(MoveInst moveInst) {
        assert moves.contains(moveInst);
        moves.remove(moveInst);
    }

    public void setMoves(HashSet<MoveInst> moves) {
        this.moves = moves;
    }

    public void appendMove(MoveInst moveInst) {
        if (moveInst.getResult().equals(moveInst.getSource()))
            return;
        moves.add(moveInst);
    }

    public MoveInst findValidMove() {
        for (MoveInst move1 : moves) {
            boolean flag = true;
            for (MoveInst move2 : moves) {
                if (move2.getSource().equals(move1.getResult())) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                return move1;
        }
        return null;
    }

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        return false;
    }

    @Override
    public Register getResult() {
        return null;
    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        return false;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        // Do nothing.
    }

    @Override
    public LLVMInstruction makeCopy() {
        assert false;
        return null;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        //do nothing
    }
}
