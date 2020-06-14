package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;
import Optimization.Andersen;
import Optimization.CSE;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BitCastInst extends LLVMInstruction{
    private Operand source;
    private LLVMtype objectType;
    private Register result;

    public BitCastInst(Block block, Operand source, LLVMtype objectType, Register result) {
        super(block);
        this.source = source;
        this.objectType = objectType;
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = bitcast " + source.getLlvMtype().toString() + " " + source.toString() + " to "
                + objectType.toString();
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        source.removeUse(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(source == oldUse){
            source.removeUse(this);
            source = (Operand) newUse;
            source.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Operand getSource() {
        return source;
    }

    public void setSource(Operand source) {
        this.source = source;
    }

    public LLVMtype getObjectType() {
        return objectType;
    }

    public void setObjectType(LLVMtype objectType) {
        this.objectType = objectType;
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
        if (source instanceof ConstNull) {
            if (scopeMap.get(result) != SideEffectChecker.Scope.local) {
                scopeMap.replace(result, SideEffectChecker.Scope.local);
                return true;
            } else
                return false;
        }
        SideEffectChecker.Scope scope = scopeMap.get(source);
        assert scope != SideEffectChecker.Scope.undefined;
        if (scopeMap.get(result) != scope) {
            scopeMap.replace(result, scope);
            return true;
        } else
            return false;

    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        source.markBaseAsLive(live, queue);
    }

    @Override
    public CSE.Expression convertToExpression() {
        String instructionName = "bitcast";
        ArrayList<String> operands = new ArrayList<>();
        operands.add(source.toString());
        operands.add(objectType.toString());
        return new CSE.Expression(instructionName, operands);
    }

    @Override
    public void addConstraintsForAndersen(Map<Operand, Andersen.Node> nodeMap, Set<Andersen.Node> nodes) {
        assert source.getLlvMtype() instanceof LLVMPointerType && result.getLlvMtype() instanceof LLVMPointerType;
        if (!(source instanceof ConstNull)) {
            assert nodeMap.containsKey(result);
            assert nodeMap.containsKey(source);
            nodeMap.get(source).getInclusiveEdge().add(nodeMap.get(result));
        }
    }

    @Override
    public LLVMInstruction makeCopy() {
        BitCastInst bitCastInst = new BitCastInst(this.getBlock(), this.source,     //gugu changed:
                this.objectType, this.result.makeCopy());
        bitCastInst.result.setDef(bitCastInst);
        return bitCastInst;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        if (source instanceof Register) {
            assert operandMap.containsKey(source);
            source = operandMap.get(source);
        }
        source.addUse(this);
    }

    @Override
    public Object clone() {
        BitCastInst bitCastInst = ((BitCastInst) super.clone());
        bitCastInst.source = this.source;               //???????
        bitCastInst.objectType = this.objectType;
        bitCastInst.result = (Register) this.result.clone();

        bitCastInst.result.setDef(bitCastInst);
        return bitCastInst;
    }
}
