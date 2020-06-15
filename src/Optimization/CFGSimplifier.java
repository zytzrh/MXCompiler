package Optimization;

import IR.Block;
import IR.Instruction.BranchInst;
import IR.Instruction.LLVMInstruction;
import IR.Instruction.PhiInst;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstBool;
import IR.LLVMoperand.Operand;
import IR.Module;
import Utility.Pair;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Set;

public class CFGSimplifier extends IRPass {
    public CFGSimplifier(Module module) {
        super(module);
        this.setChanged(false);
    }

    @Override
    public boolean run() {
        for(LLVMfunction function : module.getFunctionMap().values()){
            if(!function.isFunctional())
                return false;
        }

        changed = false;
        for(LLVMfunction mfunction : module.getFunctionMap().values()){
            if(functionSimplify(mfunction))
                changed = true;
        }
        return changed;
    }

    private boolean functionSimplify(LLVMfunction mfunction){
        boolean changed = false;
        while(true){
            boolean loopChanged = false;                    //gugu changed: the Order of optimization?
            if(removeRedundantBranch(mfunction)) loopChanged = true;
            if(removeUnreachableBlock(mfunction)) loopChanged = true;
            if(optimizePhiSingleBranch(mfunction)) loopChanged = true;

            if(loopChanged)
                changed = true;
            else
                break;
        }
        return changed;
    }

    private boolean removeRedundantBranch(LLVMfunction mfunction){
        boolean changed = false;
        ArrayList<Block> dfsOrder = mfunction.getDFSOrder();
        ListIterator<Block> blockListIterator = dfsOrder.listIterator(dfsOrder.size());
        while(blockListIterator.hasPrevious()){
            Block selfBlock = blockListIterator.previous();
            LLVMInstruction instTail = selfBlock.getInstTail();
            if(instTail instanceof BranchInst){                         //gugu changed: these two optimization can be made separate function
                BranchInst branchInst = (BranchInst) instTail;
                Block ifTrueBlock = branchInst.getIfTrueBlock();
                Block ifFalseBlock = branchInst.getIfFalseBlock();
                Operand condition = branchInst.getCondition();
                if(ifTrueBlock == ifFalseBlock){
                    //merge ifTrue and ifFalse
                    branchInst.reset2Unconditional(ifTrueBlock);        //gugu changed: can be replaced by a merge function
                    changed = true;
                }
                if(condition instanceof ConstBool){
                    boolean constCondtion = ((ConstBool) condition).getValue();
                    if(constCondtion){
                        ifFalseBlock.cutBlockForPhi(selfBlock);     //phi cut
                        branchInst.reset2Unconditional(ifTrueBlock);
                        changed = true;
                    }else{
                        ifTrueBlock.cutBlockForPhi(selfBlock);
                        branchInst.reset2Unconditional(ifFalseBlock);
                        changed = true;
                    }
                }
            }
        }
        return changed;

    }

    private boolean removeUnreachableBlock(LLVMfunction mfunction){
        boolean changed = false;
        ArrayList<Block> dfsOrder = mfunction.getDFSOrder();
        Block currentBlock = mfunction.getInitBlock();
        while(currentBlock != null){
            Block nextBlock = currentBlock.getNext();
            //delete not existing block
            if(!dfsOrder.contains(currentBlock)){
                currentBlock.removeFromFunction();
                changed = true;
            }
            //merge block or delete self-loop block
            Set<Block> predecessors = currentBlock.getPredecessors();
            if(predecessors.size() == 1){
                Block predecessor = predecessors.iterator().next();
                if(predecessor.getSuccessors().size() == 1){
                    if(predecessor == currentBlock)
                        currentBlock.removeFromFunction();
                    else
                        mergeBlock(predecessor, currentBlock);
                    changed = true;
                }
            }
            currentBlock = nextBlock;
        }
        return changed;
    }

    private void mergeBlock(Block predecessorBlock, Block successorBlock){
        predecessorBlock.getInstTail().removeFromBlock();
        LLVMInstruction currentInstruction = successorBlock.getInstHead();
        while(currentInstruction != null){
            LLVMInstruction nextInstruction = currentInstruction.getPostInst();
            if(currentInstruction instanceof PhiInst){
                PhiInst phiInst = (PhiInst) currentInstruction;
                assert phiInst.getBranches().size() == 1;
                deletePhiSingleBranch(phiInst);
            }else{
                currentInstruction.setBlock(predecessorBlock);
                currentInstruction.setPreInst(predecessorBlock.getInstTail());
                predecessorBlock.justAddInst(currentInstruction);
            }
            currentInstruction = nextInstruction;
        }

        for(Block successor : successorBlock.getSuccessors()){
            predecessorBlock.getSuccessors().add(successor);
            successor.getPredecessors().add(predecessorBlock);
        }


        //hide successorBlock
        successorBlock.beOverriden(predecessorBlock);
        successorBlock.removeFromList();
        successorBlock.cutCFGLink();
    }

    private boolean optimizePhiSingleBranch(LLVMfunction mfunction){      //delete phi in the front of block
        boolean changed = false;
        Block currentBlock = mfunction.getInitBlock();
        while(currentBlock != null){
            Block nextBlock = currentBlock.getNext();
            LLVMInstruction currentInstruction = currentBlock.getInstHead();
            while(currentInstruction instanceof PhiInst){
                LLVMInstruction nextInstruction = currentInstruction.getPostInst();
                PhiInst phiInst = (PhiInst) currentInstruction;
                if(phiInst.getBranches().size() == 1){
                    deletePhiSingleBranch(phiInst);
                    changed = true;
                }
                currentInstruction = nextInstruction;
            }
            currentBlock = nextBlock;
        }
        return changed;
    }

    private void deletePhiSingleBranch(PhiInst phiInst){
        Set<Pair<Operand, Block>> branches = phiInst.getBranches();
        Operand result = phiInst.getResult();
        assert branches.size() == 1;
        Operand onlyIncomeValue = branches.iterator().next().getFirst();
        result.beOverriden(onlyIncomeValue);
        phiInst.removeFromBlock();
    }

}
