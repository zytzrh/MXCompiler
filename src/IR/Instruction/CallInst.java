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
        Operand lastPara = paras.get(paras.size()-1);
        for(Operand para : paras){
            string.append(para.getLlvMtype().toString() + " " + para.toString());
            if(para != lastPara){
                string.append(", ");
            }
        }
        string.append(")");
        return string.toString();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
