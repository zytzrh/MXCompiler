package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;

import java.util.ArrayList;
import java.util.ListIterator;

public class GEPInst extends LLVMInstruction{
    private Operand pointer;    //gugu changed: why operand?? ans: register or GlobarVar??
    private ArrayList<Operand> indexs;
    private Register result;

    public GEPInst(Block block, Operand pointer, ArrayList<Operand> indexs, Register result) {
        super(block);
        this.pointer = pointer;
        this.indexs = indexs;
        this.result = result;
    }

    @Override
    public String toString() {
        //assert pointer.getLlvMtype() instanceof LLVMPointerType;
        LLVMtype pointerType = pointer.getLlvMtype();
        assert pointerType instanceof LLVMPointerType;
        LLVMtype baseType = ((LLVMPointerType) pointerType).getBaseType();
        StringBuilder string = new StringBuilder();
        string.append(result.toString() + " = ");
        string.append("getelementptr " + baseType.toString() + ", " +
                pointerType.toString() + " " + pointer.toString());
        for(Operand index : indexs){
            string.append(", " + index.getLlvMtype().toString() + " " + index.toString());
        }
        return string.toString();
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        pointer.removeUse(this);
        for(Operand index : indexs)
            index.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        ListIterator<Operand> listIterator = indexs.listIterator();
        while(listIterator.hasNext()){
            Operand index = listIterator.next();
            if(index == oldUse){
                index.removeUse(this);
                listIterator.set((Operand) newUse);
                ((Operand) newUse).addUse(this);
            }
        }
        if(pointer == oldUse){
            pointer.removeUse(this);
            pointer = (Operand) newUse;
            pointer.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Operand getPointer() {
        return pointer;
    }

    public void setPointer(Operand pointer) {
        this.pointer = pointer;
    }

    public ArrayList<Operand> getIndexs() {
        return indexs;
    }

    public void setIndexs(ArrayList<Operand> indexs) {
        this.indexs = indexs;
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }
}
