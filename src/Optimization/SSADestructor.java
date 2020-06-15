package Optimization;

import IR.Block;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;
import Utility.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SSADestructor extends IRPass {
    public SSADestructor(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {
            destructFunction(function);
        }

        return false;
    }

    public void destructFunction(LLVMfunction mfunction){
        for(Block block : mfunction.getDFSOrder()){
            addCriticalBlock(block);
        }
        for (Block block : mfunction.getBlocks()) {
            parallelCopy2move(block);
        }
    }

    private void addCriticalBlock(Block block) {
        Set<Block> predecessors = new HashSet<>(block.getPredecessors());
        ArrayList<PhiInst> phiInsts = new ArrayList<>();
        if (predecessors.size() == 0)
            return;

        LLVMInstruction phiPtr = block.getInstHead();
        while (phiPtr instanceof PhiInst) {
            phiInsts.add(((PhiInst) phiPtr));
            phiPtr = phiPtr.getPostInst();
        }
        if (phiInsts.size() == 0)
            return;

        if (predecessors.size() == 1) {
            for (PhiInst phiInst : phiInsts) {
                assert phiInst.getBranches().size() == 1;
                Operand imcomeResult = phiInst.getBranches().iterator().next().getFirst();
                phiInst.getResult().beOverriden(imcomeResult);
                phiInst.removeFromBlock();
            }
        }else{
            for (Block predecessor : predecessors) {
                ParallelCopyInst parallelCopyInst;
                if (predecessor.getSuccessors().size() > 1) {
                    Block criticalBlock = new Block("criticalBlock",block.getFunction());
                    block.getFunction().registerBlockName(criticalBlock.getName(), criticalBlock);
                    BranchInst branch = new BranchInst(criticalBlock, null, block, null);
                    parallelCopyInst = new ParallelCopyInst(criticalBlock);

                    criticalBlock.addInst(parallelCopyInst);
                    criticalBlock.addInst(branch);

                    if (predecessor.getInstTail() instanceof BranchInst)
                        predecessor.getInstTail().overrideObject(block, criticalBlock);
                    //fix the block relationship
                    criticalBlock.getPredecessors().add(predecessor);
                    criticalBlock.getSuccessors().add(block);
                    block.getPredecessors().remove(predecessor);
                    block.getPredecessors().add(criticalBlock);
                    predecessor.getSuccessors().remove(block);
                    predecessor.getSuccessors().add(criticalBlock);
                    for (PhiInst phi : phiInsts)
                        phi.overrideObject(predecessor, criticalBlock);

                    block.getFunction().addBasicBlockPrev(block, criticalBlock);
                } else {
                    parallelCopyInst = new ParallelCopyInst(predecessor);
                    if (predecessor.getInstTail() == null || !predecessor.getInstTail().isTerminalInst())
                        predecessor.addInst(parallelCopyInst);
                    else
                        predecessor.addInstructionPrev(predecessor.getInstTail(), parallelCopyInst);
                }
            }

            for (PhiInst phi : phiInsts) {
                for (Pair<Operand, Block> branch : phi.getBranches()) {
                    Block predecessor = branch.getSecond();
                    Operand source = branch.getFirst();
                    predecessor.getParallelCopy().appendMove(new MoveInst(predecessor, source, phi.getResult()));
                }
                phi.removeFromBlock();
            }
        }
    }

    private void parallelCopy2move(Block block) {
        ParallelCopyInst parallelCopy = block.getParallelCopy();
        if (parallelCopy != null){
            ArrayList<MoveInst> moves = new ArrayList<>();
            while (!parallelCopy.getMoves().isEmpty()) {
                MoveInst move = parallelCopy.findValidMove();
                if (move != null) {
                    moves.add(move);
                    parallelCopy.removeMove(move);
                } else {
                    move = parallelCopy.getMoves().iterator().next();
                    Operand source = move.getSource();

                    Register cycle = new Register(source.getLlvMtype(), "breakCycle");
                    block.getFunction().registerVar(cycle.getName(), cycle);

                    moves.add(new MoveInst(block, source, cycle));
                    move.setSource(cycle);
                }
            }
            if (block.getInstTail() == null || !block.getInstTail().isTerminalInst()) {
                for (MoveInst move : moves)
                    block.addInst(move);
            } else {
                for (MoveInst move : moves)
                    block.addInstructionPrev(block.getInstTail(), move);
            }
            parallelCopy.removeFromBlock();
        }
    }
}
