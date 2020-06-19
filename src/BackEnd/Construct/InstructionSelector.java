package BackEnd.Construct;

import BackEnd.ASMBlock;
import BackEnd.ASMFunction;
import BackEnd.ASMModule;
import BackEnd.Instruction.*;
import BackEnd.Instruction.BinaryInst.ITypeBinaryInst;
import BackEnd.Instruction.BinaryInst.RTypeBinaryInst;
import BackEnd.Instruction.Branch.BinaryBranchInst;
import BackEnd.Instruction.Branch.UnaryBranchInst;
import BackEnd.Operand.ASMGlobalVar;
import BackEnd.Operand.ASMOperand;
import BackEnd.Operand.ASMRegister.PhysicalASMRegister;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.BaseOffsetAddr;
import BackEnd.Operand.Address.StackLocation;
import BackEnd.Operand.Immediate.Immediate;
import BackEnd.Operand.Immediate.IntImmediate;
import BackEnd.Operand.Immediate.RelocationImmediate;
import BackEnd.StackFrame;
import IR.Block;
import IR.IRVisitor;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.*;
import IR.Module;
import IR.TypeSystem.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static BackEnd.Instruction.ASMUnaryInst.OpName.*;
import static BackEnd.Instruction.BinaryInst.ITypeBinaryInst.OpName.*;
import static BackEnd.Instruction.BinaryInst.RTypeBinaryInst.OpName.*;
import static BackEnd.Instruction.Branch.BinaryBranchInst.OpName.*;
import static BackEnd.Instruction.Branch.UnaryBranchInst.OpName.beqz;

public class InstructionSelector implements IRVisitor {
    private ASMModule ASMRISCVModule;

    private ASMFunction currentASMFunction;
    private ASMBlock currentBlock;

    public InstructionSelector() {
        ASMRISCVModule = new ASMModule();
        currentASMFunction = null;
        currentBlock = null;
    }

    public ASMModule getASMRISCVModule() {
        return ASMRISCVModule;
    }

    @Override
    public void visit(Module module) {
        for(DefineGlobal defineGlobal : module.getDefineGlobals()){
            defineGlobal.accept(this);
        }

        for (LLVMfunction builtInFunction : module.getBuiltInFunctionMap().values()) {
            String name = builtInFunction.getFunctionName();
            ASMFunction asmFunction = new ASMFunction(ASMRISCVModule, name, null);
            ASMRISCVModule.getBuiltInFunctionMap().put(name, asmFunction);
        }
        for (LLVMfunction normalFunction : module.getFunctionMap().values()) {
            String functionName = normalFunction.getFunctionName();
            ASMFunction asmFunction = new ASMFunction(ASMRISCVModule, functionName, normalFunction);
            ASMRISCVModule.getFunctionMap().put(functionName, asmFunction);
            initASMBlocks(asmFunction, normalFunction);
            initVRTable(asmFunction, normalFunction);
        }

        for (LLVMfunction IRFunction : module.getFunctionMap().values())
            IRFunction.accept(this);
    }

    public void initASMBlocks(ASMFunction asmFunction, LLVMfunction llvMfunction){
        int functionCnt = asmFunction.getASMModule().getFunctionMap().size();
        int blockCnt = 0;
        asmFunction.setBlockMap(new HashMap<>());
        ArrayList<Block> IRBlocks = llvMfunction.getBlocks();
        for (Block IRBlock : IRBlocks) {
            String blockName = ".ASMBlock" + functionCnt + "_" + blockCnt;
            ASMBlock block = new ASMBlock(asmFunction, IRBlock, IRBlock.getName(),blockName);
            asmFunction.addBasicBlock(block);
            asmFunction.getBlockMap().put(block.getName(), block);
            blockCnt++;
        }
        for (Block IRBlock : IRBlocks) {
            ASMBlock block = asmFunction.getBlockMap().get(IRBlock.getName());
            Set<ASMBlock> predecessors = block.getPredecessors();
            Set<ASMBlock> successors = block.getSuccessors();

            for (Block predecessor : IRBlock.getPredecessors())
                predecessors.add(asmFunction.getBlockMap().get(predecessor.getName()));
            for (Block successor : IRBlock.getSuccessors())
                successors.add(asmFunction.getBlockMap().get(successor.getName()));
        }
        asmFunction.setEntranceBlock(asmFunction.getBlockMap().get(IRBlocks.get(0).getName()));
        asmFunction.setExitBlock(asmFunction.getBlockMap().get(IRBlocks.get(IRBlocks.size() - 1).getName()));
    }

    public void initVRTable(ASMFunction asmFunction, LLVMfunction llvMfunction) {
        asmFunction.setVRSymbolTable(new HashMap<>());
        ArrayList<Block> IRBlocks = llvMfunction.getBlocks();
        for (Register parameter : llvMfunction.getParas()) {
            VirtualASMRegister vr = new VirtualASMRegister(parameter.getName());
            asmFunction.registerVR(vr);
        }
        for (Block IRBlock : IRBlocks) {
            LLVMInstruction ptr = IRBlock.getInstHead();
            while (ptr != null) {
                if (ptr.hasResult()) {
                    String registerName = ptr.getResult().getName();
                    if (!(ptr instanceof MoveInst)) {
                        VirtualASMRegister vr = new VirtualASMRegister(registerName);
                        asmFunction.registerVR(vr);
                    } else {
                        //for moveInst
                        if (!asmFunction.contains(registerName)) {
                            VirtualASMRegister vr = new VirtualASMRegister(registerName);
                            asmFunction.registerVR(vr);
                        }
                    }
                }
                ptr = ptr.getPostInst();
            }
        }
    }



    @Override
    public void visit(LLVMfunction function) {
        String functionName = function.getFunctionName();
        currentASMFunction = ASMRISCVModule.getFunctionMap().get(functionName);
        currentBlock = currentASMFunction.getEntranceBlock();
        StackFrame stackFrame = currentASMFunction.getStackFrame();


        // Save return address
        VirtualASMRegister tmpRA = new VirtualASMRegister(PhysicalASMRegister.returnAddressVR.getName() + ".tmp");
        currentASMFunction.registerVR(tmpRA);           //gugu changed
        currentBlock.addInstruction(new ASMMoveInst(currentBlock, tmpRA, PhysicalASMRegister.returnAddressVR));

        // Save callee-save registers
        for (VirtualASMRegister vr : PhysicalASMRegister.calleeSaveVRs) {
            VirtualASMRegister savedVR = new VirtualASMRegister(vr.getName() + ".tmp");
            currentASMFunction.registerVR(savedVR);
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, savedVR, vr));
        }

        // Parameters
        ArrayList<Register> IRParameters = function.getParas();
        int paraNum = IRParameters.size();
        if(paraNum <= 8){
            for(int i = 0; i < paraNum; i++){
                // Fix the color of the parameters.
                Register parameter = IRParameters.get(i);
                VirtualASMRegister vr = currentASMFunction.getVR(parameter.getName());
                currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                        vr, PhysicalASMRegister.argVR.get(i)));
            }
        }else{
            for(int i = 0; i < 8; i++){
                // Fix the color of the parameters.
                Register parameter = IRParameters.get(i);
                VirtualASMRegister vr = currentASMFunction.getVR(parameter.getName());
                currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                        vr, PhysicalASMRegister.argVR.get(i)));
            }
            for (int i = 8; i < paraNum; i++) {
                Register parameter = IRParameters.get(i);
                VirtualASMRegister vr = currentASMFunction.getVR(parameter.getName());

                StackLocation stackLocation = new StackLocation(parameter.getName() + ".para");
                stackFrame.addFormalParameterLocation(stackLocation);

                currentBlock.addInstruction(new ASMLoadInst(currentBlock, vr,
                        ASMLoadInst.ByteType.lw, stackLocation));
            }
        }

        //Blocks
        for (Block block : function.getBlocks())
            block.accept(this);
    }

    @Override
    public void visit(Block block) {
        currentBlock = currentASMFunction.getBlockMap().get(block.getName());
        LLVMInstruction currentInst = block.getInstHead();
        while (currentInst != null) {
            currentInst.accept(this);
            currentInst = currentInst.getPostInst();
        }
    }

    @Override
    public void visit(ReturnInst inst) {
        //move return value
        if (!(inst.getReturnType() instanceof LLVMVoidType)) {
            VirtualASMRegister returnValue = Operand2VR(inst.getReturnValue());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                    PhysicalASMRegister.argVR.get(0), returnValue));
        }
        //recover callee-save regsiter
        for (VirtualASMRegister vr : PhysicalASMRegister.calleeSaveVRs) {
            VirtualASMRegister savedVR = currentASMFunction.getVR(vr.getName() + ".tmp");
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, vr, savedVR));
        }
        //recover return address register
        VirtualASMRegister savedRA = currentASMFunction.getVR(PhysicalASMRegister.returnAddressVR.getName() + ".tmp");
        currentBlock.addInstruction(new ASMMoveInst(currentBlock, PhysicalASMRegister.returnAddressVR, savedRA));
        //add return inst
        currentBlock.addInstruction(new ASMReturnInst(currentBlock));
    }

    @Override
    public void visit(BranchInst inst) {
        if (inst.getCondition() ==  null) {
            ASMBlock ifTrueBlock = currentASMFunction.getBlockMap().get(inst.getIfTrueBlock().getName());
            currentBlock.addInstruction(new ASMJumpInst(currentBlock, ifTrueBlock));
        }else{
            Operand cond = inst.getCondition();
            ASMBlock ifTrueBlock = currentASMFunction.getBlockMap().get(inst.getIfTrueBlock().getName());
            ASMBlock ifFalseBlock = currentASMFunction.getBlockMap().get(inst.getIfFalseBlock().getName());

            if (cond instanceof Register && ((Register) cond).getDef() instanceof IcmpInst && cond.onlyHaveOneBranchUse()) {
                IcmpInst icmpInst = ((IcmpInst) ((Register) cond).getDef());
                icmpInst.swapOpIfNeed();

                Operand icmpOp1 = icmpInst.getOp1();
                VirtualASMRegister rs1 = currentASMFunction.getVR(icmpOp1.getName());       //
                Operand icmpOp2 = icmpInst.getOp2();
                VirtualASMRegister rs2 = getIcmpRs2(icmpInst, icmpOp2);

                BinaryBranchInst.OpName ASMop;
                if(icmpInst.getOperator() == IcmpInst.IcmpName.eq)ASMop = bne;
                else if(icmpInst.getOperator() == IcmpInst.IcmpName.ne)ASMop = beq;
                else if(icmpInst.getOperator() == IcmpInst.IcmpName.sge)ASMop = blt;
                else if(icmpInst.getOperator() == IcmpInst.IcmpName.sgt)ASMop = ble;
                else if(icmpInst.getOperator() == IcmpInst.IcmpName.sle)ASMop = bgt;
                else if(icmpInst.getOperator() == IcmpInst.IcmpName.slt)ASMop = bge;
                else throw new RuntimeException();

                currentBlock.addInstruction(new BinaryBranchInst(currentBlock, ASMop, rs1, rs2, ifFalseBlock));
                currentBlock.addInstruction(new ASMJumpInst(currentBlock, ifTrueBlock));
            }else{
                VirtualASMRegister condVR = currentASMFunction.getVR(cond.getName());       //
                currentBlock.addInstruction(new UnaryBranchInst(currentBlock, beqz, condVR, ifFalseBlock));
                currentBlock.addInstruction(new ASMJumpInst(currentBlock, ifTrueBlock));
            }
        }
    }

    private VirtualASMRegister getIcmpRs2(IcmpInst icmp, Operand icmpOp2) {
        VirtualASMRegister rs2;
        if(!(icmpOp2 instanceof Constant)){
            rs2 = currentASMFunction.getVR(icmpOp2.getName());
        }else if (icmp.getCompareType() instanceof LLVMIntType) {
            long value = getConstantValue(icmpOp2);
            if (value == 0) {
                rs2 = PhysicalASMRegister.zeroVR;
            } else{
                rs2 = new VirtualASMRegister("loadImmediate");
                currentASMFunction.registerVRDuplicateName(rs2);
                if (needToLoadImm(value)) {
                    currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(value)));
                } else {
                    currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi, PhysicalASMRegister.zeroVR,
                            new IntImmediate(value), rs2));
                }
            }
        } else if (icmp.getCompareType() instanceof LLVMPointerType) {
            assert icmpOp2 instanceof ConstNull;
            rs2 = PhysicalASMRegister.zeroVR;
        } else
            throw new RuntimeException();

        return rs2;
    }

    private long getConstantValue(Operand constOperand) {
        long value;
        if(constOperand instanceof  ConstBool){
            boolean boolValue = ((ConstBool) constOperand).getValue();
            if(boolValue)
                value = 1;
            else
                value = 0;
        }else{
            assert constOperand instanceof ConstInt;
            value = ((ConstInt) constOperand).getValue();
        }
        return value;
    }

    @Override
    public void visit(BinaryOpInst inst) {
        if (inst.shouldSwapOperands())
            inst.swapOperands();

        Operand lhs = inst.getLhs();
        Operand rhs = inst.getRhs();
        VirtualASMRegister lhsOperand;
        ASMOperand rhsOperand;
        VirtualASMRegister result = currentASMFunction.getVR(inst.getResult().getName());

        Object opName;
        BinaryOpInst.BinaryOpName instOp = inst.getOp();


        switch (instOp) {
            case add: case and: case or: case xor:
                lhsOperand = Operand2VR(lhs);
                rhsOperand = Operand2ASM(rhs);
                if (rhsOperand instanceof Immediate) {
                    if(instOp == BinaryOpInst.BinaryOpName.add) opName = addi;
                    else if(instOp == BinaryOpInst.BinaryOpName.and) opName = andi;
                    else if(instOp == BinaryOpInst.BinaryOpName.or) opName = ori;
                    else if(instOp == BinaryOpInst.BinaryOpName.xor) opName = xori;
                    else throw  new RuntimeException();
                    currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, ((ITypeBinaryInst.OpName) opName),
                            lhsOperand, ((Immediate) rhsOperand), result));
                } else {
                    if(instOp == BinaryOpInst.BinaryOpName.add) opName = add;
                    else if(instOp == BinaryOpInst.BinaryOpName.and) opName = and;
                    else if(instOp == BinaryOpInst.BinaryOpName.or) opName = or;
                    else if(instOp == BinaryOpInst.BinaryOpName.xor) opName = xor;
                    else throw  new RuntimeException();
                    currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, ((RTypeBinaryInst.OpName) opName),
                            lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                }
                break;
            case sub:
                lhsOperand = Operand2VR(lhs);
                rhsOperand = Operand2ASM(rhs);
                if (rhsOperand instanceof Immediate) {
                    assert rhsOperand instanceof IntImmediate;
                    ((IntImmediate) rhsOperand).becomeMinusImmediate();
                    currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi,
                            lhsOperand, ((Immediate) rhsOperand), result));
                } else {
                    currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, RTypeBinaryInst.OpName.sub,
                            lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                }
                break;
            case mul: case sdiv: case srem:
                if(instOp == BinaryOpInst.BinaryOpName.mul) opName = mul;
                else if(instOp == BinaryOpInst.BinaryOpName.sdiv) opName = div;
                else if(instOp == BinaryOpInst.BinaryOpName.srem) opName = rem;
                else throw new RuntimeException();
                lhsOperand = Operand2VR(lhs);
                rhsOperand = Operand2VR(rhs);
                currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, ((RTypeBinaryInst.OpName) opName),
                        lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                break;
            case shl: case ashr:
                if (rhs instanceof ConstInt && (((ConstInt) rhs).getValue() >= 32 || ((ConstInt) rhs).getValue() <= 0))
                    break;
                lhsOperand = Operand2VR(lhs);
                rhsOperand = Operand2ASM(rhs);
                if (rhsOperand instanceof Immediate) {
                    opName = instOp == BinaryOpInst.BinaryOpName.shl ? slli : srai;
                    currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, ((ITypeBinaryInst.OpName) opName),
                            lhsOperand, ((Immediate) rhsOperand), result));
                } else {
                    opName = instOp == BinaryOpInst.BinaryOpName.shl ? sll : sra;
                    currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, ((RTypeBinaryInst.OpName) opName),
                            lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                }
                break;
            default:
                throw new RuntimeException();
        }
    }


    @Override
    public void visit(LoadInst inst) {
        assert inst.getType() instanceof LLVMIntType || inst.getType() instanceof LLVMPointerType;
        VirtualASMRegister rd = currentASMFunction.getVR(inst.getResult().getName());

        ASMLoadInst.ByteType size;
        if(inst.getType().getByte() == 1) size = ASMLoadInst.ByteType.lb;
        else size = ASMLoadInst.ByteType.lw;

        if (inst.getAddr() instanceof GlobalVar) {
            ASMGlobalVar gv = ASMRISCVModule.getGlobalVariableMap().get(inst.getAddr().getName());
            VirtualASMRegister lui = new VirtualASMRegister("luiHigh");
            currentASMFunction.registerVRDuplicateName(lui);
            currentBlock.addInstruction(new ASMLoadUpperImmediate(currentBlock, lui, new RelocationImmediate(RelocationImmediate.Type.high, gv)));
            currentBlock.addInstruction(new ASMLoadInst(currentBlock, rd, size, new BaseOffsetAddr(lui, new RelocationImmediate(RelocationImmediate.Type.low, gv))));
        } else {
            if (inst.getAddr() instanceof ConstNull) {
                currentBlock.addInstruction(new ASMLoadInst(currentBlock, rd, size, new BaseOffsetAddr(PhysicalASMRegister.zeroVR, new IntImmediate(0))));
            } else {
                assert inst.getAddr() instanceof Register;
                VirtualASMRegister pointer = currentASMFunction.getVR(inst.getAddr().getName());
                if (currentASMFunction.getGepAddrMap().containsKey(pointer)) {
                    BaseOffsetAddr addr = currentASMFunction.getGepAddrMap().get(pointer);
                    currentBlock.addInstruction(new ASMLoadInst(currentBlock, rd, size, addr));
                } else {
                    currentBlock.addInstruction(new ASMLoadInst(currentBlock, rd, size, new BaseOffsetAddr(pointer, new IntImmediate(0))));
                }
            }
        }
    }

    @Override
    public void visit(StoreInst inst) {
        VirtualASMRegister value = Operand2VR(inst.getValue());
        LLVMtype irType = inst.getValue().getLlvMtype();
        assert irType instanceof LLVMIntType || irType instanceof LLVMPointerType;
        ASMStoreInst.ByteType size = irType.getByte() == 1
                ? ASMStoreInst.ByteType.sb
                : ASMStoreInst.ByteType.sw;

        if (inst.getAddr() instanceof GlobalVar) {
            ASMGlobalVar gv =
                    ASMRISCVModule.getGlobalVariableMap().get(inst.getAddr().getName());
            VirtualASMRegister lui = new VirtualASMRegister("luiHigh");
            currentASMFunction.registerVRDuplicateName(lui);
            currentBlock.addInstruction(new ASMLoadUpperImmediate(currentBlock, lui,
                    new RelocationImmediate(RelocationImmediate.Type.high, gv)));
            currentBlock.addInstruction(new ASMStoreInst(currentBlock, value, size,
                    new BaseOffsetAddr(lui, new RelocationImmediate(RelocationImmediate.Type.low, gv))));
        } else {
            if (inst.getAddr() instanceof ConstNull) {
                currentBlock.addInstruction(new ASMStoreInst(currentBlock, value, size,
                        new BaseOffsetAddr(PhysicalASMRegister.zeroVR, new IntImmediate(0))));
            } else {
                assert inst.getAddr() instanceof Register;
                VirtualASMRegister pointer = currentASMFunction.getVR(inst.getAddr().getName());
                if (currentASMFunction.getGepAddrMap().containsKey(pointer)) {
                    BaseOffsetAddr addr = currentASMFunction.getGepAddrMap().get(pointer);
                    currentBlock.addInstruction(new ASMStoreInst(currentBlock,
                            value, size, addr));
                } else {
                    currentBlock.addInstruction(new ASMStoreInst(currentBlock,
                            value, size, new BaseOffsetAddr(pointer, new IntImmediate(0))));
                }
            }
        }
    }

    @Override
    public void visit(GEPInst inst) {
        if (inst.getPointer() instanceof GlobalVar) {
            gepString(inst);
        } else if (inst.getIndexs().size() == 1) {
            gepArray(inst);
        } else {
            gepClass(inst);
        }
    }

    void gepString(GEPInst inst){
        VirtualASMRegister rd = currentASMFunction.getVR(inst.getResult().getName());
        currentBlock.addInstruction(new ASMLoadAddressInst(currentBlock, rd,
                ASMRISCVModule.getGlobalVariableMap().get(inst.getPointer().getName())));
    }

    void gepArray(GEPInst inst){
        VirtualASMRegister rd = currentASMFunction.getVR(inst.getResult().getName());
        VirtualASMRegister pointer = currentASMFunction.getVR(inst.getPointer().getName());
        Operand index = inst.getIndexs().get(0);
        if (index instanceof Constant) {
            assert index instanceof ConstInt;
            long value = ((ConstInt) index).getValue() * 4; // 4 is the size of a pointer.
            ASMOperand rs = Operand2ASM(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), value));
            if (rs instanceof Immediate)
                currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi, pointer, ((Immediate) rs), rd));
            else {
                assert rs instanceof VirtualASMRegister;
                currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, add, pointer,
                        ((VirtualASMRegister) rs), rd));
            }
        } else {
            VirtualASMRegister rs1 = currentASMFunction.getVR(index.getName());
            VirtualASMRegister rs2 = new VirtualASMRegister("slli");
            currentASMFunction.registerVRDuplicateName(rs2);
            currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, slli, rs1, new IntImmediate(2), rs2));
            currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, add, pointer, rs2, rd));
        }
    }

    void gepClass(GEPInst inst){
        VirtualASMRegister rd = currentASMFunction.getVR(inst.getResult().getName());
        if (inst.getPointer() instanceof ConstNull) {
            currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi, PhysicalASMRegister.zeroVR,
                    new IntImmediate(((int) ((ConstInt) inst.getIndexs().get(1)).getValue())), rd));
        } else {
            assert inst.getPointer().getLlvMtype() instanceof LLVMPointerType
                    && ((LLVMPointerType) inst.getPointer().getLlvMtype()).getBaseType() instanceof LLVMStructType;
            assert inst.getIndexs().size() == 2;
            assert inst.getIndexs().get(0) instanceof ConstInt
                    && ((ConstInt) inst.getIndexs().get(0)).getValue() == 0;
            assert inst.getIndexs().get(1) instanceof ConstInt;
            VirtualASMRegister pointer = currentASMFunction.getVR(inst.getPointer().getName());
            LLVMStructType LLVMStructType = ((LLVMStructType) ((LLVMPointerType)
                    inst.getPointer().getLlvMtype()).getBaseType());
            int index = ((int) ((ConstInt) inst.getIndexs().get(1)).getValue());
            int offset = LLVMStructType.calcOffset(index);
            if (!(LLVMStructType.getMembers().get(index) instanceof LLVMPointerType))
                currentASMFunction.getGepAddrMap().put(rd, new BaseOffsetAddr(pointer, new IntImmediate(offset)));
            else {
                ASMOperand rs = Operand2ASM(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), offset));
                if (rs instanceof Immediate)
                    currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi, pointer, ((Immediate) rs), rd));
                else {
                    assert rs instanceof VirtualASMRegister;
                    currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, add,
                            pointer, ((VirtualASMRegister) rs), rd));
                }
            }
        }
    }

    @Override
    public void visit(BitCastInst inst) {
        VirtualASMRegister src = currentASMFunction.getVR(inst.getSource().getName());
        VirtualASMRegister dest = currentASMFunction.getVR(inst.getResult().getName());
        currentBlock.addInstruction(new ASMMoveInst(currentBlock, dest, src));
    }

    @Override
    public void visit(IcmpInst inst) {
        //can be optimized
        if (inst.getResult().onlyHaveOneBranchUse()) return;
        LLVMtype type = inst.getCompareType();
        inst.swapOpIfNeed();
        Operand op1 = inst.getOp1();
        Operand op2 = inst.getOp2();
        VirtualASMRegister rd = currentASMFunction.getVR(inst.getResult().getName());
        if (type instanceof LLVMIntType) {
            VirtualASMRegister rs1 = currentASMFunction.getVR(op1.getName());
            if (op2 instanceof Constant) {
                //Constant  ->  I-type instruction
                inst.removeEqual();
                op1 = inst.getOp1();
                op2 = inst.getOp2();
                IcmpInst.IcmpName op = inst.getOperator();
                //find op2value
                long op2value;
                if(op2 instanceof ConstBool){
                    if(((ConstBool) op2).getValue())
                        op2value = 1;
                    else
                        op2value = 0;
                }else{
                    assert op2 instanceof ConstInt;
                    op2value = ((ConstInt) op2).getValue();
                }

                VirtualASMRegister rs2 = new VirtualASMRegister("loadImm");
                VirtualASMRegister rs3 = new VirtualASMRegister("xor");
                switch (op) {
                    case slt:
                        if (needToLoadImm(op2value)) {
                            currentASMFunction.registerVRDuplicateName(rs2);
                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(op2value)));
                            currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, slt, rs1, rs2, rd));
                        } else if (op2value != 0) {
                            currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, slti, rs1, new IntImmediate(op2value), rd));
                        } else { // value == 0
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, sltz, rs1, rd));
                        }
                        break;
                    case sgt:
                        if (needToLoadImm(op2value)) {
                            currentASMFunction.registerVRDuplicateName(rs2);
                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(op2value)));
                            currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, slt, rs2, rs1, rd));
                        } else if (op2value != 0) {
                            currentASMFunction.registerVRDuplicateName(rs2);
                            currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi, PhysicalASMRegister.zeroVR,
                                    new IntImmediate(op2value), rs2));
                            currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, slt, rs2, rs1, rd));
                        } else { // value == 0
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, sgtz, rs1, rd));
                        }
                        break;
                    case eq: case ne:
                        ASMUnaryInst.OpName opName = op == IcmpInst.IcmpName.eq ? seqz : snez;
                        if (needToLoadImm(op2value)) {
                            currentASMFunction.registerVRDuplicateName(rs2);
                            currentASMFunction.registerVRDuplicateName(rs3);

                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(op2value)));
                            currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, xor, rs1, rs2, rs3));
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, opName, rs3, rd));
                        } else if (op2value != 0) {
                            currentASMFunction.registerVRDuplicateName(rs3);
                            currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, xori, rs1,
                                    new IntImmediate(op2value), rs3));
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, opName, rs3, rd));
                        } else { // value = 0
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, opName, rs1, rd));
                        }
                        break;
                    default:
                        throw new RuntimeException();
                }
            } else {
                IcmpInst.IcmpName op = inst.getOperator();
                VirtualASMRegister rs2 = currentASMFunction.getVR(op2.getName());
                VirtualASMRegister rs3 = new VirtualASMRegister("slt");
                VirtualASMRegister rs4 = new VirtualASMRegister("xor");
                switch (op) {
                    case slt:
                        currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, slt, rs1, rs2, rd));
                        break;
                    case sgt:
                        currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, slt, rs2, rs1, rd));
                        break;
                    case sle:
                        currentASMFunction.registerVRDuplicateName(rs3);
                        currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, slt, rs2, rs1, rs3));
                        currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, xori, rs3,
                                new IntImmediate(1), rd));
                        break;
                    case sge:
                        currentASMFunction.registerVRDuplicateName(rs3);
                        currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, slt, rs1, rs2, rs3));
                        currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, xori, rs3,
                                new IntImmediate(1), rd));
                        break;
                    case eq:
                        currentASMFunction.registerVRDuplicateName(rs4);
                        currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, xor, rs1, rs2, rs4));
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, seqz, rs4, rd));
                        break;
                    case ne:
                        currentASMFunction.registerVRDuplicateName(rs4);
                        currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, xor, rs1, rs2, rs4));
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, snez, rs4, rd));
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        } else if (type instanceof LLVMPointerType) {
            VirtualASMRegister rs1 = currentASMFunction.getVR(op1.getName());
            IcmpInst.IcmpName op = inst.getOperator();
            if (op2 instanceof Constant) {
                assert op2 instanceof ConstNull;
                switch (op) {
                    case eq:
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, seqz, rs1, rd));
                        break;
                    case ne:
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, snez, rs1, rd));
                        break;
                    default:
                        throw new RuntimeException();
                }
            } else {
                VirtualASMRegister rs2 = currentASMFunction.getVR(op2.getName());
                VirtualASMRegister rs3 = new VirtualASMRegister("xor");

                currentASMFunction.registerVRDuplicateName(rs3);
                currentBlock.addInstruction(new RTypeBinaryInst(currentBlock, xor, rs1, rs2, rs3));
                switch (op) {
                    case eq:
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, seqz, rs3, rd));
                        break;
                    case ne:
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, snez, rs3, rd));
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        } else
            throw new RuntimeException();
    }



    @Override
    public void visit(MoveInst inst) {
        VirtualASMRegister dest = currentASMFunction.getVR(inst.getResult().getName());
        if (inst.getSource() instanceof Constant) {
            ASMOperand src = Operand2ASM(inst.getSource());
            if (src instanceof VirtualASMRegister) {
                currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                        dest, ((VirtualASMRegister) src)));
            } else {
                assert src instanceof Immediate;
                currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi,
                        PhysicalASMRegister.zeroVR, ((Immediate) src), dest));
            }
        } else {
            VirtualASMRegister src = currentASMFunction.getVR(inst.getSource().getName());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, dest, src));
        }
    }



    static private boolean needToLoadImm(long value) {
        return value >= (1 << 11) || value < -(1 << 11);
    }

    private VirtualASMRegister Operand2VR(Operand operand) {
        assert !(operand instanceof GlobalVar);
        if (operand instanceof ConstBool) {
            boolean boolValue = ((ConstBool) operand).getValue();
            return boolVR(boolValue);
        } else if (operand instanceof ConstInt) {
            long intValue = ((ConstInt) operand).getValue();
            return intVR(intValue);
        } else if (operand instanceof ConstNull) {
            return PhysicalASMRegister.zeroVR;
        } else if (operand instanceof Register) {
            return currentASMFunction.getVR(operand.getName());
        } else{
            throw new RuntimeException();
        }
    }

    private VirtualASMRegister boolVR(Boolean boolValue){
        if(boolValue){
            VirtualASMRegister constBool = new VirtualASMRegister("constBool");
            currentASMFunction.registerVRDuplicateName(constBool);
            currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi,
                    PhysicalASMRegister.zeroVR, new IntImmediate(1), constBool));
            return constBool;
        }else{
            return PhysicalASMRegister.zeroVR;
        }
    }

    private VirtualASMRegister intVR(long intValue){
        if (intValue == 0)
            return PhysicalASMRegister.zeroVR;
        else {
            VirtualASMRegister constInt = new VirtualASMRegister("constInt");
            currentASMFunction.registerVRDuplicateName(constInt);
            if (needToLoadImm(intValue)) {
                currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, constInt, new IntImmediate(intValue)));
            } else {
                currentBlock.addInstruction(new ITypeBinaryInst(currentBlock, addi,
                        PhysicalASMRegister.zeroVR, new IntImmediate(intValue), constInt));
            }
            return constInt;
        }

    }


    private ASMOperand Operand2ASM(Operand operand) {
        if (operand instanceof ConstBool) {
            boolean value = ((ConstBool) operand).getValue();
            return new IntImmediate(value ? 1 : 0);
        } else if (operand instanceof ConstInt) {
            long value = ((ConstInt) operand).getValue();
            if (needToLoadImm(value))
                return Operand2VR(operand);
            else
                return new IntImmediate(value);
        } else if (operand instanceof ConstNull) {
            return PhysicalASMRegister.zeroVR;
        } else if (operand instanceof GlobalVar) {
            throw new RuntimeException();
        } else if (operand instanceof Register) {
            return Operand2VR(operand);
        } else
            throw new RuntimeException();
    }


    @Override
    public void visit(CallInst inst) {
        ASMFunction callee = ASMRISCVModule.getFunction(inst.getLlvMfunction().getFunctionName());
        ArrayList<Operand> paras = inst.getParas();
        int paraSize = paras.size();
        if(paraSize <= 8){
            for (int i = 0; i < paraSize; i++) {
                VirtualASMRegister parameter = Operand2VR(paras.get(i));
                currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                        PhysicalASMRegister.argVR.get(i), parameter));
            }
        }else{
            for (int i = 0; i < 8; i++) {
                VirtualASMRegister parameter = Operand2VR(paras.get(i));
                currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                        PhysicalASMRegister.argVR.get(i), parameter));
            }

            StackFrame stackFrame = currentASMFunction.getStackFrame();                   //current stackframe
            if (stackFrame.getParameterLocation().containsKey(callee)) {
                ArrayList<StackLocation> stackLocations = stackFrame.getParameterLocation().get(callee);
                for (int i = 8; i < paraSize; i++) {
                    Operand para = paras.get(i);
                    VirtualASMRegister parameter = Operand2VR(para);
                    currentBlock.addInstruction(new ASMStoreInst(currentBlock, parameter,
                            ASMStoreInst.ByteType.sw, stackLocations.get(i - 8)));
                }
            } else {
                ArrayList<StackLocation> stackLocations = new ArrayList<>();
                for (int i = 8; i < paras.size(); i++) {
                    VirtualASMRegister parameter = Operand2VR(paras.get(i));
                    StackLocation stackLocation = new StackLocation(".para" + i);
                    stackLocations.add(stackLocation);

                    currentBlock.addInstruction(new ASMStoreInst(currentBlock, parameter,
                            ASMStoreInst.ByteType.sw, stackLocation));
                }
                stackFrame.getParameterLocation().put(callee, stackLocations);
            }
        }

        //call inst
        ASMCallInst ASMCallInst = new ASMCallInst(currentBlock, callee);
        currentBlock.addInstruction(ASMCallInst);
        //
        if (!inst.isVoidCall()) {
            VirtualASMRegister result = currentASMFunction.getVR(inst.getResult().getName());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, result, PhysicalASMRegister.returnValueVR));
        }
    }

    @Override
    public void visit(DefineGlobal defineGlobal) {
        GlobalVar globalVar = defineGlobal.getGlobalVar();
        String name = globalVar.getName();
        ASMGlobalVar gv = new ASMGlobalVar(name);
        ASMRISCVModule.getGlobalVariableMap().put(name, gv);

        Operand init = defineGlobal.getInit();
        assert init != null;

        assert globalVar.getLlvMtype() instanceof LLVMPointerType;
        LLVMtype globalVarType = ((LLVMPointerType) globalVar.getLlvMtype()).getBaseType();

        if (globalVarType instanceof LLVMArrayType) {
            assert defineGlobal.getInit() instanceof ConstString;
            gv.setString(((ConstString) init).getValue());
        } else if (globalVarType instanceof LLVMIntType
                && ((LLVMIntType) globalVarType).getBitWidth() == LLVMIntType.BitWidth.int1) {
            assert init instanceof ConstBool;
            gv.setBool(((ConstBool) init).getValue() ? 1 : 0);
        } else if (globalVarType instanceof LLVMIntType
                && ((LLVMIntType) globalVarType).getBitWidth() == LLVMIntType.BitWidth.int32) {
            assert init instanceof ConstInt;
            gv.setInt(((int) ((ConstInt) init).getValue()));
        } else if (globalVarType instanceof LLVMPointerType) {
            assert init instanceof ConstNull;
            gv.setInt(0);
        }
    }

    @Override
    public void visit(PhiInst inst) {
    }


    @Override
    public void visit(ParallelCopyInst parallelCopyInst) {
        //gugu changed
    }

    @Override
    public void visit(AllocInst inst) {
    }
}