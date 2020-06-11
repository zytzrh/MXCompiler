package BackEnd.Instruction.BinaryInst;

import BackEnd.ASMBlock;
import BackEnd.Instruction.ASMInstruction;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.Set;

abstract public class BinaryInst extends ASMInstruction {
    private VirtualASMRegister rd;
    private VirtualASMRegister rs1;

    public BinaryInst(ASMBlock ASMBlock, VirtualASMRegister rd, VirtualASMRegister rs1) {
        super(ASMBlock);
        this.rd = rd;
        this.rs1 = rs1;

        this.rd.addDef(this);
        this.rs1.addUse(this);
        this.addDef(this.rd);
        this.addUse(this.rs1);
    }

    public VirtualASMRegister getRd() {
        return rd;
    }

    public VirtualASMRegister getRs1() {
        return rs1;
    }

    @Override
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {
        if (!varKill.contains(rs1))
            UEVar.add(rs1);
        varKill.add(rd);
    }

    @Override
    public void replaceDef(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        assert rd == oldVR;
        rd = newVR;
        super.replaceDef(oldVR, newVR);
    }

    @Override
    public void replaceUse(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        if (rs1 == oldVR)
            rs1 = newVR;
        super.replaceUse(oldVR, newVR);
    }
}
