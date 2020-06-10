package Optimization;


import IR.Block;
import IR.LLVMfunction;
import IR.Module;
import Utility.Pair;

import java.util.Map;

public class DtreeConstructor extends Pass{
    private Map<Block, Pair<Block, Block>> disjointSet;

    public DtreeConstructor(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for(LLVMfunction mfunction : module.getFunctionMap().values()){         //gugu changed: when can this happen
            if(!mfunction.isFunctional())
                return false;
        }
        return false;


    }


//    private constructDTree(LLVMfunction mfunction){
//        ArrayList<Block> dfsOrder = mfunction.getDFSOrder();
//        disjointSet = new HashMap<>();
//        for(Block block : dfsOrder){
//            disjointSet.put(block, new Pair<>(block, block));
//
//        }
//    }
}
