package BackEnd.Instruction.Branch;

import BackEnd.ASMBlock;
import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.Set;

public class BinaryBranchInst extends ASMBranchInst {
    public enum OpName {
        beq, bne, blt, bge, ble, bgt
    }

    private OpName op;
    private VirtualASMRegister rs2;

    public BinaryBranchInst(BackEnd.ASMBlock ASMBlock, OpName op,
                            VirtualASMRegister rs1, VirtualASMRegister rs2, ASMBlock thenBlock) {
        super(ASMBlock, rs1, thenBlock);
        this.op = op;
        this.rs2 = rs2;

        this.rs2.addUse(this);
        this.addUse(this.rs2);
    }

    @Override
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {
        super.addToUEVarAndVarKill(UEVar, varKill);
        if (!varKill.contains(rs2))
            UEVar.add(rs2);
    }

    @Override
    public void replaceUse(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        if (rs2 == oldVR)
            rs2 = newVR;
        super.replaceUse(oldVR, newVR);
    }

    @Override
    public String emitCode() {
        return "\t" + op.name() + "\t"
                + getRs1().emitCode() + ", " + rs2.emitCode() + ", " + getThenBlock().emitCode();
    }

    @Override
    public String toString() {
        return op + " " + getRs1() + ", " + rs2 + ", " + getThenBlock();
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
