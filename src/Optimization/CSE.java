package Optimization;

import IR.Block;
import IR.Instruction.CallInst;
import IR.Instruction.LLVMInstruction;
import IR.Instruction.LoadInst;
import IR.Instruction.StoreInst;
import IR.LLVMfunction;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.Module;

import java.util.*;

public class CSE extends Pass{
    private PointerAnalysis pointerAnalysis;
    private SideEffectChecker sideEffectChecker;
    private Map<Expression, ArrayList<Register>> expressionMap;
    private Map<LoadInst, Set<LLVMInstruction>> unavailable;

    static public class Expression {
        private String instructionName;
        private ArrayList<String> operands;

        public Expression(String instructionName, ArrayList<String> operands) {
            this.instructionName = instructionName;
            this.operands = operands;
        }

        public String getInstructionName() {
            return instructionName;
        }

        public boolean isCommutable() {
            return instructionName.equals("add")
                    || instructionName.equals("mul")
                    || instructionName.equals("and")
                    || instructionName.equals("or")
                    || instructionName.equals("xor")
                    || instructionName.equals("eq")
                    || instructionName.equals("ne")
                    || instructionName.equals("sgt")
                    || instructionName.equals("sge")
                    || instructionName.equals("slt")
                    || instructionName.equals("sle");
        }

        public Expression getCommutation() {
            assert operands.size() == 2;
            ArrayList<String> newOperands = new ArrayList<>();
            newOperands.add(operands.get(1));
            newOperands.add(operands.get(0));
            switch (instructionName) {
                case "sgt":
                    return new Expression("slt", newOperands);
                case "sge":
                    return new Expression("sle", newOperands);
                case "slt":
                    return new Expression("sgt", newOperands);
                case "sle":
                    return new Expression("sge", newOperands);
                default:
                    return new Expression(instructionName, newOperands);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Expression))
                return false;
            if (!((Expression) obj).getInstructionName().equals(this.instructionName))
                return false;
            if (((Expression) obj).operands.size() != this.operands.size())
                return false;
            for (int i = 0; i < this.operands.size(); i++) {
                if (!((Expression) obj).operands.get(i).equals(this.operands.get(i)))
                    return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    public CSE(Module module, PointerAnalysis pointerAnalysis, SideEffectChecker sideEffectChecker) {
        super(module);
        this.pointerAnalysis = pointerAnalysis;
        this.sideEffectChecker = sideEffectChecker;
    }

    @Override
    public boolean run() {
        for (LLVMfunction function : module.getFunctionMap().values()) {
            if (!function.isFunctional())
                return false;
        }

        sideEffectChecker.setIgnoreIO(true);
        sideEffectChecker.setIgnoreLoad(true);
        sideEffectChecker.run();

        changed = false;
        for (LLVMfunction function : module.getFunctionMap().values())
            changed |= commonSubexpressionElimination(function);
        return false;
    }

    private boolean commonSubexpressionElimination(LLVMfunction function) {
        if (!function.isFunctional())
            return false;
        boolean changed = false;
        expressionMap = new HashMap<>();
        unavailable = new HashMap<>();

        ArrayList<Block> blocks = function.getDFSOrder();
        for (Block block : blocks)
            changed |= commonSubexpressionElimination(block);
        return changed;
    }

    private boolean commonSubexpressionElimination(Block block) {
        boolean changed = false;
        LLVMInstruction ptr = block.getInstHead();
        while (ptr != null) {
            LLVMInstruction next = ptr.getPostInst();
            if (ptr.canConvertToExpression()) {
                Expression expression = ptr.convertToExpression();
                Register register = lookupExpression(expression, ptr, block);
                if (register != null) {
                    ptr.getResult().beOverriden(register);
                    ptr.removeFromBlock();
                    changed = true;
                } else {
                    putExpression(expression, ptr.getResult());
                    if (expression.isCommutable())
                        putExpression(expression.getCommutation(), ptr.getResult());

                    if (ptr instanceof LoadInst)
                        propagateUnavailability((LoadInst) ptr);
                }
            }
            ptr = next;
        }
        return changed;
    }

    private Register lookupExpression(Expression expression, LLVMInstruction instruction, Block block) {
        if (!expressionMap.containsKey(expression))
            return null;
        ArrayList<Register> registers = expressionMap.get(expression);
        for (Register register : registers) {
            LLVMInstruction def = register.getDef();
            if (expression.instructionName.equals("load")) {
                assert def instanceof LoadInst;
                assert unavailable.containsKey(def);
                if (def.getBlock().dominate(block) && !unavailable.get(def).contains(instruction))
                    return register;
            } else {
                if (def.getBlock().dominate(block))
                    return register;
            }
        }
        return null;
    }

    private void putExpression(Expression expression, Register register) {
        if (!expressionMap.containsKey(expression))
            expressionMap.put(expression, new ArrayList<>());
        expressionMap.get(expression).add(register);
    }

    private void markSuccessorUnavailable(LoadInst loadInst, LLVMInstruction instruction,
                                          Set<LLVMInstruction> unavailable, Queue<LLVMInstruction> queue) {
        Block block = instruction.getBlock();
        if (instruction == block.getInstTail()) {
            for (Block successor : block.getSuccessors()) {
                if (successor.getStrictDominators().contains(loadInst.getBlock())) {
                    LLVMInstruction instHead = successor.getInstHead();
                    if (!unavailable.contains(instHead)) {
                        unavailable.add(instHead);
                        queue.offer(instHead);
                    }
                }
            }
        } else {
            LLVMInstruction instNext = instruction.getPostInst();
            if (!unavailable.contains(instNext)) {
                unavailable.add(instNext);
                queue.offer(instNext);
            }
        }
    }

    private void propagateUnavailability(LoadInst loadInst) {
        Set<LLVMInstruction> unavailable = new HashSet<>();
        Queue<LLVMInstruction> queue = new LinkedList<>();

        Operand loadPointer = loadInst.getAddr();
        Block loadBlock = loadInst.getBlock();
        LLVMfunction function = loadBlock.getFunction();

        for (Block block : function.getBlocks()) {
            if (loadInst.getBlock().dominate(block)) {
                LLVMInstruction ptr = loadBlock == block ? loadInst.getPostInst() : block.getInstHead();
                while (ptr != null) {
                    if (ptr instanceof StoreInst) {
                        if (pointerAnalysis.mayAlias(loadPointer, ((StoreInst) ptr).getAddr()))
                            markSuccessorUnavailable(loadInst, ptr, unavailable, queue);
                    } else if (ptr instanceof CallInst) {
                        LLVMfunction callee = ((CallInst) ptr).getLlvMfunction();
                        if (sideEffectChecker.hasSideEffect(callee))
                            markSuccessorUnavailable(loadInst, ptr, unavailable, queue);
                    }
                    ptr = ptr.getPostInst();
                }
            }
        }

        while (!queue.isEmpty()) {
            LLVMInstruction inst = queue.poll();
            markSuccessorUnavailable(loadInst, inst, unavailable, queue);
        }
        this.unavailable.put(loadInst, unavailable);
    }
}
