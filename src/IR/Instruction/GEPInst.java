package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;

import java.util.ArrayList;

public class GEPInst extends LLVMInstruction{
    private Operand pointer;
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

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
