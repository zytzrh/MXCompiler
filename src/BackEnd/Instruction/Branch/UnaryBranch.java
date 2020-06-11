package BackEnd.Instruction.Branch;

import BackEnd.ASMBlock;
import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

public class UnaryBranch extends Branch {
    public enum OpName {
        beqz, bnez, bltz, bgez, blez, bgtz
    }

    private OpName op;

    public UnaryBranch(BackEnd.ASMBlock ASMBlock, OpName op, VirtualASMRegister rs1, ASMBlock thenBlock) {
        super(ASMBlock, rs1, thenBlock);
        this.op = op;
    }

    @Override
    public String emitCode() {
        return "\t" + op.name() + "\t" + getRs1().emitCode() + ", " + getThenBlock().emitCode();
    }

    @Override
    public String toString() {
        return op + " " + getRs1() + ", " + getThenBlock();
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}