package Optimization;

import IR.Block;
import IR.Instruction.DefineGlobal;
import IR.Instruction.LLVMInstruction;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.GlobalVar;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;
import IR.TypeSystem.LLVMPointerType;

import java.util.*;

public class PointerAnalysis extends Pass{
    static public int TOT = 0;
    static public int CNT = 0;

    public static class Node {
        private String name;
        private Set<Node> pointsTo;         // a -> b if loc(b) is in pts(a)
        private Set<Node> inclusiveEdge;    // a -> b if a <= b
        private Set<Node> dereferenceLhs;   // a -> b if *a <= b
        private Set<Node> dereferenceRhs;   // a -> b if b <= *a

        public Node(String name) {
            this.name = name;
            pointsTo = new HashSet<>();
            inclusiveEdge = new HashSet<>();
            dereferenceLhs = new HashSet<>();
            dereferenceRhs = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public Set<Node> getPointsTo() {
            return pointsTo;
        }

        public Set<Node> getInclusiveEdge() {
            return inclusiveEdge;
        }

        public Set<Node> getDereferenceLhs() {
            return dereferenceLhs;
        }

        public Set<Node> getDereferenceRhs() {
            return dereferenceRhs;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private Set<Node> nodes;
    private Map<Operand, Node> nodeMap;

    public PointerAnalysis(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {
            if (!function.isFunctional())
                return false;
        }

        nodes = new HashSet<>();
        nodeMap = new HashMap<>();
        constructNode();
        addConstraints();
        runAndersen();
        return false;
    }

    private void constructNode() {
        for(DefineGlobal defineGlobal : module.getDefineGlobals()){
            GlobalVar globalVar = defineGlobal.getGlobalVar();
            Node node = new Node(globalVar.toString());
            nodeMap.put(globalVar, node);
            nodes.add(node);
        }

        for (LLVMfunction function : module.getFunctionMap().values()) {
            for (Register parameter : function.getParas()) {
                if (parameter.getLlvMtype() instanceof LLVMPointerType) {
                    String paraName = function.getFunctionName() + parameter.toString();
                    Node node = new Node(paraName);
                    nodeMap.put(parameter, node);
                    nodes.add(node);
                }
            }

            for (Block block : function.getBlocks()) {
                LLVMInstruction ptr = block.getInstHead();
                while (ptr != null) {
                    if (ptr.hasResult()) {
                        Register result = ptr.getResult();
                        if (result.getLlvMtype() instanceof LLVMPointerType) {
                            String resultName = function.getFunctionName() + result.toString();
                            Node node = new Node(resultName);
                            nodeMap.put(result, node);
                            nodes.add(node);
                        }
                    }
                    ptr = ptr.getPostInst();
                }
            }
        }
    }

    private void addConstraints() {
        for(DefineGlobal defineGlobal : module.getDefineGlobals()){
            GlobalVar globalVar = defineGlobal.getGlobalVar();
            Node pointer = nodeMap.get(globalVar);
            Node pointTo = new Node(pointer.getName() + ".globalValue");
            pointer.getPointsTo().add(pointTo);
            nodes.add(pointTo);
        }


        for (LLVMfunction function : module.getFunctionMap().values()) {
            for (Block block : function.getBlocks()) {
                LLVMInstruction ptr = block.getInstHead();
                while (ptr != null) {
                    ptr.addConstraintsForAndersen(nodeMap, nodes);
                    ptr = ptr.getPostInst();
                }
            }
        }
    }

    private void runAndersen() {
        Queue<Node> queue = new LinkedList<>();
        Set<Node> inQueue = new HashSet<>();
        for (Node node : nodes) {
            if (!node.getPointsTo().isEmpty()) {
                queue.offer(node);
                inQueue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            inQueue.remove(node);
            for (Node pointTo : node.getPointsTo()) {
                for (Node lhs : node.getDereferenceLhs()) {
                    if (!pointTo.getInclusiveEdge().contains(lhs)) {
                        pointTo.getInclusiveEdge().add(lhs);
                        if (!inQueue.contains(pointTo)) {
                            queue.offer(pointTo);
                            inQueue.add(pointTo);
                        }
                    }
                }
                for (Node rhs : node.getDereferenceRhs()) {
                    if (!rhs.getInclusiveEdge().contains(pointTo)) {
                        rhs.getInclusiveEdge().add(pointTo);
                        if (!inQueue.contains(rhs)) {
                            queue.offer(rhs);
                            inQueue.add(rhs);
                        }
                    }
                }
            }
            for (Node inclusive : node.getInclusiveEdge()) {
                if (inclusive.pointsTo.addAll(node.pointsTo)) {
                    if (!inQueue.contains(inclusive)) {
                        queue.offer(inclusive);
                        inQueue.add(inclusive);
                    }
                }
            }
        }
    }

    public boolean mayAlias(Operand op1, Operand op2) {
        if (op1 instanceof ConstNull || op2 instanceof ConstNull)
            return false;
//        TOT++;
        if (!op1.getLlvMtype().equals(op2.getLlvMtype()))
            return false;
        assert nodeMap.containsKey(op1);
        assert nodeMap.containsKey(op2);
        Set<Node> pointsTo1 = nodeMap.get(op1).getPointsTo();
        Set<Node> pointsTo2 = nodeMap.get(op2).getPointsTo();
//        if (!Collections.disjoint(pointsTo1, pointsTo2))
//            CNT++;
        return !Collections.disjoint(pointsTo1, pointsTo2);
    }
}
