package BackEnd.Construct;

import BackEnd.ASMBlock;
import BackEnd.Instruction.*;
import BackEnd.Instruction.BinaryInst.ITypeBinary;
import BackEnd.Instruction.BinaryInst.RTypeBinary;
import BackEnd.Instruction.Branch.BinaryBranch;
import BackEnd.Instruction.Branch.UnaryBranch;
import BackEnd.Operand.ASMGlobalVar;
import BackEnd.Operand.ASMOperand;
import BackEnd.Operand.ASMRegister.PhysicalASMRegister;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.BaseOffsetAddr;
import BackEnd.Operand.Address.StackLocation;
import BackEnd.Operand.Immediate.Immediate;
import BackEnd.Operand.Immediate.IntImmediate;
import BackEnd.Operand.Immediate.RelocationImmediate;
import BackEnd.RISCVFunction;
import BackEnd.RISCVModule;
import BackEnd.StackFrame;
import IR.Block;
import IR.IRVisitor;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.LLVMoperand.*;
import IR.Module;
import IR.TypeSystem.*;

import java.util.ArrayList;

import static BackEnd.Instruction.ASMUnaryInst.OpName.*;
import static BackEnd.Instruction.BinaryInst.ITypeBinary.OpName.*;
import static BackEnd.Instruction.BinaryInst.RTypeBinary.OpName.*;
import static BackEnd.Instruction.Branch.BinaryBranch.OpName.*;
import static BackEnd.Instruction.Branch.UnaryBranch.OpName.beqz;

public class InstructionSelector implements IRVisitor {
    private RISCVModule ASMRISCVModule;

    private RISCVFunction currentRISCVFunction;
    private ASMBlock currentBlock;

    public InstructionSelector() {
        ASMRISCVModule = new RISCVModule();
        currentRISCVFunction = null;
        currentBlock = null;
    }

    public RISCVModule getASMRISCVModule() {
        return ASMRISCVModule;
    }

    @Override
    public void visit(Module module) {
        for(DefineGlobal defineGlobal : module.getDefineGlobals()){
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

        for (LLVMfunction IRExternalFunction : module.getBuiltInFunctionMap().values()) {
            String name = IRExternalFunction.getFunctionName();
            ASMRISCVModule.getBuiltInFunctionMap().put(name,
                    new RISCVFunction(ASMRISCVModule, name, null));
        }
        for (LLVMfunction IRFunction : module.getFunctionMap().values()) {
            String functionName = IRFunction.getFunctionName();
            ASMRISCVModule.getFunctionMap().put(functionName,
                    new RISCVFunction(ASMRISCVModule, functionName, IRFunction));
        }

        for (LLVMfunction IRFunction : module.getFunctionMap().values())
            IRFunction.accept(this);
    }

    @Override
    public void visit(LLVMfunction function) {
        String functionName = function.getFunctionName();
        currentRISCVFunction = ASMRISCVModule.getFunctionMap().get(functionName);
        currentBlock = currentRISCVFunction.getEntranceBlock();

        // ------ Stack Frame ------
        StackFrame stackFrame = new StackFrame(currentRISCVFunction);
        currentRISCVFunction.setStackFrame(stackFrame);

        // Save return address
        VirtualASMRegister tmpRA = new VirtualASMRegister(PhysicalASMRegister.returnAddressVR.getName() + ".tmp");
        currentRISCVFunction.registerVR(tmpRA);           //gugu changed
        currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                tmpRA, PhysicalASMRegister.returnAddressVR));

        // Save callee-save registers
        for (VirtualASMRegister vr : PhysicalASMRegister.calleeSaveVRs) {
            VirtualASMRegister savedVR = new VirtualASMRegister(vr.getName() + ".tmp");
            currentRISCVFunction.registerVR(savedVR);
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, savedVR, vr));
        }

        // Parameters
        ArrayList<Register> IRParameters = function.getParas();
        int paraNum = IRParameters.size();
        if(paraNum <= 8){
            for(int i = 0; i < paraNum; i++){
                // Fix the color of the parameters.
                Register parameter = IRParameters.get(i);
                VirtualASMRegister vr = currentRISCVFunction.getVR(parameter.getName());
                currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                        vr, PhysicalASMRegister.argVR.get(i)));
            }
        }else{
            for(int i = 0; i < 8; i++){
                // Fix the color of the parameters.
                Register parameter = IRParameters.get(i);
                VirtualASMRegister vr = currentRISCVFunction.getVR(parameter.getName());
                currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                        vr, PhysicalASMRegister.argVR.get(i)));
            }
            for (int i = 8; i < paraNum; i++) {
                Register parameter = IRParameters.get(i);
                VirtualASMRegister vr = currentRISCVFunction.getVR(parameter.getName());

                StackLocation stackLocation = new StackLocation(parameter.getName() + ".para");
                stackFrame.addFormalParameterLocation(stackLocation);

                currentBlock.addInstruction(new ASMLoadInst(currentBlock, vr,
                        ASMLoadInst.ByteSize.lw, stackLocation));
            }
        }

        // ------ Blocks ------
        for (Block block : function.getBlocks())
            block.accept(this);
    }

    @Override
    public void visit(Block block) {
        currentBlock = currentRISCVFunction.getBlockMap().get(block.getName());
        LLVMInstruction ptr = block.getInstHead();
        while (ptr != null) {
            ptr.accept(this);
            ptr = ptr.getPostInst();
        }
    }

    @Override
    public void visit(ReturnInst inst) {
        if (!(inst.getReturnType() instanceof LLVMVoidType)) {
            VirtualASMRegister returnValue = Operand2VR(inst.getReturnValue());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                    PhysicalASMRegister.argVR.get(0), returnValue));
        }

        // recover callee-save regsiter
        for (VirtualASMRegister vr : PhysicalASMRegister.calleeSaveVRs) {
            VirtualASMRegister savedVR = currentRISCVFunction.getVR(vr.getName() + ".tmp");
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, vr, savedVR));
        }

        VirtualASMRegister savedRA = currentRISCVFunction.getVR(
                PhysicalASMRegister.returnAddressVR.getName() + ".tmp");
        currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                PhysicalASMRegister.returnAddressVR, savedRA));

        currentBlock.addInstruction(new ASMReturnInst(currentBlock));
    }

    @Override
    public void visit(BranchInst inst) {
        if (inst.getCondition() !=  null) {
            Operand cond = inst.getCondition();
            ASMBlock thenBlock = currentRISCVFunction.getBlockMap().get(inst.getIfTrueBlock().getName());
            ASMBlock elseBlock = currentRISCVFunction.getBlockMap().get(inst.getIfFalseBlock().getName());

            if (cond instanceof Register
                    && ((Register) cond).getDef() instanceof IcmpInst
                    && cond.onlyHaveOneBranchUse()) {
                IcmpInst icmp = ((IcmpInst) ((Register) cond).getDef());
                icmp.swapOpIfNeed();

                LLVMtype type = icmp.getCompareType();
                IcmpInst.IcmpName op = icmp.getOperator();
                Operand op1 = icmp.getOp1();
                Operand op2 = icmp.getOp2();
                VirtualASMRegister rs1 = currentRISCVFunction.getVR(op1.getName());
                VirtualASMRegister rs2;
                if (type instanceof LLVMIntType) {
                    if (op2 instanceof Constant) {
                        long value = op2 instanceof ConstBool
                                ? (((ConstBool) op2).getValue() ? 1 : 0) : ((ConstInt) op2).getValue();

                        if (value != 0) {
                            rs2 = new VirtualASMRegister("loadImmediate");
                            currentRISCVFunction.registerVRDuplicateName(rs2);
                            if (needToLoadImm(value)) {
                                currentBlock.addInstruction(new ASMLoadImmediate(currentBlock,
                                        rs2, new IntImmediate(value)));
                            } else {
                                currentBlock.addInstruction(new ITypeBinary(currentBlock, addi, PhysicalASMRegister.zeroVR,
                                        new IntImmediate(value), rs2));
                            }
                        } else
                            rs2 = PhysicalASMRegister.zeroVR;
                    } else
                        rs2 = currentRISCVFunction.getVR(op2.getName());
                } else if (type instanceof LLVMPointerType) {
                    if (op2 instanceof Constant) {
                        assert op2 instanceof ConstNull;
                        rs2 = PhysicalASMRegister.zeroVR;
                    } else
                        rs2 = currentRISCVFunction.getVR(op2.getName());
                } else
                    throw new RuntimeException();

                BinaryBranch.OpName branchOp = op == IcmpInst.IcmpName.eq ? bne
                        : op == IcmpInst.IcmpName.ne ? beq
                        : op == IcmpInst.IcmpName.sgt ? ble
                        : op == IcmpInst.IcmpName.sge ? blt
                        : op == IcmpInst.IcmpName.slt ? bge
                        : bgt;
                currentBlock.addInstruction(new BinaryBranch(currentBlock, branchOp, rs1, rs2, elseBlock));
                currentBlock.addInstruction(new ASMJumpInst(currentBlock, thenBlock));
                return;
            }

            VirtualASMRegister condVR = currentRISCVFunction.getVR(cond.getName());
            currentBlock.addInstruction(new UnaryBranch(currentBlock, beqz, condVR, elseBlock));
            currentBlock.addInstruction(new ASMJumpInst(currentBlock, thenBlock));
        } else {
            ASMBlock thenBlock = currentRISCVFunction.getBlockMap().get(inst.getIfTrueBlock().getName());
            currentBlock.addInstruction(new ASMJumpInst(currentBlock, thenBlock));
        }
    }

    @Override
    public void visit(BinaryOpInst inst) {
        if (inst.shouldSwapOperands())
            inst.swapOperands();

        Operand lhs = inst.getLhs();
        Operand rhs = inst.getRhs();
        VirtualASMRegister lhsOperand;
        ASMOperand rhsOperand;
        VirtualASMRegister result = currentRISCVFunction.getVR(inst.getResult().getName());

        Object opName;
        BinaryOpInst.BinaryOpName instOp = inst.getOp();
        switch (instOp) {
            case add: case and: case or: case xor:
                lhsOperand = Operand2VR(lhs);
                rhsOperand = getOperand(rhs);
                if (rhsOperand instanceof Immediate) {
                    opName = instOp == BinaryOpInst.BinaryOpName.add ? addi
                            : instOp == BinaryOpInst.BinaryOpName.and ? andi
                            : instOp == BinaryOpInst.BinaryOpName.or ? ori
                            : xori;
                    currentBlock.addInstruction(new ITypeBinary(currentBlock, ((ITypeBinary.OpName) opName),
                            lhsOperand, ((Immediate) rhsOperand), result));
                } else {
                    opName = instOp == BinaryOpInst.BinaryOpName.add ? add
                            : instOp == BinaryOpInst.BinaryOpName.and ? and
                            : instOp == BinaryOpInst.BinaryOpName.or ? or
                            : xor;
                    currentBlock.addInstruction(new RTypeBinary(currentBlock, ((RTypeBinary.OpName) opName),
                            lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                }
                break;
            case sub:
                lhsOperand = Operand2VR(lhs);
                rhsOperand = getOperand(rhs);
                if (rhsOperand instanceof Immediate) {
                    assert rhsOperand instanceof IntImmediate;
                    ((IntImmediate) rhsOperand).minusImmediate();
                    currentBlock.addInstruction(new ITypeBinary(currentBlock, addi,
                            lhsOperand, ((Immediate) rhsOperand), result));
                } else {
                    currentBlock.addInstruction(new RTypeBinary(currentBlock, RTypeBinary.OpName.sub,
                            lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                }
                break;
            case mul: case sdiv: case srem:
                opName = instOp == BinaryOpInst.BinaryOpName.mul ? RTypeBinary.OpName.mul
                        : instOp == BinaryOpInst.BinaryOpName.sdiv ? RTypeBinary.OpName.div
                        : RTypeBinary.OpName.rem;
                lhsOperand = Operand2VR(lhs);
                rhsOperand = Operand2VR(rhs);
                currentBlock.addInstruction(new RTypeBinary(currentBlock, ((RTypeBinary.OpName) opName),
                        lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                break;
            case shl: case ashr:
                if (rhs instanceof ConstInt && (((ConstInt) rhs).getValue() >= 32 || ((ConstInt) rhs).getValue() <= 0))
                    break;
                lhsOperand = Operand2VR(lhs);
                rhsOperand = getOperand(rhs);
                if (rhsOperand instanceof Immediate) {
                    opName = instOp == BinaryOpInst.BinaryOpName.shl ? slli : srai;
                    currentBlock.addInstruction(new ITypeBinary(currentBlock, ((ITypeBinary.OpName) opName),
                            lhsOperand, ((Immediate) rhsOperand), result));
                } else {
                    opName = instOp == BinaryOpInst.BinaryOpName.shl ? sll : sra;
                    currentBlock.addInstruction(new RTypeBinary(currentBlock, ((RTypeBinary.OpName) opName),
                            lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                }
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public void visit(AllocInst inst) {
        // Do nothing.
    }

    @Override
    public void visit(LoadInst inst) {
        VirtualASMRegister rd = currentRISCVFunction.getVR(inst.getResult().getName());
        assert inst.getType() instanceof LLVMIntType || inst.getType() instanceof LLVMPointerType;
        ASMLoadInst.ByteSize size = inst.getType().getByte() == 1
                ? ASMLoadInst.ByteSize.lb
                : ASMLoadInst.ByteSize.lw;

        if (inst.getAddr() instanceof GlobalVar) {
            ASMGlobalVar gv =
                    ASMRISCVModule.getGlobalVariableMap().get(inst.getAddr().getName());
            VirtualASMRegister lui = new VirtualASMRegister("luiHigh");
            currentRISCVFunction.registerVRDuplicateName(lui);
            currentBlock.addInstruction(new ASMLoadUpperImmediate(currentBlock, lui,
                    new RelocationImmediate(RelocationImmediate.Type.high, gv)));
            currentBlock.addInstruction(new ASMLoadInst(currentBlock, rd, size,
                    new BaseOffsetAddr(lui, new RelocationImmediate(RelocationImmediate.Type.low, gv))));
        } else {
            if (inst.getAddr() instanceof ConstNull) {
                currentBlock.addInstruction(new ASMLoadInst(currentBlock, rd, size,
                        new BaseOffsetAddr(PhysicalASMRegister.zeroVR, new IntImmediate(0))));
            } else {
                assert inst.getAddr() instanceof Register;
                VirtualASMRegister pointer = currentRISCVFunction.getVR(inst.getAddr().getName());
                if (currentRISCVFunction.getGepAddrMap().containsKey(pointer)) {
                    BaseOffsetAddr addr = currentRISCVFunction.getGepAddrMap().get(pointer);
                    currentBlock.addInstruction(new ASMLoadInst(currentBlock,
                            rd, size, addr));
                } else {
                    currentBlock.addInstruction(new ASMLoadInst(currentBlock,
                            rd, size, new BaseOffsetAddr(pointer, new IntImmediate(0))));
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
            currentRISCVFunction.registerVRDuplicateName(lui);
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
                VirtualASMRegister pointer = currentRISCVFunction.getVR(inst.getAddr().getName());
                if (currentRISCVFunction.getGepAddrMap().containsKey(pointer)) {
                    BaseOffsetAddr addr = currentRISCVFunction.getGepAddrMap().get(pointer);
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
        VirtualASMRegister rd = currentRISCVFunction.getVR(inst.getResult().getName());

        if (inst.getPointer() instanceof GlobalVar) { // gep string
            currentBlock.addInstruction(new ASMLoadAddressInst(currentBlock, rd,
                    ASMRISCVModule.getGlobalVariableMap().get(inst.getPointer().getName())));
        } else if (inst.getIndexs().size() == 1) { // gep array
            VirtualASMRegister pointer = currentRISCVFunction.getVR(inst.getPointer().getName());
            Operand index = inst.getIndexs().get(0);
            if (index instanceof Constant) {
                assert index instanceof ConstInt;
                long value = ((ConstInt) index).getValue() * 4; // 4 is the size of a pointer.
                ASMOperand rs = getOperand(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), value));
                if (rs instanceof Immediate)
                    currentBlock.addInstruction(new ITypeBinary(currentBlock, addi, pointer, ((Immediate) rs), rd));
                else {
                    assert rs instanceof VirtualASMRegister;
                    currentBlock.addInstruction(new RTypeBinary(currentBlock, add, pointer,
                            ((VirtualASMRegister) rs), rd));
                }
            } else {
                VirtualASMRegister rs1 = currentRISCVFunction.getVR(index.getName());
                VirtualASMRegister rs2 = new VirtualASMRegister("slli");
                currentRISCVFunction.registerVRDuplicateName(rs2);
                currentBlock.addInstruction(new ITypeBinary(currentBlock, slli, rs1, new IntImmediate(2), rs2));
                currentBlock.addInstruction(new RTypeBinary(currentBlock, add, pointer, rs2, rd));
            }
        } else { // gep class
            if (inst.getPointer() instanceof ConstNull) {
                currentBlock.addInstruction(new ITypeBinary(currentBlock, addi, PhysicalASMRegister.zeroVR,
                        new IntImmediate(((int) ((ConstInt) inst.getIndexs().get(1)).getValue())), rd));
            } else {
                assert inst.getPointer().getLlvMtype() instanceof LLVMPointerType
                        && ((LLVMPointerType) inst.getPointer().getLlvMtype()).getBaseType() instanceof LLVMStructType;
                assert inst.getIndexs().size() == 2;
                assert inst.getIndexs().get(0) instanceof ConstInt
                        && ((ConstInt) inst.getIndexs().get(0)).getValue() == 0;
                assert inst.getIndexs().get(1) instanceof ConstInt;
                VirtualASMRegister pointer = currentRISCVFunction.getVR(inst.getPointer().getName());
                LLVMStructType LLVMStructType = ((LLVMStructType) ((LLVMPointerType)
                        inst.getPointer().getLlvMtype()).getBaseType());
                int index = ((int) ((ConstInt) inst.getIndexs().get(1)).getValue());
                int offset = LLVMStructType.calcOffset(index);
                if (!(LLVMStructType.getMembers().get(index) instanceof LLVMPointerType))
                    currentRISCVFunction.getGepAddrMap().put(rd, new BaseOffsetAddr(pointer, new IntImmediate(offset)));
                else {
                    ASMOperand rs = getOperand(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), offset));
                    if (rs instanceof Immediate)
                        currentBlock.addInstruction(new ITypeBinary(currentBlock, addi, pointer, ((Immediate) rs), rd));
                    else {
                        assert rs instanceof VirtualASMRegister;
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, add,
                                pointer, ((VirtualASMRegister) rs), rd));
                    }
                }
            }
        }
    }

    @Override
    public void visit(BitCastInst inst) {
        VirtualASMRegister src = currentRISCVFunction.getVR(inst.getSource().getName());
        VirtualASMRegister dest = currentRISCVFunction.getVR(inst.getResult().getName());
        currentBlock.addInstruction(new ASMMoveInst(currentBlock, dest, src));
    }

    @Override
    public void visit(IcmpInst inst) {
        if (inst.getResult().onlyHaveOneBranchUse()) {
            // Do nothing. Wait until dealing with BranchInst.
            return;
        }
        
        LLVMtype type = inst.getCompareType();
        inst.swapOpIfNeed();
        Operand op1 = inst.getOp1();
        Operand op2 = inst.getOp2();
        VirtualASMRegister rd = currentRISCVFunction.getVR(inst.getResult().getName());
        if (type instanceof LLVMIntType) {
            VirtualASMRegister rs1 = currentRISCVFunction.getVR(op1.getName());
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
                            currentRISCVFunction.registerVRDuplicateName(rs2);
                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(op2value)));
                            currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs1, rs2, rd));
                        } else if (op2value != 0) {
                            currentBlock.addInstruction(new ITypeBinary(currentBlock, slti, rs1, new IntImmediate(op2value), rd));
                        } else { // value == 0
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, sltz, rs1, rd));
                        }
                        break;
                    case sgt:
                        if (needToLoadImm(op2value)) {
                            currentRISCVFunction.registerVRDuplicateName(rs2);
                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(op2value)));
                            currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs2, rs1, rd));
                        } else if (op2value != 0) {
                            currentRISCVFunction.registerVRDuplicateName(rs2);
                            currentBlock.addInstruction(new ITypeBinary(currentBlock, addi, PhysicalASMRegister.zeroVR,
                                    new IntImmediate(op2value), rs2));
                            currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs2, rs1, rd));
                        } else { // value == 0
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, sgtz, rs1, rd));
                        }
                        break;
                    case eq: case ne:
                        ASMUnaryInst.OpName opName = op == IcmpInst.IcmpName.eq ? seqz : snez;
                        if (needToLoadImm(op2value)) {
                            currentRISCVFunction.registerVRDuplicateName(rs2);
                            currentRISCVFunction.registerVRDuplicateName(rs3);

                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(op2value)));
                            currentBlock.addInstruction(new RTypeBinary(currentBlock, xor, rs1, rs2, rs3));
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, opName, rs3, rd));
                        } else if (op2value != 0) {
                            currentRISCVFunction.registerVRDuplicateName(rs3);
                            currentBlock.addInstruction(new ITypeBinary(currentBlock, xori, rs1,
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
                VirtualASMRegister rs2 = currentRISCVFunction.getVR(op2.getName());
                VirtualASMRegister rs3 = new VirtualASMRegister("slt");
                VirtualASMRegister rs4 = new VirtualASMRegister("xor");
                switch (op) {
                    case slt:
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs1, rs2, rd));
                        break;
                    case sgt:
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs2, rs1, rd));
                        break;
                    case sle:
                        currentRISCVFunction.registerVRDuplicateName(rs3);
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs2, rs1, rs3));
                        currentBlock.addInstruction(new ITypeBinary(currentBlock, xori, rs3,
                                new IntImmediate(1), rd));
                        break;
                    case sge:
                        currentRISCVFunction.registerVRDuplicateName(rs3);
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs1, rs2, rs3));
                        currentBlock.addInstruction(new ITypeBinary(currentBlock, xori, rs3,
                                new IntImmediate(1), rd));
                        break;
                    case eq:
                        currentRISCVFunction.registerVRDuplicateName(rs4);
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, xor, rs1, rs2, rs4));
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, seqz, rs4, rd));
                        break;
                    case ne:
                        currentRISCVFunction.registerVRDuplicateName(rs4);
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, xor, rs1, rs2, rs4));
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, snez, rs4, rd));
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        } else if (type instanceof LLVMPointerType) {
            VirtualASMRegister rs1 = currentRISCVFunction.getVR(op1.getName());
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
                VirtualASMRegister rs2 = currentRISCVFunction.getVR(op2.getName());
                VirtualASMRegister rs3 = new VirtualASMRegister("xor");

                currentRISCVFunction.registerVRDuplicateName(rs3);
                currentBlock.addInstruction(new RTypeBinary(currentBlock, xor, rs1, rs2, rs3));
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
        VirtualASMRegister dest = currentRISCVFunction.getVR(inst.getResult().getName());
        if (inst.getSource() instanceof Constant) {
            ASMOperand src = getOperand(inst.getSource());
            if (src instanceof VirtualASMRegister) {
                currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                        dest, ((VirtualASMRegister) src)));
            } else {
                assert src instanceof Immediate;
                currentBlock.addInstruction(new ITypeBinary(currentBlock, addi,
                        PhysicalASMRegister.zeroVR, ((Immediate) src), dest));
            }
        } else {
            VirtualASMRegister src = currentRISCVFunction.getVR(inst.getSource().getName());
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
            return currentRISCVFunction.getVR(operand.getName());
        } else{
            throw new RuntimeException();
        }
    }

    private VirtualASMRegister boolVR(Boolean boolValue){
        if(boolValue){
            VirtualASMRegister constBool = new VirtualASMRegister("constBool");
            currentRISCVFunction.registerVRDuplicateName(constBool);
            currentBlock.addInstruction(new ITypeBinary(currentBlock, addi,
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
            currentRISCVFunction.registerVRDuplicateName(constInt);
            if (needToLoadImm(intValue)) {
                currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, constInt, new IntImmediate(intValue)));
            } else {
                currentBlock.addInstruction(new ITypeBinary(currentBlock, addi,
                        PhysicalASMRegister.zeroVR, new IntImmediate(intValue), constInt));
            }
            return constInt;
        }

    }


    private ASMOperand getOperand(Operand operand) {
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
        RISCVFunction callee = ASMRISCVModule.getFunction(inst.getLlvMfunction().getFunctionName());
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

            StackFrame stackFrame = currentRISCVFunction.getStackFrame();                   //current stackframe
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
            VirtualASMRegister result = currentRISCVFunction.getVR(inst.getResult().getName());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, result, PhysicalASMRegister.returnValueVR));
        }
    }

    @Override
    public void visit(PhiInst inst) {
        // Do nothing.
    }

    @Override
    public void visit(DefineGlobal defineGlobal) {
        //gugu changed
    }

    @Override
    public void visit(ParallelCopyInst parallelCopyInst) {
        //gugu changed
    }
}