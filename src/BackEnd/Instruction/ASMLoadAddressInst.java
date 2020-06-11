package BackEnd.Instruction;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMGlobalVar;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.Set;

public class ASMLoadAddressInst extends ASMInstruction {
    private VirtualASMRegister rd;
    private BackEnd.Operand.ASMGlobalVar ASMGlobalVar;

    public ASMLoadAddressInst(BackEnd.ASMBlock ASMBlock, VirtualASMRegister rd, ASMGlobalVar ASMGlobalVar) {
        super(ASMBlock);
        this.rd = rd;
        this.ASMGlobalVar = ASMGlobalVar;

        this.rd.addDef(this);
        this.addDef(this.rd);
    }

    @Override
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {
        varKill.add(rd);
    }

    @Override
    public void replaceDef(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        assert rd == oldVR;
        rd = newVR;
        super.replaceDef(oldVR, newVR);
    }

    @Override
    public String emitCode() {
        return "\tla\t" + rd.emitCode() + ", " + ASMGlobalVar.getName();
    }

    @Override
    public String toString() {
        return "la " + rd + ", " + ASMGlobalVar.getName();
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}