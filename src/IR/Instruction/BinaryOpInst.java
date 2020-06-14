package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstInt;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMIntType;
import Optimization.Andersen;
import Optimization.CSE;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BinaryOpInst extends LLVMInstruction{
    public enum BinaryOpName {
        add, sub, mul, sdiv, srem,          // Binary Operations
        shl, ashr, and, or, xor             // Bitwise Binary Operations
    }

    private BinaryOpName op;
    private Operand lhs;
    private Operand rhs;
    private Register result;

    public BinaryOpInst(Block block, BinaryOpName op, Operand lhs, Operand rhs, Register result) {
        super(block);
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        this.result = result;
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        lhs.removeUse(this);
        rhs.removeUse(this);
    }

    @Override
    public String toString() {
        return result.toString() + " = " +
                op.name() + " " + result.getLlvMtype().toString() + " " + lhs.toString() + ", " + rhs.toString();
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        if(lhs == oldUse){
            lhs.removeUse(this);
            lhs = (Operand) newUse;
            lhs.addUse(this);
        }
        if(rhs == oldUse){
            rhs.removeUse(this);
            rhs = (Operand) newUse;
            rhs.addUse(this);
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public BinaryOpName getOp() {
        return op;
    }

    public void setOp(BinaryOpName op) {
        this.op = op;
    }

    public Operand getLhs() {
        return lhs;
    }

    public void setLhs(Operand lhs) {
        this.lhs = lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    public void setRhs(Operand rhs) {
        this.rhs = rhs;
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

    public boolean shouldSwapOperands() {
        // add, sub, mul, sdiv, srem,          // Binary Operations
        //        shl, ashr, and, or, xor             // Bitwise Binary Operations
        return (op == BinaryOpName.add
                || op == BinaryOpName.mul || op == BinaryOpName.and
                || op == BinaryOpName.or || op == BinaryOpName.xor)
                && lhs.getPrivilege() < rhs.getPrivilege();
    }

    public boolean isIntegerNot() {
        return op == BinaryOpName.xor
                && (lhs.equals(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), -1))
                || rhs.equals(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), -1)));
    }

    public boolean isNegative() {
        return op == BinaryOpName.sub && lhs.equals(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 0));
    }

    public void swapOperands() {
        Operand tmp = lhs;
        lhs = rhs;
        rhs = tmp;
    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        if (scopeMap.get(result) != SideEffectChecker.Scope.local) {
            scopeMap.replace(result, SideEffectChecker.Scope.local);
            return true;
        } else
            return false;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        lhs.markBaseAsLive(live, queue);
        rhs.markBaseAsLive(live, queue);
    }

    @Override
    public LLVMInstruction makeCopy() {
        BinaryOpInst binaryOpInst =  new BinaryOpInst(this.getBlock(), this.op, this.lhs, this.rhs, this.result);
        binaryOpInst.result.setDef(binaryOpInst);
        return binaryOpInst;
    }

    @Override
    public void addConstraintsForAndersen(Map<Operand, Andersen.Node> nodeMap, Set<Andersen.Node> nodes) {

    }

    @Override
    public CSE.Expression convertToExpression() {
        String instructionName = op.name();
        ArrayList<String> operands = new ArrayList<>();
        operands.add(lhs.toString());
        operands.add(rhs.toString());
        return new CSE.Expression(instructionName, operands);
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        if (lhs instanceof Register) {
            assert operandMap.containsKey(lhs);
            lhs = operandMap.get(lhs);
        }
        if (rhs instanceof Register) {
            assert operandMap.containsKey(rhs);
            rhs = operandMap.get(rhs);
        }
        lhs.addUse(this);
        rhs.addUse(this);
    }

    @Override
    public Object clone() {
        BinaryOpInst binaryOpInst = (BinaryOpInst) super.clone();
        binaryOpInst.op = this.op;
        binaryOpInst.lhs = this.lhs;
        binaryOpInst.rhs = this.rhs;
        binaryOpInst.result = (Register) this.result.clone();

        binaryOpInst.result.setDef(binaryOpInst);
        return binaryOpInst;
    }
}
