package BackEnd.Instruction.Branch;

import BackEnd.ASMBlock;
import BackEnd.Instruction.ASMInstruction;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.Set;

abstract public class ASMBranchInst extends ASMInstruction {
    private VirtualASMRegister rs1;
    private BackEnd.ASMBlock thenBlock;

    public ASMBranchInst(ASMBlock ASMBlock, VirtualASMRegister rs1, ASMBlock thenBlock) {
        super(ASMBlock);
        this.rs1 = rs1;
        this.thenBlock = thenBlock;

        this.rs1.addUse(this);
        this.addUse(this.rs1);
    }

    public VirtualASMRegister getRs1() {
        return rs1;
    }

    public ASMBlock getThenBlock() {
        return thenBlock;
    }

    @Override
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {
        if (!varKill.contains(rs1))
            UEVar.add(rs1);
    }

    @Override
    public void replaceUse(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        if (rs1 == oldVR)
            rs1 = newVR;
        super.replaceUse(oldVR, newVR);
    }
}
