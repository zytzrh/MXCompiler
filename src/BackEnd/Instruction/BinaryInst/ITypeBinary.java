package BackEnd.Instruction.BinaryInst;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Immediate.Immediate;

public class ITypeBinary extends BinaryInst {
    public enum OpName {
        addi, slli, srai, andi, ori, xori, slti
    }

    private OpName op;
    private Immediate immediate;

    public ITypeBinary(BackEnd.ASMBlock ASMBlock, OpName op,
                       VirtualASMRegister rs1, Immediate immediate, VirtualASMRegister rd) {
        super(ASMBlock, rd, rs1);
        this.op = op;
        this.immediate = immediate;
    }

    @Override
    public String emitCode() {
        return "\t" + op.name() + "\t"
                + getRd().emitCode() + ", " + getRs1().emitCode() + ", " + immediate.emitCode();
    }

    @Override
    public String toString() {
        return op + " " + getRd() + ", " + getRs1() + ", " + immediate;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}