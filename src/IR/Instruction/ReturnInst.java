package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMVoidType;
import IR.TypeSystem.LLVMtype;
import Optimization.PointerAnalysis;
import Optimization.CSE;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ReturnInst extends LLVMInstruction{
    private LLVMtype returnType;
    private Operand returnValue;

    public ReturnInst(Block block, LLVMtype returnType, Operand returnValue) {
        super(block);
        this.returnType = returnType;
        this.returnValue = returnValue;
    }

    @Override
    public String toString() {
        if(!(returnType instanceof LLVMVoidType))
            return "ret " + returnType.toString() + " " + returnValue.toString();
        else
            return "ret " + "void";
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        if(returnValue != null)
            returnValue.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(returnValue == oldUse){
            returnValue.removeUse(this);
            returnValue = (Operand) newUse;
            returnValue.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public LLVMtype getReturnType() {
        return returnType;
    }

    public Operand getReturnValue() {
        return returnValue;
    }

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        // Do nothing.
        return false;
    }

    @Override
    public Register getResult() {
        throw new RuntimeException("Get result of return instruction.");
    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        return false;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        if (returnValue != null)
            returnValue.markBaseAsLive(live, queue);
    }

    @Override
    public void addConstraintsForAndersen(Map<Operand, PointerAnalysis.Node> nodeMap, Set<PointerAnalysis.Node> nodes) {

    }

    @Override
    public CSE.Expression convertToExpression() {
        assert false;
        return null;
    }

    @Override
    public LLVMInstruction makeCopy() {
        ReturnInst returnInst = new ReturnInst(this.getBlock(), this.returnType, this.returnValue);
        return returnInst;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        if (!(returnType instanceof LLVMVoidType)) {
            if (returnValue instanceof Register) {
                assert operandMap.containsKey(returnValue);
                returnValue = operandMap.get(returnValue);
            }
            returnValue.addUse(this);
        }
    }


    @Override
    public Object clone() {
        ReturnInst returnInst = (ReturnInst) super.clone();
        returnInst.returnType = this.returnType;
        returnInst.returnValue = this.returnValue;

        return returnInst;
    }
}
