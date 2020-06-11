package BackEnd.Operand.Address;

import BackEnd.Instruction.ASMInstruction;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.Set;

abstract public class Address {
    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {

    }

    public void addBaseUse(ASMInstruction use) {

    }

    public void removeBaseUse(ASMInstruction use) {

    }

    public void replaceUse(VirtualASMRegister oldVR, VirtualASMRegister newVR) {

    }

    abstract public String emitCode();

    @Override
    abstract public String toString();

    @Override
    abstract public boolean equals(Object obj);
}

