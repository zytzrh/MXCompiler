package BackEnd.Instruction;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.Address;

import java.util.Set;

public class ASMStoreInst extends ASMInstruction {
    public enum ByteType {
        sb, sw  //32bit and 8bit
    }

    private VirtualASMRegister rs;
    private ByteType byteType;
    private Address addr;

    public ASMStoreInst(BackEnd.ASMBlock ASMBlock, VirtualASMRegister rs, ByteType byteType, Address addr) {
        super(ASMBlock);
        this.rs = rs;
        this.byteType = byteType;
        this.addr = addr;

        this.rs.addUse(this);
        this.addUse(this.rs);
        this.addr.addBaseUse(this);
    }

    public VirtualASMRegister getRs() {
        return rs;
    }

    public Address getAddr() {
        return addr;
    }

    @Override
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {
        if (!varKill.contains(rs))
            UEVar.add(rs);
        addr.addToUEVarAndVarKill(UEVar, varKill);
    }

    @Override
    public void replaceUse(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        if (rs == oldVR)
            rs = newVR;
        addr.replaceUse(oldVR, newVR);
        super.replaceUse(oldVR, newVR);
    }

    @Override
    public String emitCode() {
        return "\t" + byteType.name() + "\t" + rs.emitCode() + ", " + addr.emitCode();
    }

    @Override
    public String toString() {
        return byteType + " " + rs + ", " + addr;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}

