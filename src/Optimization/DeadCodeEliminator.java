package Optimization;

import IR.Block;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.Module;
import Optimization.Loop.LoopAnalysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class DeadCodeEliminator extends Pass {
    private SideEffectChecker sideEffectChecker;
    private LoopAnalysis loopAnalysis;

    public DeadCodeEliminator(Module module, SideEffectChecker sideEffectChecker, LoopAnalysis loopAnalysis) {
        super(module);
        this.sideEffectChecker = sideEffectChecker;
        this.loopAnalysis = loopAnalysis;
    }

    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {
            if (!function.isFunctional())
                return false;
        }

        changed = false;
        sideEffectChecker.setIgnoreIO(false);
        sideEffectChecker.setIgnoreLoad(true);
        sideEffectChecker.run();

        for (LLVMfunction function : module.getFunctionMap().values())
            changed |= deadCodeElimination(function);
        return changed;
    }

    private boolean deadCodeElimination(LLVMfunction function) {
        Set<LLVMInstruction> live = new HashSet<>();
        Queue<LLVMInstruction> queue = new LinkedList<>();
        for (Block block : function.getBlocks())
            addLiveInstructions(block, live, queue);

        while (!queue.isEmpty()) {
            LLVMInstruction instruction = queue.poll();
            instruction.markUseAsLive(live, queue);
            for (Block block : instruction.getBlock().getPostDF()) {
                assert block.getInstTail() instanceof BranchInst;
                if (!live.contains(block.getInstTail())) {
                    live.add(block.getInstTail());
                    queue.offer(block.getInstTail());
                }
            }
        }

        boolean changed = false;
        for (Block block : function.getBlocks())
            changed |= removeDeadInstructions(block, live);
        return changed;
    }

    private void addLiveInstructions(Block block, Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {
        LLVMInstruction ptr = block.getInstHead();
        while (ptr != null) {
            if (ptr instanceof StoreInst) {
                live.add(ptr);
                queue.offer(ptr);
                if (!live.contains(ptr.getBlock().getInstTail())) {
                    live.add(ptr.getBlock().getInstTail());
                    queue.offer(ptr.getBlock().getInstTail());
                }
            } else if (ptr instanceof CallInst) {
                if (sideEffectChecker.hasSideEffect(((CallInst) ptr).getLlvMfunction())) {
                    live.add(ptr);
                    queue.offer(ptr);
                    if (!live.contains(ptr.getBlock().getInstTail())) {
                        live.add(ptr.getBlock().getInstTail());
                        queue.offer(ptr.getBlock().getInstTail());
                    }
                }
            } else if (ptr instanceof ReturnInst) {
                live.add(ptr);
                queue.offer(ptr);
                if (!live.contains(ptr.getBlock().getInstTail())) {
                    live.add(ptr.getBlock().getInstTail());
                    queue.offer(ptr.getBlock().getInstTail());
                }
            }
            ptr = ptr.getPostInst();
        }
    }

    private boolean removeDeadInstructions(Block block, Set<LLVMInstruction> live) {
        LLVMInstruction ptr = block.getInstHead();
        boolean changed = false;
        while (ptr != null) {
            if (!live.contains(ptr))
                changed |= ptr.dceRemoveFromBlock(loopAnalysis);
            ptr = ptr.getPostInst();
        }
        return changed;
    }
}
