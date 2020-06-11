package BackEnd.Construct;

import BackEnd.ASMBlock;
import BackEnd.Instruction.*;
import BackEnd.Instruction.BinaryInst.ITypeBinary;
import BackEnd.Operand.ASMRegister.PhysicalASMRegister;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.StackLocation;
import BackEnd.Operand.Immediate.IntImmediate;
import BackEnd.RISCVFunction;
import Optimization.LoopAnalysis;
import Utility.Pair;

import java.util.*;

public class RegisterAllocator extends ASMPass {
    private static class Edge extends Pair<VirtualASMRegister, VirtualASMRegister> {
        public Edge(VirtualASMRegister first, VirtualASMRegister second) {
            super(first, second);
            if (first.hashCode() > second.hashCode()) {
                setFirst(second);
                setSecond(first);
            }
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Edge))
                return false;
            return toString().equals(obj.toString());
        }

        @Override
        public String toString() {
            return "(" + getFirst().getName() + ", " + getSecond().getName() + ")";
        }
    }

    final private int K = PhysicalASMRegister.allocatablePRs.size();
    // K represents the number of allocatable physical registers.

    private BackEnd.RISCVFunction RISCVFunction;
    private final LoopAnalysis loopAnalysis;


    public RegisterAllocator(BackEnd.RISCVModule RISCVModule, LoopAnalysis loopAnalysis) {
        super(RISCVModule);
        this.loopAnalysis = loopAnalysis;
    }

    // ------ Data Structures ------
    private Set<VirtualASMRegister> preColored;
    private Set<VirtualASMRegister> initial;
    private Set<VirtualASMRegister> simplifyWorkList;
    private Set<VirtualASMRegister> freezeWorkList;
    private Set<VirtualASMRegister> spillWorkList;
    private Set<VirtualASMRegister> spilledNodes;
    private Set<VirtualASMRegister> coalescedNodes;
    private Set<VirtualASMRegister> coloredNodes;
    private Stack<VirtualASMRegister> selectStack;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Set<ASMMoveInst> coalescedMoves;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Set<ASMMoveInst> constrainedMoves;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Set<ASMMoveInst> frozenMoves;
    private Set<ASMMoveInst> workListMoves;
    private Set<ASMMoveInst> activeMoves;

    private Set<Edge> adjSet;
    // adjList is contained in every VirtualRegister.
    // degree is contained in every VirtualRegister.
    // moveList is contained in every VirtualRegister.
    // alias is contained in every VirtualRegister.
    // color is contained in every VirtualRegister.

    // ------ Data Structure End ------

    @Override
    public void run() {
        for (RISCVFunction RISCVFunction : RISCVModule.getFunctionMap().values())
            runGraphColoring(RISCVFunction);
    }

    private void runGraphColoring(RISCVFunction RISCVFunction) {
        this.RISCVFunction = RISCVFunction;
        while (true) {
            initializeDataStructures();
            computeSpillCost();
            new LivenessAnalysis(RISCVModule).run();
            build();
            makeWorkList();

            while (!simplifyWorkList.isEmpty()
                    || !workListMoves.isEmpty()
                    || !freezeWorkList.isEmpty()
                    || !spillWorkList.isEmpty()) {
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
        preColored = new HashSet<>();
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

        adjSet = new HashSet<>();


        initial.addAll(RISCVFunction.getSymbolTable().getAllVRs());
        preColored.addAll(PhysicalASMRegister.vrs.values());
        initial.removeAll(preColored);


        for (VirtualASMRegister vr : initial)
            vr.clearColoringData();
        int inf = 1000000000;
        for (VirtualASMRegister vr : preColored)
            vr.setDegree(inf);
    }

    // Compute the spill cost of every virtual register(\sum (10^depth * number of defs/uses)).
    private void computeSpillCost() {
        ArrayList<ASMBlock> dfsOrder = RISCVFunction.getDFSOrder();
        for (ASMBlock block : dfsOrder) {
            int depth = loopAnalysis.getBlockDepth(block);
            ASMInstruction ptr = block.getInstHead();
            while (ptr != null) {
                for (VirtualASMRegister def : ptr.getDef())
                    def.increaseSpillCost(Math.pow(10, depth));
                for (VirtualASMRegister use : ptr.getUse())
                    use.increaseSpillCost(Math.pow(10, depth));
                ptr = ptr.getNextInst();
            }
        }
    }

    // Build interference graph.
    private void build() {
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

    // Add edges (u, v) & (v, u) to interference graph.
    private void addEdge(VirtualASMRegister u, VirtualASMRegister v) {
        if (!adjSet.contains(new Edge(u, v)) && u != v) {
            adjSet.add(new Edge(u, v));
            adjSet.add(new Edge(v, u));
            if (!preColored.contains(u)) {
                u.getAdjList().add(v);
                u.increaseDegree();
            }
            if (!preColored.contains(v)) {
                v.getAdjList().add(u);
                v.increaseDegree();
            }
        }
    }

    // For each virtual register which is not pre-colored, add it to one of the work lists.
    private void makeWorkList() {
        for (VirtualASMRegister n : initial) {
            if (n.getDegree() >= K)
                spillWorkList.add(n);
            else if (moveRelated(n))
                freezeWorkList.add(n);
            else
                simplifyWorkList.add(n);
        }
        // We don't have to clear "initial".
    }

    // Get the current neighbors of a virtual register n.
    private Set<VirtualASMRegister> adjacent(VirtualASMRegister n) {
        Set<VirtualASMRegister> res = new HashSet<>(n.getAdjList());
        res.removeAll(selectStack);
        res.removeAll(coalescedNodes);
        return res;
    }

    // Get the current move instructions related to a virtual register n.
    private Set<ASMMoveInst> nodeMoves(VirtualASMRegister n) {
        Set<ASMMoveInst> res = new HashSet<>(activeMoves);
        res.addAll(workListMoves);
        res.retainAll(n.getMoveList());
        return res;
    }

    // Check whether a virtual register n has related move instructions.
    private boolean moveRelated(VirtualASMRegister n) {
        return !nodeMoves(n).isEmpty();
    }

    // Remove a node whose current degree is no more than K from the interference graph.
    private void simplify() {
        assert !simplifyWorkList.isEmpty();
        VirtualASMRegister n = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(n);
        selectStack.push(n);
        for (VirtualASMRegister m : adjacent(n))
            decrementDegree(m);
    }

    // Decrease the degree of m by 1.
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

    // Move some move instructions related to virtual register in nodes to workListMoves.
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

    // Move a virtual register u from freezeWorkList to simplifyWorkList.
    private void addWorkList(VirtualASMRegister u) {
        if (!preColored.contains(u) && !moveRelated(u) && u.getDegree() < K) {
            freezeWorkList.remove(u);
            simplifyWorkList.add(u);
        }
    }

    // George's condition for conservative coalescing.
    private boolean OK(VirtualASMRegister t, VirtualASMRegister r) {
        return t.getDegree() < K || preColored.contains(t) || adjSet.contains(new Edge(t, r));
    }

    // Briggs's condition for conservative coalescing.
    private boolean conservative(Set<VirtualASMRegister> nodes) {
        int k = 0;
        for (VirtualASMRegister n : nodes) {
            if (n.getDegree() >= K)
                k++;
        }
        return k < K;
    }

    // Try to coalesce rd and rs of a move instruction in workListMoves.
    private void coalesce() {
        assert !workListMoves.isEmpty();
        ASMMoveInst m = workListMoves.iterator().next();
        workListMoves.remove(m);
        VirtualASMRegister x = getAlias(m.getRd());
        VirtualASMRegister y = getAlias(m.getRs());

        VirtualASMRegister u;
        VirtualASMRegister v;
        if (preColored.contains(y)) {
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
        } else if (preColored.contains(v) || adjSet.contains(new Edge(u, v))) {
            constrainedMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        } else if ((preColored.contains(u) && anyAdjacentNodeIsOK(v, u))
                || (!preColored.contains(u) && conservative(unionAdjacentNode))) {
            coalescedMoves.add(m);
            combine(u, v);
            addWorkList(u);
        } else
            activeMoves.add(m);
    }

    // Check whether any adjacent node of v "t" satisfies that OK(t, u) is true.
    private boolean anyAdjacentNodeIsOK(VirtualASMRegister v, VirtualASMRegister u) {
        for (VirtualASMRegister t : adjacent(v)) {
            if (!OK(t, u))
                return false;
        }
        return true;
    }

    // Coalesce virtual registers u and v, where u may be pre-colored.
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

    // Get the alias of n. It is just a union-find set, so path contraction can be applied.
    private VirtualASMRegister getAlias(VirtualASMRegister n) {
        if (coalescedNodes.contains(n)) {
            VirtualASMRegister alias = getAlias(n.getAlias());
            n.setAlias(alias);
            return alias;
        } else
            return n;
    }

    // Try to freeze a virtual register so that coalescing is given up.
    private void freeze() {
        VirtualASMRegister u = freezeWorkList.iterator().next();
        freezeWorkList.remove(u);
        simplifyWorkList.add(u);
        freezeMoves(u);
    }

    // Freeze a virtual register u.
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

    // Select a virtual register from spillWorkList and then spill it.
    private void selectSpill() {
        VirtualASMRegister m = selectVRToBeSpilled();
        spillWorkList.remove(m);
        simplifyWorkList.add(m);
        freezeMoves(m);
    }

    // Select a optimal virtual register to spill using spill metric.
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
                union.addAll(preColored);
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

    // Select an unused physical register, with caller-save registers always being selected first.
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
                RISCVFunction.getSymbolTable().putASMRename(spilledVR.getName(), spilledVR);
                cnt++;

                ASMBlock block = inst.getASMBlock();
                inst.replaceDef(vr, spilledVR);
                block.addInstructionNext(inst, new ASMStoreInst(block, spilledVR, ASMStoreInst.ByteSize.sw, stackLocation));
            }
            for (ASMInstruction inst : uses) {
                VirtualASMRegister spilledVR = new VirtualASMRegister(vr.getName() + ".spill" + cnt);
                RISCVFunction.getSymbolTable().putASMRename(spilledVR.getName(), spilledVR);
                cnt++;

                ASMBlock block = inst.getASMBlock();
                inst.replaceUse(vr, spilledVR);
                block.addInstructionPrev(inst, new ASMLoadInst(block, spilledVR, ASMLoadInst.ByteSize.lw, stackLocation));
            }
            assert vr.getDef().isEmpty() && vr.getUse().isEmpty();
            RISCVFunction.getSymbolTable().removeVR(vr);
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
        RISCVFunction.getEntranceBlock().addInstructionAtFront(new ITypeBinary(RISCVFunction.getEntranceBlock(),
                ITypeBinary.OpName.addi, sp, new IntImmediate(-frameSize * 4), sp));

        for (ASMBlock block : RISCVFunction.getBlocks()) {
            if (block.getInstTail() instanceof ASMReturnInst) {
                block.addInstructionPrev(block.getInstTail(), new ITypeBinary(block,
                        ITypeBinary.OpName.addi, sp, new IntImmediate(frameSize * 4), sp));
                break;
            }
        }
    }
}