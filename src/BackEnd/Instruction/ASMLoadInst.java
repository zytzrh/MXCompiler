package BackEnd.Instruction;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.Address;

import java.util.Set;

public class ASMLoadInst extends ASMInstruction {
    public enum ByteSize {
        lb, lw
    }

    private VirtualASMRegister rd;
    private ByteSize byteSize;
    private Address addr;

    public ASMLoadInst(BackEnd.ASMBlock ASMBlock, VirtualASMRegister rd, ByteSize byteSize, Address addr) {
        super(ASMBlock);
        this.rd = rd;
        this.byteSize = byteSize;
        this.addr = addr;

        this.rd.addDef(this);
        this.addDef(this.rd);
        this.addr.addBaseUse(this);
    }

    public VirtualASMRegister getRd() {
        return rd;
    }

    public Address getAddr() {
        return addr;
    }

    public void removeFromBlock() {
        this.rd.removeDef(this);
        this.removeDef(this.rd);
        this.addr.removeBaseUse(this);

        rd = null;
        addr = null;
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
        addr.addToUEVarAndVarKill(UEVar, varKill);
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
        addr.replaceUse(oldVR, newVR);
        super.replaceUse(oldVR, newVR);
    }

    @Override
    public String emitCode() {
        return "\t" + byteSize.name() + "\t" + rd.emitCode() + ", " + addr.emitCode();
    }

    @Override
    public String toString() {
        return byteSize + " " + rd + ", " + addr;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
