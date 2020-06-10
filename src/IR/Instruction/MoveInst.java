package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;

public class MoveInst extends LLVMInstruction{
    private Operand source;
    private Register result;

    public MoveInst(Block block, Operand source, Register result) {
        super(block);
        this.source = source;
        this.result = result;

        //gugu changed: delete
//        assert source.getType().equals(result.getType())
//                || (result.getType() instanceof PointerType && source instanceof ConstNull);
//        assert source instanceof Register || source instanceof Parameter || source instanceof Constant;
    }


    @Override
    public String toString() {
        return "move" + " " + result.toString() + " " + source.toString();
    }

    @Override
    public void accept(IRVisitor irVisitor) {
        irVisitor.visit(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(source == oldUse){
            source.removeUse(this);
            source = (Operand) newUse;
            source.addUse(this);
        }
    }

    //gugu changed: no remove from block

    public Operand getSource() {
        return source;
    }

    public void setSource(Operand source) {
        this.source = source;
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }
}
