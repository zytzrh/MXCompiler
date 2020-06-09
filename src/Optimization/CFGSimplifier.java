package Optimization;

import IR.LLVMfunction;
import IR.Module;

public class CFGSimplifier extends Pass{
    public CFGSimplifier(Module module) {
        super(module);
        this.setChangded(false);
    }

    @Override
    public boolean run() {
        for(LLVMfunction function : module.getFunctionMap().values()){
            if(!function.isFunctional())
                return false;
        }

        changded = false;
        for(LLVMfunction mfunction : module.getFunctionMap().values()){
            if(functionSimplify(mfunction))
                changded = true;
        }
        return changded;
    }

    private boolean functionSimplify(LLVMfunction mfunction){
        boolean changed = false;
        while(true){
            boolean loopChanged = false;                    //gugu changed: the Order of optimization?
            if(removePhiInstWithSingleBranch(mfunction)) loopChanged = true;
            if(removeUnreachableBlock(mfunction)) loopChanged = true;
            if(removePhiInstWithSingleBranch(mfunction)) loopChanged = true;

            if(loopChanged)
                changed = true;
            else
                break;
        }
        return changed;
    }

    private boolean removeRedundantBranch(LLVMfunction mfunction){
        return false;
    }

    private boolean removeUnreachableBlock(LLVMfunction mfunction){
        return false;
    }

    private boolean removePhiInstWithSingleBranch(LLVMfunction mfunction){
        return false;
    }
}
