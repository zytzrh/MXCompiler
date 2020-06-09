package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;

import java.util.ArrayList;

public class CallInst extends LLVMInstruction {
    private Register result;
    private LLVMfunction llvMfunction;
    private ArrayList<Operand> paras;

    public CallInst(Block block, Register result, LLVMfunction llvMfunction, ArrayList<Operand> paras) {
        super(block);
        this.result = result;
        this.llvMfunction = llvMfunction;
        this.paras = paras;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        if(result != null){
            string.append(result.toString() + " = ");
        }
        string.append("call " + llvMfunction.getResultType().toString() + " " + "@" +
                llvMfunction.getFunctionName() + "(");
        for (int i = 0; i < paras.size(); i++) {
            string.append(paras.get(i).getLlvMtype().toString()).append(" ")
                    .append(paras.get(i).toString());
            if (i != paras.size() - 1)
                string.append(", ");
        }
        string.append(")");
        return string.toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }

    public LLVMfunction getLlvMfunction() {
        return llvMfunction;
    }

    public void setLlvMfunction(LLVMfunction llvMfunction) {
        this.llvMfunction = llvMfunction;
    }

    public ArrayList<Operand> getParas() {
        return paras;
    }

    public void setParas(ArrayList<Operand> paras) {
        this.paras = paras;
    }
}
