package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import Optimization.ConstOptim;
import Optimization.SideEffectChecker;
import Utility.Pair;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class PhiInst extends LLVMInstruction {
    private Set<Pair<Operand, Block>> branches;   //gugu changde: Set can be changed
    private Register result;

    public PhiInst(Block block, Set<Pair<Operand, Block>> branches, Register result) {
        super(block);
        this.branches = branches;
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(result.toString() + " = phi " + result.getLlvMtype().toString() + " ");
//        //gugu changed: replaced
//        Iterator<Pair<Operand, Block>> iterator = branches.iterator();
//        while(iterator.hasNext()){
//            Pair<Operand, Block> branch = iterator.next();
//            Operand operand = branch.getFirst();
//            Block block = branch.getSecond();
//            string.append("[ " + operand.toString() + ", " + block.toString() + " ]");
//            if (iterator.hasNext())
//                string.append(", ");
//        }
        int size = branches.size();
        int cnt = 0;
        for (Pair<Operand, Block> pair : branches) {
            Operand operand = pair.getFirst();
            Block block = pair.getSecond();
            string.append("[ " + operand.toString() + ", " + block.toString() + " ]");
            if (++cnt != size)
                string.append(", ");
        }
        return string.toString();
    }

    public void cutBlock(Block block){
        for(Pair<Operand, Block> branch : this.branches){
            if(branch.getSecond() == block){
                cutBranch(branch);
                break;
            }
        }
    }

    public void cutBranch(Pair<Operand, Block> branch){             //gugu changed: maybe can be combined with cutBlock
        Operand operand = branch.getFirst();
        Block block = branch.getSecond();
        operand.removeUse(this);
        block.removeUse(this);
        this.branches.remove(branch);
    }

    @Override
    public void removeFromBlock() {
        super.removeFromBlock();
        for(Pair<Operand, Block> branch : branches){
            Operand operand = branch.getFirst();
            Block block = branch.getSecond();
            operand.removeUse(this);
            block.removeUse(this);
        }
    }

    @Override
    public void accept(IRVisitor irVisitor) {
        irVisitor.visit(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        for(Pair<Operand, Block> branch : branches){
            if(branch.getFirst() == oldUse){
                branch.getFirst().removeUse(this);
                branch.setFirst((Operand) newUse);
                branch.getFirst().addUse(this);
            }
            if(branch.getSecond() == oldUse){
                branch.getSecond().removeUse(this);
                branch.setSecond((Block) newUse);
                branch.getSecond().addUse(this);
            }
        }
    }

    public Set<Pair<Operand, Block>> getBranches() {
        return branches;
    }

    public void setBranches(Set<Pair<Operand, Block>> branches) {
        this.branches = branches;
    }

    public Register getResult() {
        return result;
    }

    public void setResult(Register result) {
        this.result = result;
    }

    public void addBranch(Operand operand, Block block) {
        branches.add(new Pair<>(operand, block));
        operand.addUse(this);
        block.addUse(this);
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
        if (SideEffectChecker.getOperandScope(result) == SideEffectChecker.Scope.local) {
            if (scopeMap.get(result) != SideEffectChecker.Scope.local) {
                scopeMap.replace(result, SideEffectChecker.Scope.local);
                return true;
            } else
                return false;
        }

        for (Pair<Operand, Block> pair : branches) {
            if (pair.getFirst() instanceof ConstNull)
                continue;
            SideEffectChecker.Scope scope = scopeMap.get(pair.getFirst());
            if (scope == SideEffectChecker.Scope.undefined)
                continue;
            if (scope == SideEffectChecker.Scope.outer) {
                if (scopeMap.get(result) != SideEffectChecker.Scope.outer) {
                    scopeMap.replace(result, SideEffectChecker.Scope.outer);
                    return true;
                } else
                    return false;
            }
        }
        if (scopeMap.get(result) != SideEffectChecker.Scope.local) {
            scopeMap.replace(result, SideEffectChecker.Scope.local);
            return true;
        } else
            return false;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        for (Pair<Operand, Block> pair : branches) {
            pair.getFirst().markBaseAsLive(live, queue);
            if (pair.getSecond().isNotExitBlock() && !live.contains(pair.getSecond().getInstTail())) {
                live.add(pair.getSecond().getInstTail());
                queue.offer(pair.getSecond().getInstTail());
            }
        }
    }

    @Override
    public LLVMInstruction makeCopy() {
        PhiInst phiInst = new PhiInst(this.getBlock(), new LinkedHashSet<>(this.branches), this.getResult().makeCopy());
        phiInst.getResult().setDef(phiInst);
        return phiInst;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        Set<Pair<Operand, Block>> newBranch = new LinkedHashSet<>();
        for (Pair<Operand, Block> pair : branches) {
            Operand operand;
            Block block;
            if (pair.getFirst() instanceof Register) {
                assert operandMap.containsKey(pair.getFirst());
                operand = operandMap.get(pair.getFirst());
            } else
                operand = pair.getFirst();
            operand.addUse(this);

            assert blockMap.containsKey(pair.getSecond());
            block = blockMap.get(pair.getSecond());
            block.addUse(this);

            newBranch.add(new Pair<>(operand, block));
        }
        this.branches = newBranch;
    }

    @Override
    public Object clone() {
        PhiInst phiInst = (PhiInst) super.clone();
        phiInst.branches = new LinkedHashSet<>(this.branches);
        phiInst.result = (Register) this.result.clone();

        phiInst.result.setDef(phiInst);
        return phiInst;
    }
}
