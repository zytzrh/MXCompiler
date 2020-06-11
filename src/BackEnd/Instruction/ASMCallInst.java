package BackEnd.Instruction;

import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.PhysicalASMRegister;
import BackEnd.RISCVFunction;

public class ASMCallInst extends ASMInstruction {
    private RISCVFunction callee;

    public ASMCallInst(BackEnd.ASMBlock ASMBlock, RISCVFunction callee) {
        super(ASMBlock);
        this.callee = callee;

        for (String name : PhysicalASMRegister.callerSavePRNames) {
            PhysicalASMRegister.vrs.get(name).addDef(this);
            this.addDef(PhysicalASMRegister.vrs.get(name));
        }
    }

    @Override
    public String emitCode() {
        return "\tcall\t" + callee.getName();
    }

    @Override
    public String toString() {
        return "call " + callee.getName();
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
