package BackEnd.Operand.Address;

import BackEnd.Instruction.ASMInstruction;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Immediate.Immediate;

import java.util.Set;

public class BaseOffsetAddr extends Address {
    private VirtualASMRegister base;
    private Immediate offset;

    public BaseOffsetAddr(VirtualASMRegister base, Immediate offset) {
        this.base = base;
        this.offset = offset;
    }

    @Override
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {
        if (!varKill.contains(base))
            UEVar.add(base);
    }

    @Override
    public void addBaseUse(ASMInstruction use) {
        use.addUse(base);
        base.addUse(use);
    }

    @Override
    public void removeBaseUse(ASMInstruction use) {
        use.removeUse(base);
        base.removeUse(use);
    }

    @Override
    public void replaceUse(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        if (base == oldVR)
            base = newVR;
        super.replaceUse(oldVR, newVR);
    }

    @Override
    public String emitCode() {
        return offset.emitCode() + "(" + base.emitCode() + ")";
    }

    @Override
    public String toString() {
        return offset + "(" + base + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BaseOffsetAddr))
            return false;
        return base == ((BaseOffsetAddr) obj).base && offset.equals(((BaseOffsetAddr) obj).offset);
    }
}


