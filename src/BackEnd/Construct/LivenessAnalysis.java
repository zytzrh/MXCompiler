package BackEnd.Construct;

import BackEnd.ASMBlock;
import BackEnd.ASMModule;
import BackEnd.Instruction.ASMInstruction;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.ASMFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LivenessAnalysis extends ASMPass {
    public LivenessAnalysis(ASMModule ASMModule) {
        super(ASMModule);
    }

    @Override
    public void run() {
        for (ASMFunction ASMFunction : ASMModule.getFunctionMap().values())
            computeLiveOutSet(ASMFunction);
    }

    private void computeLiveOutSet(ASMFunction ASMFunction) {
        ArrayList<ASMBlock> dfsOrder = ASMFunction.getDFSOrder();
        for (ASMBlock block : dfsOrder){
            Set<VirtualASMRegister> UEVar = new HashSet<>();
            Set<VirtualASMRegister> varKill = new HashSet<>();

            ASMInstruction ptr = block.getInstHead();
            while (ptr != null) {
                ptr.addToUEVarAndVarKill(UEVar, varKill);
                ptr = ptr.getNextInst();
            }

            block.setUEVar(UEVar);
            block.setVarKill(varKill);
        }

        for (int i = dfsOrder.size() - 1; i >= 0; i--) {
            ASMBlock block = dfsOrder.get(i);
            block.setLiveOut(new HashSet<>());
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = dfsOrder.size() - 1; i >= 0; i--) {
                ASMBlock block = dfsOrder.get(i);
                Set<VirtualASMRegister> liveOut = new HashSet<>();
                for (ASMBlock successor : block.getSuccessors()) {
                    Set<VirtualASMRegister> intersection = new HashSet<>(successor.getLiveOut());
                    intersection.removeAll(successor.getVarKill());

                    Set<VirtualASMRegister> union = new HashSet<>(successor.getUEVar());
                    union.addAll(intersection);

                    liveOut.addAll(union);
                }
                if (!block.getLiveOut().equals(liveOut)) {
                    block.setLiveOut(liveOut);
                    changed = true;
                }
            }
        }
    }



}

