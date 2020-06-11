package Optimization;

import BackEnd.ASMBlock;
import IR.Block;
import IR.Instruction.BranchInst;
import IR.Instruction.LLVMInstruction;
import IR.Instruction.PhiInst;
import IR.LLVMfunction;
import IR.LLVMoperand.Constant;
import IR.LLVMoperand.GlobalVar;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;
import Utility.Pair;

import java.util.*;

public class LoopAnalysis extends Pass {
    static public class LoopNode {
        static LoopAnalysis loopAnalysis;

        private Block header;
        private Set<Block> loopBlocks;
        private Set<Block> uniqueLoopBlocks;
        private Set<Block> exitBlocks;

        private LoopNode father;
        private ArrayList<LoopNode> children;

        private int depth;

        private Block preHeader;

        public LoopNode(Block header) {
            this.header = header;
            this.loopBlocks = new HashSet<>();
            this.uniqueLoopBlocks = null;
            this.exitBlocks = null;
            this.father = null;
            this.children = new ArrayList<>();
            this.depth = 0;
            this.preHeader = null;
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
                loopAnalysis.preHeaders.add(preHeader);
                assert blockNodeMap.containsKey(preHeader) && blockNodeMap.get(preHeader) == this.father;
                return true;
            } else
                return false;
        }

        public void addPreHeader(Map<Block, LoopNode> blockNodeMap) {
            LLVMfunction function = header.getFunction();
            preHeader = new Block("preHeaderOf" + header.getNameWithoutDot(), function);
            loopAnalysis.preHeaders.add(preHeader);
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
    }

    private Map<LLVMfunction, LoopNode> loopRoot;
    private Map<Block, LoopNode> blockNodeMap;
    private Map<Block, LoopNode> headerNodeMap;
    private Set<Block> preHeaders;

    public LoopAnalysis(Module module) {
        super(module);
        LoopNode.loopAnalysis = this;
    }

    public Map<LLVMfunction, LoopNode> getLoopRoot() {
        return loopRoot;
    }

    public Map<Block, LoopNode> getBlockNodeMap() {
        return blockNodeMap;
    }

    public boolean isPreHeader(Block block) {
        return preHeaders != null && preHeaders.contains(block);
    }

    public int getBlockDepth(ASMBlock ASMBlock) {
        Block irBlock = ASMBlock.getIrBlock();
        return blockNodeMap.get(irBlock).getDepth();
    }

    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {
            if (!function.isFunctional())
                return false;
        }

        loopRoot = new HashMap<>();
        blockNodeMap = new HashMap<>();
        headerNodeMap = new HashMap<>();
        preHeaders = new HashSet<>();
        for (LLVMfunction function : module.getFunctionMap().values())
            loopRoot.put(function, constructLoopTree(function));


        return false;
    }

    private LoopNode constructLoopTree(LLVMfunction function) {
        LoopNode root = new LoopNode(function.getInitBlock());
        loopRoot.put(function, root);

        dfsDetectNaturalLoop(function.getInitBlock(), new HashSet<>(), root);
        dfsConstructLoopTree(function.getInitBlock(), new HashSet<>(), root);
        root.setDepth(0);
        dfsLoopTree(root);

        return root;
    }

    private void dfsDetectNaturalLoop(Block block, Set<Block> visit, LoopNode root) {
        visit.add(block);
        root.addLoopBlock(block);
        for (Block successor : block.getSuccessors()) {
            if (successor.dominate(block)) {
                // Means that a back edge is found.
                extractNaturalLoop(successor, block);
            } else if (!visit.contains(successor))
                dfsDetectNaturalLoop(successor, visit, root);
        }
    }

    private void extractNaturalLoop(Block header, Block end) {
        LoopNode loop = new LoopNode(header);

        HashSet<Block> visit = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        queue.offer(end);
        visit.add(end);
        while (!queue.isEmpty()) {
            Block block = queue.poll();
            if (header.dominate(block))
                loop.addLoopBlock(block);

            for (Block predecessor : block.getPredecessors()) {
                if (predecessor != header && !visit.contains(predecessor)) {
                    queue.offer(predecessor);
                    visit.add(predecessor);
                }
            }
        }
        loop.addLoopBlock(header);

        if (!headerNodeMap.containsKey(header))
            headerNodeMap.put(header, loop);
        else
            headerNodeMap.get(header).mergeLoopNode(loop);
    }

    private void dfsConstructLoopTree(Block block, Set<Block> visit, LoopNode currentLoop) {
        visit.add(block);

        LoopNode child = null;
        if (block == currentLoop.header) {
            // block == entrance block
            currentLoop.uniqueLoopBlocks = new HashSet<>(currentLoop.loopBlocks);
        } else if (headerNodeMap.containsKey(block)) {
            child = headerNodeMap.get(block);
            child.setFather(currentLoop);
            currentLoop.addChild(child);

            currentLoop.removeUniqueLoopBlocks(child);
            child.uniqueLoopBlocks = new HashSet<>(child.loopBlocks);
        }

        for (Block successor : block.getSuccessors()) {
            if (!visit.contains(successor)) {
                LoopNode nextLoop = child != null ? child : currentLoop;
                while (nextLoop != null && !nextLoop.loopBlocks.contains(successor))
                    nextLoop = nextLoop.father;
                assert nextLoop != null;

                dfsConstructLoopTree(successor, visit, nextLoop);
            }
        }
    }

    private void dfsLoopTree(LoopNode loop) {
        for (Block block : loop.uniqueLoopBlocks)
            blockNodeMap.put(block, loop);

        for (LoopNode child : loop.children) {
            child.setDepth(loop.getDepth() + 1);
            dfsLoopTree(child);
        }
        if (loop.hasFather() && !loop.hasPreHeader(blockNodeMap))
            loop.addPreHeader(blockNodeMap);

        Set<Block> exitBlocks = new HashSet<>();
        if (loop.hasFather()) {
            for (LoopNode child : loop.children) {
                for (Block exit : child.exitBlocks) {
                    assert exit.getInstTail() instanceof BranchInst;
                    BranchInst exitInst = ((BranchInst) exit.getInstTail());
                    if (!loop.getLoopBlocks().contains(exitInst.getIfTrueBlock())) {
                        exitBlocks.add(exit);
                        break;
                    }
                    if (exitInst.getCondition() != null && !loop.getLoopBlocks().contains(exitInst.getIfFalseBlock())) {
                        exitBlocks.add(exit);
                        break;
                    }
                }
            }
            for (Block exit : loop.getUniqueLoopBlocks()) {
                assert exit.getInstTail() instanceof BranchInst;
                BranchInst exitInst = ((BranchInst) exit.getInstTail());
                if (!loop.getLoopBlocks().contains(exitInst.getIfTrueBlock())) {
                    exitBlocks.add(exit);
                    break;
                }
                if (exitInst.getCondition() != null && !loop.getLoopBlocks().contains(exitInst.getIfFalseBlock())) {
                    exitBlocks.add(exit);
                    break;
                }
            }
        }
        loop.setExitBlocks(exitBlocks);
    }
}

