package Optimization.Loop;

import BackEnd.ASMBlock;
import IR.Block;
import IR.Instruction.BranchInst;
import IR.LLVMfunction;
import IR.Module;
import Optimization.Pass;

import java.util.*;

public class LoopAnalysis extends Pass {

    //************************************************************//

    private Map<LLVMfunction, LoopNode> loopRoot;
    private Map<Block, LoopNode> blockNodeMap;
    private Map<Block, LoopNode> headerNodeMap;
    private Set<Block> preHeaders;

    public LoopAnalysis(Module module) {
        super(module);
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
        for (LLVMfunction function : module.getFunctionMap().values()) {                //gugu changed
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
        LoopNode root = new LoopNode(function.getInitBlock(), this);
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
        LoopNode loop = new LoopNode(header, this);

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
        if (block == currentLoop.getHeader()) {
            // block == entrance block
            currentLoop.setUniqueLoopBlocks(new HashSet<>(currentLoop.getLoopBlocks()));
        } else if (headerNodeMap.containsKey(block)) {
            child = headerNodeMap.get(block);
            child.setFather(currentLoop);
            currentLoop.addChild(child);

            currentLoop.removeUniqueLoopBlocks(child);
            child.setUniqueLoopBlocks(new HashSet<>(child.getLoopBlocks()));
        }

        for (Block successor : block.getSuccessors()) {
            if (!visit.contains(successor)) {
                LoopNode nextLoop = child != null ? child : currentLoop;
                while (nextLoop != null && !nextLoop.getLoopBlocks().contains(successor))
                    nextLoop = nextLoop.getFather();
                assert nextLoop != null;

                dfsConstructLoopTree(successor, visit, nextLoop);
            }
        }
    }

    private void dfsLoopTree(LoopNode loop) {
        for (Block block : loop.getUniqueLoopBlocks())
            blockNodeMap.put(block, loop);

        for (LoopNode child : loop.getChildren()) {
            child.setDepth(loop.getDepth() + 1);
            dfsLoopTree(child);
        }
        if (loop.hasFather() && !loop.hasPreHeader(blockNodeMap))
            loop.addPreHeader(blockNodeMap);

        Set<Block> exitBlocks = new HashSet<>();
        if (loop.hasFather()) {
            for (LoopNode child : loop.getChildren()) {
                for (Block exit : child.getExitBlocks()) {
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

    public void setLoopRoot(Map<LLVMfunction, LoopNode> loopRoot) {
        this.loopRoot = loopRoot;
    }

    public void setBlockNodeMap(Map<Block, LoopNode> blockNodeMap) {
        this.blockNodeMap = blockNodeMap;
    }

    public Map<Block, LoopNode> getHeaderNodeMap() {
        return headerNodeMap;
    }

    public void setHeaderNodeMap(Map<Block, LoopNode> headerNodeMap) {
        this.headerNodeMap = headerNodeMap;
    }

    public Set<Block> getPreHeaders() {
        return preHeaders;
    }

    public void setPreHeaders(Set<Block> preHeaders) {
        this.preHeaders = preHeaders;
    }
}

