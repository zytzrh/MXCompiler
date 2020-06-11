package BackEnd.Instruction;

import BackEnd.ASMBlock;
import BackEnd.ASMVisitor;

public class ASMJumpInst extends ASMInstruction {
    private BackEnd.ASMBlock dest;

    public ASMJumpInst(ASMBlock ASMBlock, ASMBlock dest) {
        super(ASMBlock);
        this.dest = dest;
    }

    public ASMBlock getDest() {
        return dest;
    }

    public void setDest(ASMBlock dest) {
        this.dest = dest;
    }

    @Override
    public String emitCode() {
        assert dest != null;
        return "\tj\t" + dest.emitCode();
    }

    @Override
    public String toString() {
        return "j " + dest;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}

