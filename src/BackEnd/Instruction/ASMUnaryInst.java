package BackEnd.Instruction;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.Set;

public class ASMUnaryInst extends ASMInstruction {
    public enum OpName {
        seqz, snez, sltz, sgtz
    }

    private OpName op;
    private VirtualASMRegister rs;
    private VirtualASMRegister rd;

    public ASMUnaryInst(BackEnd.ASMBlock ASMBlock, OpName op, VirtualASMRegister rs, VirtualASMRegister rd) {
        super(ASMBlock);
        this.op = op;
        this.rs = rs;
        this.rd = rd;

        this.rs.addUse(this);
        this.rd.addDef(this);
        this.addUse(this.rs);
        this.addDef(this.rd);
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
        return "\t" + op.name() + "\t" + rd.emitCode() + ", " + rs.emitCode();
    }

    @Override
    public String toString() {
        return op + " " + rd + ", " + rs;
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}