package Optimization;


import IR.Block;
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
        return false;


    }

//
//    private constructDTree(LLVMfunction mfunction){
//        ArrayList<Block>
//    }
}
