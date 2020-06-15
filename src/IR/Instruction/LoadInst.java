package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMtype;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class LoadInst extends LLVMInstruction{
    private Operand addr;
    private Register result;

    public LoadInst(Block block, Operand addr, Register result) {
        super(block);
        this.addr = addr;
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = load " + result.getLlvMtype().toString() + ", "
                + addr.getLlvMtype().toString() + " " + addr.toString();
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        addr.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(addr == oldUse){
            addr.removeUse(this);
            addr = (Operand) newUse;
            addr.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Operand getAddr() {
        return addr;
    }

    public void setAddr(Operand addr) {
        this.addr = addr;
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

    public LLVMtype getType(){
        return result.getLlvMtype();
    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        if (SideEffectChecker.getOperandScope(result) == SideEffectChecker.Scope.local
                || addr instanceof ConstNull) {
            if (scopeMap.get(result) != SideEffectChecker.Scope.local) {
                scopeMap.replace(result, SideEffectChecker.Scope.local);
                return true;
            } else
                return false;
        } else {
            SideEffectChecker.Scope scope = scopeMap.get(addr);
            assert scope != SideEffectChecker.Scope.undefined;
            if (scopeMap.get(result) != scope) {
                scopeMap.replace(result, scope);
                return true;
            } else
                return false;
        }
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        addr.markBaseAsLive(live, queue);
    }

    @Override
    public LLVMInstruction makeCopy() {
        LoadInst loadInst = new LoadInst(this.getBlock(), this.addr, this.result.makeCopy());
        loadInst.result.setDef(loadInst);
        return loadInst;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        if (addr instanceof Register) {
            assert operandMap.containsKey(addr);
            addr = operandMap.get(addr);
        }
        addr.addUse(this);
    }

    @Override
    public Object clone() {
        LoadInst loadInst = (LoadInst) super.clone();
        loadInst.addr = this.addr;
        loadInst.result = (Register) this.result.clone();

        loadInst.result.setDef(loadInst);
        return loadInst;
    }
}
