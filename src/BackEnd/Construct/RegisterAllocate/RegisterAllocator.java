package BackEnd.Construct.RegisterAllocate;

import BackEnd.ASMBlock;
import BackEnd.Construct.ASMPass;
import BackEnd.Construct.LivenessAnalysis;
import BackEnd.Instruction.*;
import BackEnd.Instruction.BinaryInst.ITypeBinaryInst;
import BackEnd.Operand.ASMRegister.PhysicalASMRegister;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.StackLocation;
import BackEnd.Operand.Immediate.IntImmediate;
import BackEnd.RISCVFunction;
import IR.Module;
import Optimization.Loop.LoopAnalysis;

import java.util.*;

public class RegisterAllocator extends ASMPass {
    final static int inf = 99999999;
    final private int K = PhysicalASMRegister.allocatablePRs.size();

    private BackEnd.RISCVFunction RISCVFunction;
    private final LoopAnalysis loopAnalysis;


    public RegisterAllocator(BackEnd.RISCVModule RISCVModule, Module module) {
        super(RISCVModule);
        this.loopAnalysis = new LoopAnalysis(module);
    }
    private Set<VirtualASMRegister> preColoredNode;
    private Set<VirtualASMRegister> initial;
    private Set<VirtualASMRegister> simplifyWorkList;
    private Set<VirtualASMRegister> freezeWorkList;
    private Set<VirtualASMRegister> spillWorkList;
    private Set<VirtualASMRegister> spilledNodes;
    private Set<VirtualASMRegister> coalescedNodes;
    private Set<VirtualASMRegister> coloredNodes;
    private Stack<VirtualASMRegister> selectStack;

    private Set<ASMMoveInst> coalescedMoves;
    private Set<ASMMoveInst> constrainedMoves;
    private Set<ASMMoveInst> frozenMoves;
    private Set<ASMMoveInst> workListMoves;
    private Set<ASMMoveInst> activeMoves;

    private Set<directedEdge> adjacentSet;

    @Override
    public void run() {
        loopAnalysis.run();
        for (RISCVFunction RISCVFunction : RISCVModule.getFunctionMap().values())
            GraphColoring(RISCVFunction);
    }

    private boolean shouldAssignColor(){
        return simplifyWorkList.isEmpty() && workListMoves.isEmpty()
                && freezeWorkList.isEmpty() && spillWorkList.isEmpty();
    }

    private void GraphColoring(RISCVFunction RISCVFunction) {
        this.RISCVFunction = RISCVFunction;
        while (true) {
            initializeDataStructures();
            computeSpillCost();
            new LivenessAnalysis(RISCVModule).run();
            constructInterferenceGraph();
            makeWorkList();

            while (!shouldAssignColor()) {
                if (!simplifyWorkList.isEmpty())
                    simplify();
                else if (!workListMoves.isEmpty())
                    coalesce();
                else if (!freezeWorkList.isEmpty())
                    freeze();
                else
                    selectSpill();
            }
            assignColors();

            if (!spilledNodes.isEmpty())
                rewriteProgram();
            else
                break;
        }

        checkEveryVRHasAColor();
        removeRedundantMoveInst();
        RISCVFunction.getStackFrame().computeFrameSize();
        moveStackPointer();
    }

    private void initializeDataStructures() {
        preColoredNode = new HashSet<>();
        initial = new HashSet<>();
        simplifyWorkList = new LinkedHashSet<>();
        freezeWorkList = new LinkedHashSet<>();
        spillWorkList = new LinkedHashSet<>();
        spilledNodes = new HashSet<>();
        coalescedNodes = new HashSet<>();
        coloredNodes = new HashSet<>();
        selectStack = new Stack<>();

        coalescedMoves = new HashSet<>();
        constrainedMoves = new HashSet<>();
        frozenMoves = new HashSet<>();
        workListMoves = new LinkedHashSet<>();
        activeMoves = new HashSet<>();

        adjacentSet = new HashSet<>();


        initial.addAll(RISCVFunction.getAllVRSet());
        preColoredNode.addAll(PhysicalASMRegister.vrs.values());
        initial.removeAll(preColoredNode);


        for (VirtualASMRegister vr : initial)
            vr.clearColoringData();
        for (VirtualASMRegister vr : preColoredNode)
            vr.setDegree(inf);
    }

    private void computeSpillCost() {
        // \sum (10^depth * number of defs/uses).
        ArrayList<ASMBlock> dfsOrder = RISCVFunction.getDFSOrder();
        for (ASMBlock block : dfsOrder) {
            int depth = loopAnalysis.getBlockDepth(block);
            ASMInstruction ptr = block.getInstHead();
            while (ptr != null) {
                for (VirtualASMRegister def : ptr.getDef())
                    def.increaseSpillCost(Math.pow(10, depth));         //gugu changed
                for (VirtualASMRegister use : ptr.getUse())
                    use.increaseSpillCost(Math.pow(10, depth));
                ptr = ptr.getNextInst();
            }
        }
    }


    private void constructInterferenceGraph() {
        ArrayList<ASMBlock> dfsOrder = RISCVFunction.getDFSOrder();
        for (ASMBlock block : dfsOrder) {
            Set<VirtualASMRegister> live = block.getLiveOut();
            ASMInstruction ptr = block.getInstTail();
            while (ptr != null) {
                if (ptr instanceof ASMMoveInst) {
                    live.removeAll(ptr.getUse());
                    for (VirtualASMRegister n : ptr.getDefUseUnion())
                        n.getMoveList().add(((ASMMoveInst) ptr));
                    workListMoves.add(((ASMMoveInst) ptr));
                }

                live.add(PhysicalASMRegister.zeroVR);
                live.addAll(ptr.getDef());
                for (VirtualASMRegister d : ptr.getDef()) {
                    for (VirtualASMRegister l : live)
                        addEdge(l, d);
                }
                live.removeAll(ptr.getDef());
                live.addAll(ptr.getUse());

                ptr = ptr.getPrevInst();
            }
        }
    }

    private void addEdge(VirtualASMRegister u, VirtualASMRegister v) {
        if (!adjacentSet.contains(new directedEdge(u, v)) && u != v) {
            adjacentSet.add(new directedEdge(u, v));
            adjacentSet.add(new directedEdge(v, u));
            if (!preColoredNode.contains(u)) {
                u.getAdjList().add(v);
                u.increaseDegree();
            }
            if (!preColoredNode.contains(v)) {
                v.getAdjList().add(u);
                v.increaseDegree();
            }
        }
    }


    private void makeWorkList() {
        for (VirtualASMRegister n : initial) {
            if (n.getDegree() >= K)
                spillWorkList.add(n);
            else if (moveRelated(n))
                freezeWorkList.add(n);
            else
                simplifyWorkList.add(n);
        }
    }


    private Set<VirtualASMRegister> adjacent(VirtualASMRegister n) {
        Set<VirtualASMRegister> res = new HashSet<>(n.getAdjList());
        res.removeAll(selectStack);
        res.removeAll(coalescedNodes);
        return res;
    }


    private Set<ASMMoveInst> nodeMoves(VirtualASMRegister n) {
        Set<ASMMoveInst> res = new HashSet<>(activeMoves);
        res.addAll(workListMoves);
        res.retainAll(n.getMoveList());
        return res;
    }

    private boolean moveRelated(VirtualASMRegister n) {
        return !nodeMoves(n).isEmpty();
    }

    private void simplify() {
        assert !simplifyWorkList.isEmpty();
        VirtualASMRegister n = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(n);
        selectStack.push(n);
        for (VirtualASMRegister m : adjacent(n))
            decrementDegree(m);
    }

    private void decrementDegree(VirtualASMRegister m) {
        int d = m.getDegree();
        m.setDegree(d - 1);
        if (d == K) {
            Set<VirtualASMRegister> union = new HashSet<>(adjacent(m));
            union.add(m);
            enableMoves(union);
            spillWorkList.remove(m);
            if (moveRelated(m))
                freezeWorkList.add(m);
            else
                simplifyWorkList.add(m);
        }
    }

    private void enableMoves(Set<VirtualASMRegister> nodes) {
        for (VirtualASMRegister n : nodes) {
            for (ASMMoveInst m : nodeMoves(n)) {
                if (activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    workListMoves.add(m);
                }
            }
        }
    }

    private void addWorkList(VirtualASMRegister u) {
        if (!preColoredNode.contains(u) && !moveRelated(u) && u.getDegree() < K) {
            freezeWorkList.remove(u);
            simplifyWorkList.add(u);
        }
    }

    private boolean OK(VirtualASMRegister t, VirtualASMRegister r) {
        return t.getDegree() < K || preColoredNode.contains(t) || adjacentSet.contains(new directedEdge(t, r));
    }

    private boolean conservative(Set<VirtualASMRegister> nodes) {
        int k = 0;
        for (VirtualASMRegister n : nodes) {
            if (n.getDegree() >= K)
                k++;
        }
        return k < K;
    }

    private void coalesce() {
        assert !workListMoves.isEmpty();
        ASMMoveInst m = workListMoves.iterator().next();
        workListMoves.remove(m);
        VirtualASMRegister x = getAlias(m.getRd());
        VirtualASMRegister y = getAlias(m.getRs());

        VirtualASMRegister u;
        VirtualASMRegister v;
        if (preColoredNode.contains(y)) {
            u = y;
            v = x;
        } else {
            u = x;
            v = y;
        }

        Set<VirtualASMRegister> unionAdjacentNode = new HashSet<>(adjacent(u));
        unionAdjacentNode.addAll(adjacent(v));
        if (u == v) {
            coalescedMoves.add(m);
            addWorkList(u);
        } else if (preColoredNode.contains(v) || adjacentSet.contains(new directedEdge(u, v))) {
            constrainedMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        } else if ((preColoredNode.contains(u) && anyAdjacentNodeIsOK(v, u))
                || (!preColoredNode.contains(u) && conservative(unionAdjacentNode))) {
            coalescedMoves.add(m);
            combine(u, v);
            addWorkList(u);
        } else
            activeMoves.add(m);
    }

    private boolean anyAdjacentNodeIsOK(VirtualASMRegister v, VirtualASMRegister u) {
        for (VirtualASMRegister t : adjacent(v)) {
            if (!OK(t, u))
                return false;
        }
        return true;
    }

    private void combine(VirtualASMRegister u, VirtualASMRegister v) {
        if (freezeWorkList.contains(v))
            freezeWorkList.remove(v);
        else
            spillWorkList.remove(v);
        coalescedNodes.add(v);
        v.setAlias(u);
        u.getMoveList().addAll(v.getMoveList());

        Set<VirtualASMRegister> nodes = new HashSet<>();
        nodes.add(v);
        enableMoves(nodes);

        for (VirtualASMRegister t : adjacent(v)) {
            addEdge(t, u);
            decrementDegree(t);
        }
        if (u.getDegree() >= K && freezeWorkList.contains(u)) {
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }

    private VirtualASMRegister getAlias(VirtualASMRegister n) {
        if (coalescedNodes.contains(n)) {
            VirtualASMRegister alias = getAlias(n.getAlias());
            n.setAlias(alias);
            return alias;
        } else
            return n;
    }

    private void freeze() {
        VirtualASMRegister u = freezeWorkList.iterator().next();
        freezeWorkList.remove(u);
        simplifyWorkList.add(u);
        freezeMoves(u);
    }

    private void freezeMoves(VirtualASMRegister u) {
        for (ASMMoveInst m : nodeMoves(u)) {
            VirtualASMRegister x = m.getRd();
            VirtualASMRegister y = m.getRs();

            VirtualASMRegister v;
            if (getAlias(y) == getAlias(u))
                v = getAlias(x);
            else
                v = getAlias(y);
            activeMoves.remove(m);
            frozenMoves.add(m);

            if (freezeWorkList.contains(v) && nodeMoves(v).isEmpty()) { // In "Implementation in C",
                // v.getDegree() < K ?
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        }
    }

    private void selectSpill() {
        VirtualASMRegister m = selectVRToBeSpilled();
        spillWorkList.remove(m);
        simplifyWorkList.add(m);
        freezeMoves(m);
    }

    private VirtualASMRegister selectVRToBeSpilled() {
        double minRatio = Double.POSITIVE_INFINITY;
        VirtualASMRegister spilledVR = null;
        for (VirtualASMRegister vr : spillWorkList) {
            double spillRatio = vr.computeSpillRatio();
            if (spillRatio <= minRatio) {
                minRatio = spillRatio;
                spilledVR = vr;
            }
        }
        assert spilledVR != null;
        return spilledVR;
    }

    private void assignColors() {
        while (!selectStack.isEmpty()) {
            VirtualASMRegister n = selectStack.pop();
            Set<PhysicalASMRegister> okColors = new LinkedHashSet<>(PhysicalASMRegister.allocatablePRs.values());
            for (VirtualASMRegister w : n.getAdjList()) {
                Set<VirtualASMRegister> union = new HashSet<>(coloredNodes);
                union.addAll(preColoredNode);
                if (union.contains(getAlias(w)))
                    okColors.remove(getAlias(w).getColorPR());
            }

            if (okColors.isEmpty())
                spilledNodes.add(n);
            else {
                coloredNodes.add(n);
                PhysicalASMRegister c = selectColor(okColors);
                n.setColorPR(c);
            }
        }
        for (VirtualASMRegister n : coalescedNodes)
            n.setColorPR(getAlias(n).getColorPR());
    }

    // caller-save registers being selected first.
    private PhysicalASMRegister selectColor(Set<PhysicalASMRegister> okColors) {
        assert !okColors.isEmpty();
        for (PhysicalASMRegister pr : okColors) {
            if (PhysicalASMRegister.callerSavePRs.containsKey(pr.getName()))
                return pr;
        }
        return okColors.iterator().next();
    }

    private void rewriteProgram() {
        for (VirtualASMRegister vr : spilledNodes) {
            StackLocation stackLocation = new StackLocation(vr.getName());
            RISCVFunction.getStackFrame().getSpillLocations().put(vr, stackLocation);
            Set<ASMInstruction> defs = new HashSet<>(vr.getDef().keySet());
            Set<ASMInstruction> uses = new HashSet<>(vr.getUse().keySet());

            int cnt = 0;
            for (ASMInstruction inst : defs) {
                VirtualASMRegister spilledVR = new VirtualASMRegister(vr.getName() + ".spill" + cnt);
                RISCVFunction.registerVRDuplicateName(spilledVR);
                cnt++;

                ASMBlock block = inst.getASMBlock();
                inst.replaceDef(vr, spilledVR);
                block.addInstructionNext(inst, new ASMStoreInst(block, spilledVR, ASMStoreInst.ByteType.sw, stackLocation));
            }
            for (ASMInstruction inst : uses) {
                VirtualASMRegister spilledVR = new VirtualASMRegister(vr.getName() + ".spill" + cnt);
                RISCVFunction.registerVRDuplicateName(spilledVR);
                cnt++;

                ASMBlock block = inst.getASMBlock();
                inst.replaceUse(vr, spilledVR);
                block.addInstructionPrev(inst, new ASMLoadInst(block, spilledVR, ASMLoadInst.ByteSize.lw, stackLocation));
            }
            assert vr.getDef().isEmpty() && vr.getUse().isEmpty();
            RISCVFunction.removeVR(vr);
        }
    }

    private void checkEveryVRHasAColor() {
        ArrayList<ASMBlock> dfsOrder = RISCVFunction.getDFSOrder();
        for (ASMBlock block : dfsOrder) {
            ASMInstruction ptr = block.getInstHead();
            while (ptr != null) {
                for (VirtualASMRegister vr : ptr.getDef())
                    assert vr.hasAColor();
                for (VirtualASMRegister vr : ptr.getUse())
                    assert vr.hasAColor();
                ptr = ptr.getNextInst();
            }
        }
    }

    private void removeRedundantMoveInst() {
        ArrayList<ASMBlock> dfsOrder = RISCVFunction.getDFSOrder();
        for (ASMBlock block : dfsOrder) {
            ASMInstruction ptr = block.getInstHead();
            while (ptr != null) {
                ASMInstruction next = ptr.getNextInst();
                if (ptr instanceof ASMMoveInst
                        && ((ASMMoveInst) ptr).getRd().getColorPR() == ((ASMMoveInst) ptr).getRs().getColorPR()) {
                    ((ASMMoveInst) ptr).removeFromBlock();
                }
                ptr = next;
            }
        }
    }

    private void moveStackPointer() {
        int frameSize = RISCVFunction.getStackFrame().getSize();
        if (frameSize == 0)
            return;

        VirtualASMRegister sp = PhysicalASMRegister.vrs.get("sp");
        RISCVFunction.getEntranceBlock().addInstructionAtFront(new ITypeBinaryInst(RISCVFunction.getEntranceBlock(),
                ITypeBinaryInst.OpName.addi, sp, new IntImmediate(-frameSize * 4), sp));

        for (ASMBlock block : RISCVFunction.getBlocks()) {
            if (block.getInstTail() instanceof ASMReturnInst) {
                block.addInstructionPrev(block.getInstTail(), new ITypeBinaryInst(block,
                        ITypeBinaryInst.OpName.addi, sp, new IntImmediate(frameSize * 4), sp));
                break;
            }
        }
    }
}