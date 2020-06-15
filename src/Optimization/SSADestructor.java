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
            splitCriticalEdges(function);
            sequentializePC(function);
        }

        return false;
    }

    private void splitCriticalEdges(LLVMfunction function) {
        for (Block block : function.getDFSOrder()) {
            Set<Block> predecessors = new HashSet<>(block.getPredecessors());
            if (predecessors.size() == 0)
                continue;

            ArrayList<PhiInst> phiNodes = new ArrayList<>();
            LLVMInstruction phiPtr = block.getInstHead();
            while (phiPtr instanceof PhiInst) {
                phiNodes.add(((PhiInst) phiPtr));
                phiPtr = phiPtr.getPostInst();
            }
            if (phiNodes.size() == 0)
                continue;

            if (predecessors.size() == 1) {
                for (PhiInst phi : phiNodes) {
                    assert phi.getBranches().size() == 1;
                    phi.getResult().beOverriden(phi.getBranches().iterator().next().getFirst());
                    phi.removeFromBlock();
                }
                continue;
            }

            for (Block predecessor : predecessors) {
                ParallelCopyInst pc;
                if (predecessor.getSuccessors().size() > 1) {
                    // critical edge
                    Block criticalBlock = new Block("criticalBlock",block.getFunction());
//                    block.getFunction().getSymbolTable().put(criticalBlock.getName(), criticalBlock);
                    block.getFunction().registerBlockName(criticalBlock.getName(), criticalBlock);
                    BranchInst branch = new BranchInst(criticalBlock, null, block, null);
                    pc = new ParallelCopyInst(criticalBlock);

                    criticalBlock.addInst(pc);
                    criticalBlock.addInst(branch);

                    if (predecessor.getInstTail() instanceof BranchInst)
                        predecessor.getInstTail().overrideObject(block, criticalBlock);

                    criticalBlock.getPredecessors().add(predecessor);
                    criticalBlock.getSuccessors().add(block);
                    block.getPredecessors().remove(predecessor);
                    block.getPredecessors().add(criticalBlock);
                    predecessor.getSuccessors().remove(block);
                    predecessor.getSuccessors().add(criticalBlock);
                    for (PhiInst phi : phiNodes)
                        phi.overrideObject(predecessor, criticalBlock);

                    block.getFunction().addBasicBlockPrev(block, criticalBlock);
                } else {
                    pc = new ParallelCopyInst(predecessor);
                    if (predecessor.getInstTail() == null || !predecessor.getInstTail().isTerminalInst())
                        predecessor.addInst(pc);
                    else
                        predecessor.addInstructionPrev(predecessor.getInstTail(), pc);
                }
            }

            for (PhiInst phi : phiNodes) {
                for (Pair<Operand, Block> branch : phi.getBranches()) {
                    Block predecessor = branch.getSecond();
                    Operand source = branch.getFirst();
                    predecessor.getParallelCopy().appendMove(new MoveInst(predecessor, source, phi.getResult()));
                }
                phi.removeFromBlock();
            }
        }
    }

    private void sequentializePC(LLVMfunction function) {
        for (Block block : function.getBlocks()) {
            ParallelCopyInst pc = block.getParallelCopy();
            if (pc == null)
                continue;

            ArrayList<MoveInst> moves = new ArrayList<>();
            while (!pc.getMoves().isEmpty()) {
                MoveInst move = pc.findValidMove();
                if (move != null) {
                    moves.add(move);
                    pc.removeMove(move);
                } else {
                    move = pc.getMoves().iterator().next();
                    Operand source = move.getSource();

                    Register cycle = new Register(source.getLlvMtype(), "breakCycle");
                    function.registerVar(cycle.getName(), cycle);

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
            pc.removeFromBlock();
        }
    }
}
