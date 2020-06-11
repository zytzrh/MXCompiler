package BackEnd.Instruction;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Immediate.Immediate;

import java.util.Set;

public class ASMLoadUpperImmediate extends ASMInstruction {
    private VirtualASMRegister rd;
    private Immediate immediate;

    public ASMLoadUpperImmediate(BackEnd.ASMBlock ASMBlock, VirtualASMRegister rd, Immediate immediate) {
        super(ASMBlock);
        this.rd = rd;
        this.immediate = immediate;

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
        return "\tlui\t" + rd.emitCode() + ", " + immediate.emitCode();
    }

    @Override
    public String toString() {
        return "lui " + rd + ", " + immediate;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}