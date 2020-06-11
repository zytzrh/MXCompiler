package BackEnd.Instruction;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.Set;

public class ASMMoveInst extends ASMInstruction {
    private VirtualASMRegister rd;
    private VirtualASMRegister rs;

    public ASMMoveInst(BackEnd.ASMBlock ASMBlock, VirtualASMRegister rd, VirtualASMRegister rs) {
        super(ASMBlock);
        this.rd = rd;
        this.rs = rs;

        this.rs.addUse(this);
        this.rd.addDef(this);
        this.addUse(this.rs);
        this.addDef(this.rd);
    }

    public VirtualASMRegister getRd() {
        return rd;
    }

    public VirtualASMRegister getRs() {
        return rs;
    }

    public void removeFromBlock() {
        this.rs.removeUse(this);
        this.rd.removeDef(this);
        this.removeUse(this.rs);
        this.removeDef(this.rd);

        rs = null;
        rd = null;
        if (getPrevInst() == null)
            getASMBlock().setInstHead(getNextInst());
        else
            getPrevInst().setNextInst(getNextInst());
        if (getNextInst() == null)
            getASMBlock().setInstTail(getPrevInst());
        else
            getNextInst().setPrevInst(getPrevInst());
    }

    @Override
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {
        if (!varKill.contains(rs))
            UEVar.add(rs);
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
        assert rs == oldVR;
        rs = newVR;
        super.replaceUse(oldVR, newVR);
    }

    @Override
    public String emitCode() {
        return "\tmv\t" + rd.emitCode() + ", " + rs.emitCode();
    }

    @Override
    public String toString() {
        return "mv " + rd + ", " + rs;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
