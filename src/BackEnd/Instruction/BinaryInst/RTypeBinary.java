package BackEnd.Instruction.BinaryInst;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.Set;

public class RTypeBinary extends BinaryInst {
    public enum OpName {
        add, sub, mul, div, rem, sll, sra, and, or, xor, slt
    }

    private OpName op;
    private VirtualASMRegister rs2;

    public RTypeBinary(BackEnd.ASMBlock ASMBlock, OpName op,
                       VirtualASMRegister rs1, VirtualASMRegister rs2, VirtualASMRegister rd) {
        super(ASMBlock, rd, rs1);
        this.op = op;
        this.rs2 = rs2;

        this.rs2.addUse(this);
        this.addUse(this.rs2);
    }

    @Override
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {
        if (!varKill.contains(rs2))
            UEVar.add(rs2);
        super.addToUEVarAndVarKill(UEVar, varKill);
    }

    @Override
    public void replaceUse(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        if (rs2 == oldVR)
            rs2 = newVR;
        super.replaceUse(oldVR, newVR);
    }

    @Override
    public String emitCode() {
        return "\t" + op.name() + "\t" + getRd().emitCode() + ", " + getRs1().emitCode() + ", " + rs2.emitCode();
    }

    @Override
    public String toString() {
        return op + " " + getRd() + ", " + getRs1() + ", " + rs2;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}