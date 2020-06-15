package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.*;

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

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        for(Operand para : paras){
            para.removeUse(this);       //gugu changed: why only delete use, not delete variable def
        }
        llvMfunction.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        ListIterator<Operand> listIterator = paras.listIterator();
        while(listIterator.hasNext()){
            Operand para= listIterator.next();
            if(para == oldUse){
                para.removeUse(this);
                listIterator.set((Operand) newUse);
                ((Operand) newUse).addUse(this);
            }
        }
        if(llvMfunction == oldUse){
            llvMfunction.removeUse(this);
            llvMfunction = (LLVMfunction) newUse;
            llvMfunction.addUse(this);
        }
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

    public boolean isVoidCall() {
        return result == null;
    }

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        if (this.isVoidCall())
            return false;
        ConstOptim.Status status = constOptim.getStatus(result);
        if (status.getOperandStatus() == ConstOptim.Status.OperandStatus.constant) {
            result.beOverriden(status.getOperand());
            this.removeFromBlock();
            return true;
        } else
            return false;
    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        if (isVoidCall())
            return false;
        if (SideEffectChecker.getOperandScope(result) == SideEffectChecker.Scope.localVar) {
            if (scopeMap.get(result) != SideEffectChecker.Scope.localVar) {
                scopeMap.replace(result, SideEffectChecker.Scope.localVar);
                return true;
            } else
                return false;
        } else {
            SideEffectChecker.Scope scope = returnValueScope.get(llvMfunction);
            if (scopeMap.get(result) != scope) {
                scopeMap.replace(result, scope);
                return true;
            } else
                return false;
        }
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        for (Operand parameter : paras)
            parameter.markBaseAsLive(live, queue);
    }

    @Override
    public LLVMInstruction makeCopy() {
        Register newResult = null;
        if(this.result != null)
            newResult = this.result.makeCopy();
        CallInst callInst = new CallInst(this.getBlock(), newResult,
                this.llvMfunction, new ArrayList<>(this.paras));

        if(newResult != null)
            newResult.setDef(callInst);
        return callInst;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        for (int i = 0; i < paras.size(); i++) {
            Operand operand = paras.get(i);
            if (operand instanceof Register) {
                assert operandMap.containsKey(operand);
                paras.set(i, operandMap.get(operand));
            }
            paras.get(i).addUse(this);
        }
        llvMfunction.addUse(this);
    }

    @Override
    public Object clone() {
        CallInst callInst = (CallInst) super.clone();
        callInst.llvMfunction = this.llvMfunction;
        callInst.paras = new ArrayList<>(this.paras);
        if (this.result != null) {
            callInst.result = (Register) this.result.clone();
            callInst.result.setDef(callInst);
        } else
            callInst.result = null;

        return callInst;
    }
}
