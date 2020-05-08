package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMtype;

public class BitCastInst extends LLVMInstruction{
    private Operand source;
    private LLVMtype ObjectType;
    private Register result;

    public BitCastInst(Block block, Operand source, LLVMtype objectType, Register result) {
        super(block);
        this.source = source;
        ObjectType = objectType;
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = bitcast " + source.getLlvMtype().toString() + " " + source.toString() + " to "
                + ObjectType.toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
