package Optimization.Loop;

import IR.Block;
import IR.Instruction.BranchInst;
import IR.Instruction.LLVMInstruction;
import IR.Instruction.PhiInst;
import IR.LLVMfunction;
import IR.LLVMoperand.Constant;
import IR.LLVMoperand.GlobalVar;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import Utility.Pair;

import java.util.*;

public class LoopNode {
    LoopAnalysis loopAnalysis;

    private Block header;                   //
    private Set<Block> loopBlocks;
    private Set<Block> uniqueLoopBlocks;
    private Set<Block> exitBlocks;

    private LoopNode father;
    private ArrayList<LoopNode> children;

    private int depth;

    private Block preHeader;            //

    public LoopNode(Block header, LoopAnalysis loopAnalysis) {
        this.header = header;
        this.loopAnalysis = loopAnalysis;
        this.loopBlocks = new HashSet<>();
        this.uniqueLoopBlocks = null;
        this.exitBlocks = null;
        this.father = null;
        this.children = new ArrayList<>();
        this.depth = 0;
        this.preHeader = null;
    }


    public boolean hasPreHeader(Map<Block, LoopNode> blockNodeMap) {
        if (preHeader != null)
            return true;

        /*
         * If header has only one predecessor out of block,
         * and the only predecessor has only one successor(header),
         * then we can mark the only predecessor as preHeader.
         */
        int predecessorCnt = 0;
        int successorCnt = 0;
        Block mayPreHeader = null;
        for (Block predecessor : header.getPredecessors()) {
            if (loopBlocks.contains(predecessor))
                continue;
            predecessorCnt++;
            successorCnt = predecessor.getSuccessors().size();
            mayPreHeader = predecessor;
        }

        if (predecessorCnt == 1 && successorCnt == 1) {
            preHeader = mayPreHeader;
            loopAnalysis.getPreHeaders().add(preHeader);
            assert blockNodeMap.containsKey(preHeader) && blockNodeMap.get(preHeader) == this.father;
            return true;
        } else
            return false;
    }

    public void addPreHeader(Map<Block, LoopNode> blockNodeMap) {
        LLVMfunction function = header.getFunction();
        preHeader = new Block("preHeaderOf" + header.getNameWithoutDot(), function);
        loopAnalysis.getPreHeaders().add(preHeader);
        function.registerBlockName(preHeader.getName(), preHeader);

        // Deal with PhiInst.
        LLVMInstruction ptr = header.getInstHead();
        while (ptr instanceof PhiInst) {
            assert ptr.hasResult();
            Register result = ((PhiInst) ptr).getResult();
            Register newResult = new Register(result.getLlvMtype(), result.getNameWithoutDot());
            PhiInst phiInst = new PhiInst(preHeader, new LinkedHashSet<>(), newResult);
            function.registerVar(newResult.getName(), newResult);

            ArrayList<Pair<Operand, Block>> removeList = new ArrayList<>();
            for (Pair<Operand, Block> branch : ((PhiInst) ptr).getBranches()) {
                if (loopBlocks.contains(branch.getSecond()))
                    continue;
                phiInst.addBranch(branch.getFirst(), branch.getSecond());
                removeList.add(branch);
            }
            preHeader.addInst(phiInst);

            for (Pair<Operand, Block> pair : removeList)
                ((PhiInst) ptr).cutBranch(pair);
            ((PhiInst) ptr).addBranch(newResult, preHeader);

            ptr = ptr.getPostInst();
        }

        // Deal with predecessor and successor.
        ArrayList<Block> removeList = new ArrayList<>();
        for (Block predecessor : header.getPredecessors()) {
            if (loopBlocks.contains(predecessor))
                continue;
            LLVMInstruction branchInst = predecessor.getInstTail();
            assert branchInst instanceof BranchInst;
            branchInst.overrideObject(header, preHeader);

            predecessor.getSuccessors().remove(header);
            predecessor.getSuccessors().add(preHeader);
            preHeader.getPredecessors().add(predecessor);
            removeList.add(predecessor);
        }
        for (Block block : removeList)
            header.getPredecessors().remove(block);

        preHeader.addInst(new BranchInst(preHeader, null, header, null));
        assert header.getPrev() != null;
        header.getPrev().setNext(preHeader);
        preHeader.setPrev(header.getPrev());
        header.setPrev(preHeader);
        preHeader.setNext(header);

        blockNodeMap.put(preHeader, this.father);
    }

    public Block getPreHeader() {
        return preHeader;
    }

    public void mergeLoopNode(LoopNode loop) {
        assert this.header == loop.header;
        this.loopBlocks.addAll(loop.loopBlocks);
    }

    public void removeUniqueLoopBlocks(LoopNode child) {
        assert uniqueLoopBlocks.containsAll(child.loopBlocks);
        uniqueLoopBlocks.removeAll(child.loopBlocks);
    }

    public boolean defOutOfLoop(Operand operand) {
        if ((operand instanceof Register && ((Register) operand).isParameter())
                || operand instanceof Constant
                || operand instanceof GlobalVar)
            return true;
        assert operand instanceof Register;
        return !this.loopBlocks.contains(((Register) operand).getDef().getBlock());
    }



    @Override
    public String toString() {
        return header.getName();
    }

    public LoopAnalysis getLoopAnalysis() {
        return loopAnalysis;
    }

    public void setLoopAnalysis(LoopAnalysis loopAnalysis) {
        this.loopAnalysis = loopAnalysis;
    }

    public Block getHeader() {
        return header;
    }

    public void setHeader(Block header) {
        this.header = header;
    }

    public void setLoopBlocks(Set<Block> loopBlocks) {
        this.loopBlocks = loopBlocks;
    }

    public void setUniqueLoopBlocks(Set<Block> uniqueLoopBlocks) {
        this.uniqueLoopBlocks = uniqueLoopBlocks;
    }

    public LoopNode getFather() {
        return father;
    }

    public void setChildren(ArrayList<LoopNode> children) {
        this.children = children;
    }

    public void setPreHeader(Block preHeader) {
        this.preHeader = preHeader;
    }


    public void addLoopBlock(Block block) {
        this.loopBlocks.add(block);
    }

    public Set<Block> getLoopBlocks() {
        return loopBlocks;
    }

    public Set<Block> getUniqueLoopBlocks() {
        return uniqueLoopBlocks;
    }

    public Set<Block> getExitBlocks() {
        return exitBlocks;
    }

    public void setExitBlocks(Set<Block> exitBlocks) {
        this.exitBlocks = exitBlocks;
    }

    public void setFather(LoopNode father) {
        this.father = father;
    }

    public boolean hasFather() {
        return father != null;
    }

    public void addChild(LoopNode child) {
        children.add(child);
    }

    public ArrayList<LoopNode> getChildren() {
        return children;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
