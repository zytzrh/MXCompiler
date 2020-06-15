package Optimization.ConstOptim;

import IR.Block;
import IR.IRVisitor;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.*;
import IR.Module;
import IR.TypeSystem.LLVMIntType;
import Optimization.IRPass;
import Utility.Pair;

import java.util.*;

public class ConstPropagation extends IRPass implements IRVisitor {

    private Queue<Register> registerWorkList;
    private Queue<Block> blockWorkList;
    private Map<Operand, OpStatus> operandStatusMap;
    private Set<Block> reachableBlocks;

    public ConstPropagation(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;
        for (LLVMfunction function : module.getFunctionMap().values())
            visit(function);
        return changed;
    }

    @Override
    public void visit(LLVMfunction function) {
        registerWorkList = new LinkedList<>();
        blockWorkList = new LinkedList<>();
        operandStatusMap = new HashMap<>();
        reachableBlocks = new HashSet<>();

        markReachable(function.getInitBlock());
        while (!registerWorkList.isEmpty() || !blockWorkList.isEmpty()) {
            while (!blockWorkList.isEmpty()) {
                Block block = blockWorkList.poll();
                block.accept(this);
            }

            while (!registerWorkList.isEmpty()) {
                Register register = registerWorkList.poll();
                assert operandStatusMap.containsKey(register);
                for (LLVMInstruction instruction : register.getUse().keySet()) {
                    assert register.getUse().get(instruction) != 0;
                    instruction.accept(this);
                }
            }
        }

        boolean functionChanged = false;
        ArrayList<Block> blocks = function.getBlocks();
        for (Block block : blocks)
            functionChanged |= replaceRegisterWithConstant(block);

        changed |= functionChanged;
    }

    @Override
    public void visit(BinaryOpInst inst) {
        Operand op1 = inst.getLhs();
        Operand op2 = inst.getRhs();
        OpStatus op1OpStatus = getNowStatus(op1);
        OpStatus op2OpStatus = getNowStatus(op2);

        if (op1OpStatus.getStatus() == OpStatus.Status.constant
                && op2OpStatus.getStatus() == OpStatus.Status.constant) {
            Constant op1Constant = (Constant) op1OpStatus.getOperand();
            Constant op2Constant = (Constant) op2OpStatus.getOperand();
            Constant foldResult = foldBinaryInstConstant(inst, op1Constant, op2Constant);
            if (foldResult != null) {
                markConstant(inst.getResult(), foldResult);
            }
        } else if (op1OpStatus.getStatus() == OpStatus.Status.multiDefined
                || op2OpStatus.getStatus() == OpStatus.Status.multiDefined)
            markMultiDefined(inst.getResult());
    }

    @Override
    public void visit(IcmpInst inst) {
        Operand op1 = inst.getOp1();
        Operand op2 = inst.getOp2();
        OpStatus op1OpStatus = getNowStatus(op1);
        OpStatus op2OpStatus = getNowStatus(op2);

        if (op1OpStatus.getStatus() == OpStatus.Status.constant
                && op2OpStatus.getStatus() == OpStatus.Status.constant) {
            Constant op1Constant = (Constant) op1OpStatus.getOperand();
            Constant op2Constant = (Constant) op2OpStatus.getOperand();
            Constant foldResult = foldIcmpConstant(inst, op1Constant, op2Constant);
            markConstant(inst.getResult(), foldResult);
        } else if (op1OpStatus.getStatus() == OpStatus.Status.multiDefined
                || op2OpStatus.getStatus() == OpStatus.Status.multiDefined)
            markMultiDefined(inst.getResult());
    }

    @Override
    public void visit(BitCastInst inst) {
        OpStatus sourceOpStatus = getNowStatus(inst.getSource());
        if (sourceOpStatus.getStatus() == OpStatus.Status.constant) {
            Constant constant;
            if (sourceOpStatus.getOperand() instanceof ConstNull)
                constant = new ConstNull();
            else
                constant = ((Constant) sourceOpStatus.getOperand()).castToType(inst.getObjectType());
            markConstant(inst.getResult(), constant);
        } else if (sourceOpStatus.getStatus() == OpStatus.Status.multiDefined)
            markMultiDefined(inst.getResult());
    }

    @Override
    public void visit(BranchInst inst) {
        if (inst.getCondition() == null)
            markReachable(inst.getIfTrueBlock());
        else {
            Operand cond = inst.getCondition();
            OpStatus condOpStatus = getNowStatus(cond);

            if (condOpStatus.getStatus() == OpStatus.Status.constant) {
                ConstBool constBool = (ConstBool) condOpStatus.getOperand();
                boolean conditionValue = constBool.getValue();
                if (conditionValue)
                    markReachable(inst.getIfTrueBlock());
                else
                    markReachable(inst.getIfFalseBlock());
            } else if (condOpStatus.getStatus() == OpStatus.Status.multiDefined) {
                markReachable(inst.getIfTrueBlock());
                markReachable(inst.getIfFalseBlock());
            }
        }
    }


    @Override
    public void visit(PhiInst inst) {
        Constant constant = null;
        for (Pair<Operand, Block> pair : inst.getBranches()) {
            if (!reachableBlocks.contains(pair.getSecond()))
                continue;
            OpStatus operandOpStatus = getNowStatus(pair.getFirst());
            if (operandOpStatus.getStatus() == OpStatus.Status.multiDefined) {
                markMultiDefined(inst.getResult());
                return;
            } else if (operandOpStatus.getStatus() == OpStatus.Status.constant) {
                if (constant != null) {
                    if (!constant.equals(pair.getFirst())) {
                        markMultiDefined(inst.getResult());
                        return;
                    }
                } else
                    constant = (Constant) operandOpStatus.getOperand();
            }
        }

        if (constant != null)
            markConstant(inst.getResult(), constant);
    }

    private void markReachable(Block block) {
        if (!reachableBlocks.contains(block)) {
            reachableBlocks.add(block);
            blockWorkList.offer(block);
        } else {
            LLVMInstruction ptr = block.getInstHead();
            while (ptr instanceof PhiInst) {
                ptr.accept(this);
                ptr = ptr.getPostInst();
            }
        }
    }

    private void markConstant(Register register, Constant constant) {
        assert constant instanceof Operand;
        OpStatus opStatus = new OpStatus(OpStatus.Status.constant, (Operand) constant); //operand of status is now Constant
        OpStatus oldOpStatus = getNowStatus(register);
        if (oldOpStatus.getStatus() == OpStatus.Status.undefined) {
            operandStatusMap.replace(register, opStatus);
            registerWorkList.offer(register);
        } else {
            assert oldOpStatus.getStatus() != OpStatus.Status.multiDefined;
            assert oldOpStatus.getOperand().equals(opStatus.getOperand());
        }
    }

    private void markMultiDefined(Register register) {
        OpStatus oldOpStatus = getNowStatus(register);
        if (oldOpStatus.getStatus() != OpStatus.Status.multiDefined) {
            operandStatusMap.replace(register, new OpStatus(OpStatus.Status.multiDefined, null));
            registerWorkList.offer(register);
        }
    }

    public OpStatus getNowStatus(Operand operand) {
        if (operandStatusMap.containsKey(operand))
            return operandStatusMap.get(operand);
        else{
            OpStatus naiveStatus;
            if (operand instanceof Constant)
                naiveStatus = new OpStatus(OpStatus.Status.constant, operand);
            else if (operand instanceof Register && ((Register) operand).isParameter())
                naiveStatus = new OpStatus(OpStatus.Status.multiDefined, null);
            else
                naiveStatus = new OpStatus(OpStatus.Status.undefined, null);
            operandStatusMap.put(operand, naiveStatus);
            return naiveStatus;
        }
    }


    @Override
    public void visit(Block block) {
        ArrayList<LLVMInstruction> instructions = block.getInstructions();
        for (LLVMInstruction instruction : instructions)
            instruction.accept(this); // visit IRInstruction
    }

    @Override
    public void visit(Module module) {
    }


    @Override
    public void visit(ReturnInst inst) {

    }



    @Override
    public void visit(LoadInst inst) {
        markMultiDefined(inst.getResult());
    }

    @Override
    public void visit(StoreInst inst) {

    }

    @Override
    public void visit(AllocInst inst) {
        markMultiDefined(inst.getResult());
    }

    @Override
    public void visit(GEPInst inst) {
        markMultiDefined(inst.getResult());
    }


    @Override
    public void visit(DefineGlobal defineGlobal) {

    }

    @Override
    public void visit(CallInst inst) {
        if (!inst.isVoidCall())
            markMultiDefined(inst.getResult());
    }

    @Override
    public void visit(MoveInst inst) {

    }

    @Override
    public void visit(ParallelCopyInst parallelCopyInst) {

    }

    private Constant foldIcmpConstant(LLVMInstruction inst, Constant lhsConstant, Constant rhsConstant){
        Operand lhs = (Operand) lhsConstant;
        Operand rhs = (Operand) rhsConstant;
        assert inst instanceof BinaryOpInst || inst instanceof IcmpInst;
        Constant result;

        boolean value;
        if (lhs instanceof ConstInt && rhs instanceof ConstInt) {
            switch (((IcmpInst) inst).getOperator()) {
                case eq:
                    value = ((ConstInt) lhs).getValue() == ((ConstInt) rhs).getValue();
                    break;
                case ne:
                    value = ((ConstInt) lhs).getValue() != ((ConstInt) rhs).getValue();
                    break;
                case sgt:
                    value = ((ConstInt) lhs).getValue() > ((ConstInt) rhs).getValue();
                    break;
                case sge:
                    value = ((ConstInt) lhs).getValue() >= ((ConstInt) rhs).getValue();
                    break;
                case slt:
                    value = ((ConstInt) lhs).getValue() < ((ConstInt) rhs).getValue();
                    break;
                case sle:
                    value = ((ConstInt) lhs).getValue() <= ((ConstInt) rhs).getValue();
                    break;
                default:
                    throw new RuntimeException();
            }
        } else if (lhs instanceof ConstBool && rhs instanceof ConstBool) {
            switch (((IcmpInst) inst).getOperator()) {
                case eq:
                    value = ((ConstBool) lhs).getValue() == ((ConstBool) rhs).getValue();
                    break;
                case ne:
                    value = ((ConstBool) lhs).getValue() != ((ConstBool) rhs).getValue();
                    break;
                default:
                    throw new RuntimeException();
            }
        } else if (lhs instanceof ConstNull && rhs instanceof ConstNull) {
            switch (((IcmpInst) inst).getOperator()) {
                case eq:
                    value = true;
                    break;
                case ne:
                    value = false;
                    break;
                default:
                    throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
        result = new ConstBool(value);
        return result;
    }

    private Constant foldBinaryInstConstant(LLVMInstruction inst, Constant lhsConstant, Constant rhsConstant) {
        Operand lhs = (Operand) lhsConstant;
        Operand rhs = (Operand) rhsConstant;
        assert inst instanceof BinaryOpInst || inst instanceof IcmpInst;
        Constant result;

        if (lhs instanceof ConstInt && rhs instanceof ConstInt) {
            long value;
            switch (((BinaryOpInst) inst).getOp()) {
                case add:
                    value = ((ConstInt) lhs).getValue() + ((ConstInt) rhs).getValue();
                    break;
                case sub:
                    value = ((ConstInt) lhs).getValue() - ((ConstInt) rhs).getValue();
                    break;
                case mul:
                    value = ((ConstInt) lhs).getValue() * ((ConstInt) rhs).getValue();
                    break;
                case sdiv:
                    if (((ConstInt) rhs).getValue() == 0)
                        return null;
                    value = ((ConstInt) lhs).getValue() / ((ConstInt) rhs).getValue();
                    break;
                case srem:
                    if (((ConstInt) rhs).getValue() == 0)
                        return null;
                    value = ((ConstInt) lhs).getValue() % ((ConstInt) rhs).getValue();
                    break;
                case shl:
                    value = ((ConstInt) lhs).getValue() << ((ConstInt) rhs).getValue();
                    break;
                case ashr:
                    value = ((ConstInt) lhs).getValue() >> ((ConstInt) rhs).getValue();
                    break;
                case and:
                    value = ((ConstInt) lhs).getValue() & ((ConstInt) rhs).getValue();
                    break;
                case or:
                    value = ((ConstInt) lhs).getValue() | ((ConstInt) rhs).getValue();
                    break;
                case xor:
                    value = ((ConstInt) lhs).getValue() ^ ((ConstInt) rhs).getValue();
                    break;
                default:
                    throw new RuntimeException();
            }
            result = new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), ((int) value));
        } else if (lhs instanceof ConstBool && rhs instanceof ConstBool) {
            boolean value;
            switch (((BinaryOpInst) inst).getOp()) {
                case and:
                    value = ((ConstBool) lhs).getValue() & ((ConstBool) rhs).getValue();
                    break;
                case or:
                    value = ((ConstBool) lhs).getValue() | ((ConstBool) rhs).getValue();
                    break;
                case xor:
                    value = ((ConstBool) lhs).getValue() ^ ((ConstBool) rhs).getValue();
                    break;
                default:
                    throw new RuntimeException();
            }
            result = new ConstBool(value);
        } else {
            throw new RuntimeException();
        }
        return result;
    }

    private boolean replaceRegisterWithConstant(Block block) {
        boolean changed = false;
        LLVMInstruction ptr = block.getInstHead();
        while (ptr != null) {
            LLVMInstruction next = ptr.getPostInst();
            changed |= ptr.result2Constant(this);
            ptr = next;
        }
        return changed;
    }
}