package Optimization.Loop;

import BackEnd.ASMBlock;
import IR.Block;
import IR.Instruction.BranchInst;
import IR.LLVMfunction;
import IR.Module;
import Optimization.IRPass;

import java.util.*;

public class LoopAnalysis extends IRPass {

    //************************************************************//

    private Map<LLVMfunction, LoopNode> rootNodes;
    private Map<Block, LoopNode> blockNodeMap;
    private Map<Block, LoopNode> headerLoopNodeMap;
    private Set<Block> preHeaders;

    public LoopAnalysis(Module module) {
        super(module);
    }

    public Map<LLVMfunction, LoopNode> getRootNodes() {
        return rootNodes;
    }

    public Map<Block, LoopNode> getBlockNodeMap() {
        return blockNodeMap;
    }

    public boolean isPreHeader(Block block) {
        return preHeaders != null && preHeaders.contains(block);
    }

    public int getBlockDepth(ASMBlock ASMBlock) {                                   //gugu
        Block irBlock = ASMBlock.getIrBlock();
        return blockNodeMap.get(irBlock).getDepth();
    }


    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {                //gugu changed
            if (!function.isFunctional())
                return false;
        }

        rootNodes = new HashMap<>();
        blockNodeMap = new HashMap<>();
        headerLoopNodeMap = new HashMap<>();
        preHeaders = new HashSet<>();
        for (LLVMfunction function : module.getFunctionMap().values())
            rootNodes.put(function, constructLoopTree(function));


        return false;
    }

    private LoopNode constructLoopTree(LLVMfunction function) {
        LoopNode root = new LoopNode(function.getInitBlock(), this);        //
        rootNodes.put(function, root);

        detectNaturalLoop(function.getInitBlock(), new HashSet<>(), root);
        constructLoopNestTree(function.getInitBlock(), new HashSet<>(), root);
        root.setDepth(0);
        dfsLoopTree(root);

        return root;
    }

    private void detectNaturalLoop(Block block, Set<Block> visit, LoopNode root) {
        visit.add(block);
        root.addLoopBlock(block);           //always add to root
        for (Block successor : block.getSuccessors()) {
            if (successor.dominate(block)) {
                // the back egde block->successor(header)
                extractNaturalLoop(successor, block);
            } else if (!visit.contains(successor))
                detectNaturalLoop(successor, visit, root);
        }
    }


    private void constructLoopNestTree(Block block, Set<Block> visit, LoopNode currentLoop) {
        visit.add(block);

        LoopNode child = null;
        if (block == currentLoop.getHeader()) {
            // block == entrance block????????
            currentLoop.setUniqueLoopBlocks(new HashSet<>(currentLoop.getLoopBlocks()));    //???
        } else if (headerLoopNodeMap.containsKey(block)) {
            child = headerLoopNodeMap.get(block);
            child.setFather(currentLoop);
            currentLoop.addChild(child);

            currentLoop.removeUniqueLoopBlocks(child);
            child.setUniqueLoopBlocks(new HashSet<>(child.getLoopBlocks()));
        }

        for (Block successor : block.getSuccessors()) {
            if (!visit.contains(successor)) {
                LoopNode nextLoopNode;
                if(child != null)
                    nextLoopNode = child;
                else
                    nextLoopNode = currentLoop;

                while (nextLoopNode != null && !nextLoopNode.getLoopBlocks().contains(successor))
                    nextLoopNode = nextLoopNode.getFather();

                assert nextLoopNode != null;
                constructLoopNestTree(successor, visit, nextLoopNode);
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

        //set exitblock
        Set<Block> exitBlocks = new HashSet<>();
        if (loop.hasFather()) {
            for (LoopNode child : loop.getChildren()) {
                for (Block exitBlock : child.getExitBlocks()) {
                    assert exitBlock.getInstTail() instanceof BranchInst;
                    BranchInst exitInst = ((BranchInst) exitBlock.getInstTail());
                    if (!loop.getLoopBlocks().contains(exitInst.getIfTrueBlock())) {
                        exitBlocks.add(exitBlock);
                        break;
                    }
                    if (exitInst.getCondition() != null && !loop.getLoopBlocks().contains(exitInst.getIfFalseBlock())) {
                        exitBlocks.add(exitBlock);
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

    private void extractNaturalLoop(Block header, Block end) {
        LoopNode loop = new LoopNode(header, this);
        loop.addLoopBlock(header);
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
        //merge loop with the same header, the result may be not natural loop
        if (!headerLoopNodeMap.containsKey(header))
            headerLoopNodeMap.put(header, loop);
        else{
            LoopNode existingHeader = headerLoopNodeMap.get(header);
            existingHeader.getLoopBlocks().addAll(loop.getLoopBlocks());
        }
    }

    public void setRootNodes(Map<LLVMfunction, LoopNode> rootNodes) {
        this.rootNodes = rootNodes;
    }

    public void setBlockNodeMap(Map<Block, LoopNode> blockNodeMap) {
        this.blockNodeMap = blockNodeMap;
    }

    public Map<Block, LoopNode> getHeaderLoopNodeMap() {
        return headerLoopNodeMap;
    }

    public void setHeaderLoopNodeMap(Map<Block, LoopNode> headerLoopNodeMap) {
        this.headerLoopNodeMap = headerLoopNodeMap;
    }

    public Set<Block> getPreHeaders() {
        return preHeaders;
    }

    public void setPreHeaders(Set<Block> preHeaders) {
        this.preHeaders = preHeaders;
    }
}

