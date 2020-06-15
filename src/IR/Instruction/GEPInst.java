package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.*;

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

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
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
        if (pointer instanceof ConstNull) {
            if (scopeMap.get(result) != SideEffectChecker.Scope.localVar) {
                scopeMap.replace(result, SideEffectChecker.Scope.localVar);
                return true;
            } else
                return false;
        }
        SideEffectChecker.Scope scope = scopeMap.get(pointer);
        assert scope != SideEffectChecker.Scope.undefined;
        if (scopeMap.get(result) != scope) {
            scopeMap.replace(result, scope);
            return true;
        } else
            return false;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        pointer.markBaseAsLive(live, queue);
        for (Operand operand : indexs)
            operand.markBaseAsLive(live, queue);
    }

    @Override
    public LLVMInstruction makeCopy() {
        GEPInst gepInst = new GEPInst(this.getBlock(), this.pointer, new ArrayList<>(this.indexs), this.result.makeCopy());
        this.result.setDef(gepInst);
        return gepInst;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        if (pointer instanceof Register) {
            assert operandMap.containsKey(pointer);
            pointer = operandMap.get(pointer);
        }
        pointer.addUse(this);
        for (int i = 0; i < indexs.size(); i++) {
            Operand aIndex = indexs.get(i);
            if (aIndex instanceof Register) {
                assert operandMap.containsKey(aIndex);
                indexs.set(i, operandMap.get(aIndex));
            }
            indexs.get(i).addUse(this);
        }
    }

    @Override
    public Object clone() {
        GEPInst GEPInst = (GEPInst) super.clone();
        GEPInst.pointer = this.pointer;
        GEPInst.indexs = new ArrayList<>(indexs);
        GEPInst.result = (Register) this.result.clone();

        GEPInst.result.setDef(GEPInst);
        return GEPInst;
    }


}
