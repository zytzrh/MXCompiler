package Optimization;


import IR.Block;
import IR.LLVMfunction;
import IR.Module;
import Utility.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DTreeConstructor extends IRPass {
    private Map<Block, Pair<Block, Block>> disjointSet;
    // first for father
    // second for the min semi dom dfn node.

    public DTreeConstructor(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        if(!module.checkNormalFunctional()) return false;
        for (LLVMfunction function : module.getFunctionMap().values()) {
            constructDTree(function);
            constructDFrontier(function);
        }
        for (LLVMfunction function : module.getFunctionMap().values()) {
            constructPostDTree(function);
            constructPostDFrontier(function);
        }
//        print();
        return true;
    }

    private Pair<Block, Block> updateDisjointSet(Block block) {
        Pair<Block, Block> pair = disjointSet.get(block);
        if (pair.getFirst() == block)
            return new Pair<>(block, block);
        Pair<Block, Block> res = updateDisjointSet(pair.getFirst());
        Block father = res.getFirst();
        Block minSemiDomDfnNode = res.getSecond();

        pair.setFirst(father);
        if (minSemiDomDfnNode.getSemiDom().getDfsNum() < pair.getSecond().getSemiDom().getDfsNum())
            pair.setSecond(minSemiDomDfnNode);
        return pair;
    }

    private void constructDTree(LLVMfunction function) {
        ArrayList<Block> dfsOrder = function.getDFSOrder();
        disjointSet = new HashMap<>();
        for (Block block : dfsOrder) {
            disjointSet.put(block, new Pair<>(block, block));
            block.setIdom(null);
            block.setSemiDom(block);
            block.setSemiDomChildren(new ArrayList<>());
        }

        for (int i = dfsOrder.size() - 1; i > 0; i--) {
            Block block = dfsOrder.get(i);
            assert block.getDfsNum() == i;

            for (Block predecessor : block.getPredecessors()) {
                if (predecessor.getDfsNum() < block.getDfsNum()) {
                    if (predecessor.getDfsNum() < block.getSemiDom().getDfsNum())
                        block.setSemiDom(predecessor);
                } else {
                    Pair<Block, Block> updateResult = updateDisjointSet(predecessor);
                    if (updateResult.getSecond().getSemiDom().getDfsNum() < block.getSemiDom().getDfsNum())
                        block.setSemiDom(updateResult.getSecond().getSemiDom());
                }
            }

            Block father = block.getDfsParent();
            block.getSemiDom().getSemiDomChildren().add(block);
            disjointSet.get(block).setFirst(father);

            for (Block semiDomChild : father.getSemiDomChildren()) {
                Pair<Block, Block> updateResult = updateDisjointSet(semiDomChild);
                if (updateResult.getSecond().getSemiDom() == semiDomChild.getSemiDom())
                    semiDomChild.setIdom(semiDomChild.getSemiDom());
                else
                    semiDomChild.setIdom(updateResult.getSecond());
            }
        }

        for (int i = 1; i < dfsOrder.size(); i++) {
            Block block = dfsOrder.get(i);
            if (block.getIdom() != block.getSemiDom())
                block.setIdom(block.getIdom().getIdom());
        }
        for (Block block : dfsOrder) {
            HashSet<Block> strictDominators = new HashSet<>();
            Block ptr = block.getIdom();
            while (ptr != null) {
                strictDominators.add(ptr);
                ptr = ptr.getIdom();
            }
            block.setStrictDominators(strictDominators);
        }
    }

    private void constructDFrontier(LLVMfunction function) {
        ArrayList<Block> blocks = function.getBlocks();
        for (Block block : blocks)
            block.setDF(new HashSet<>());

        for (Block block : blocks)
            for (Block predecessor : block.getPredecessors()) {
                Block ptr = predecessor;
                while (!block.getStrictDominators().contains(ptr)) {
                    ptr.getDF().add(block);
                    ptr = ptr.getIdom();
                }
            }
    }

    private void constructPostDTree(LLVMfunction function) {
        ArrayList<Block> reverseDfsOrder = function.getReverseDFSOrder();
        disjointSet = new HashMap<>();
        for (Block block : reverseDfsOrder) {
            disjointSet.put(block, new Pair<>(block, block));
            block.setPostIdom(null);
            block.setPostSemiDom(block);
            block.setPostSemiDomChildren(new ArrayList<>());
        }

        for (int i = reverseDfsOrder.size() - 1; i > 0; i--) {
            Block block = reverseDfsOrder.get(i);
            assert block.getR_dfsNum() == i;

            for (Block successor : block.getSuccessors()) {
                if (successor.getR_dfsNum() < block.getR_dfsNum()) {
                    if (successor.getR_dfsNum() < block.getPostSemiDom().getR_dfsNum())
                        block.setPostSemiDom(successor);
                } else {
                    Pair<Block, Block> updateResult = updateDisjointSet(successor);
                    if (updateResult.getSecond().getPostSemiDom().getR_dfsNum()
                            < block.getPostSemiDom().getR_dfsNum())
                        block.setPostSemiDom(updateResult.getSecond().getPostSemiDom());
                }
            }

            Block father = block.getR_dfsParent();
            block.getPostSemiDom().getPostSemiDomChildren().add(block);
            disjointSet.get(block).setFirst(father);

            for (Block postSemiDomChild : father.getPostSemiDomChildren()) {
                Pair<Block, Block> updateResult = updateDisjointSet(postSemiDomChild);
                if (updateResult.getSecond().getPostSemiDom() == postSemiDomChild.getPostSemiDom())
                    postSemiDomChild.setPostIdom(postSemiDomChild.getPostSemiDom());
                else
                    postSemiDomChild.setPostIdom(updateResult.getSecond());
            }
        }

        for (int i = 1; i < reverseDfsOrder.size(); i++) {
            Block block = reverseDfsOrder.get(i);
            if (block.getPostIdom() != block.getPostSemiDom())
                block.setPostIdom(block.getPostIdom().getPostIdom());
        }
        for (Block block : reverseDfsOrder) {
            HashSet<Block> postStrictDominators = new HashSet<>();
            Block ptr = block.getPostIdom();
            while (ptr != null) {
                postStrictDominators.add(ptr);
                ptr = ptr.getPostIdom();
            }
            block.setPostStrictDominators(postStrictDominators);
        }
    }

    private void constructPostDFrontier(LLVMfunction function) {
        ArrayList<Block> blocks = function.getBlocks();
        for (Block block : blocks)
            block.setPostDF(new HashSet<>());

        for (Block block : blocks)
            for (Block successor : block.getSuccessors()) {
                Block ptr = successor;
                while (!block.getPostStrictDominators().contains(ptr)) {
                    ptr.getPostDF().add(block);
                    ptr = ptr.getPostIdom();
                }
            }
    }


}

