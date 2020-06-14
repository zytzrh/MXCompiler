package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;
import Optimization.PointerAnalysis;
import Optimization.CSE;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class StoreInst extends LLVMInstruction{
    private Operand value;
    private Operand addr;   //pointType

    public StoreInst(Block block, Operand value, Operand addr) {
        super(block);
        this.value = value;
        this.addr = addr;
    }

    @Override
    public String toString() {
        assert addr.getLlvMtype() instanceof LLVMPointerType;
        LLVMtype valueType = ((LLVMPointerType) addr.getLlvMtype()).getBaseType();
        return "store " + valueType.toString() + " " + value.toString() + ", " +
                addr.getLlvMtype().toString() + " " + addr.toString();
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        value.removeUse(this);
        addr.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(value == oldUse){
            value.removeUse(this);
            value = (Operand) newUse;
            value.addUse(this);
        }
        if(addr == oldUse){
            addr.removeUse(this);
            addr = (Operand) newUse;
            addr.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Operand getValue() {
        return value;
    }

    public Operand getAddr() {
        return addr;
    }

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        // Do nothing.
        return false;
    }

    @Override
    public Register getResult() {
        throw new RuntimeException("Get result of store instruction");
    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        return false;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        value.markBaseAsLive(live, queue);
        addr.markBaseAsLive(live, queue);
    }

    @Override
    public void addConstraintsForAndersen(Map<Operand, PointerAnalysis.Node> nodeMap, Set<PointerAnalysis.Node> nodes) {
        if (!(value.getLlvMtype() instanceof LLVMPointerType))
            return;
        if (!(addr instanceof ConstNull) && !(value instanceof ConstNull)) {
            assert nodeMap.containsKey(addr);
            assert nodeMap.containsKey(value);
            nodeMap.get(addr).getDereferenceRhs().add(nodeMap.get(value));
        }
    }

    @Override
    public LLVMInstruction makeCopy() {
        StoreInst storeInst = new StoreInst(this.getBlock(), this.value, this.addr);
        return storeInst;
    }

    @Override
    public CSE.Expression convertToExpression() {
        assert false;
        return null;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        if (value instanceof Register) {
            assert operandMap.containsKey(value);
            value = operandMap.get(value);
        }
        if (addr instanceof Register) {
            assert operandMap.containsKey(addr);
            addr = operandMap.get(addr);
        }
        value.addUse(this);
        addr.addUse(this);
    }

    @Override
    public Object clone() {
        StoreInst storeInst = (StoreInst) super.clone();
        storeInst.value = this.value;
        storeInst.addr = this.addr;

        return storeInst;
    }
}
