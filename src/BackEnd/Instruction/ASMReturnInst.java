package BackEnd.Instruction;

import BackEnd.ASMVisitor;

public class ASMReturnInst extends ASMInstruction {
    public ASMReturnInst(BackEnd.ASMBlock ASMBlock) {
        super(ASMBlock);
    }

    @Override
    public String emitCode() {
        return "\tret";
    }

    @Override
    public String toString() {
        return "ret";
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
