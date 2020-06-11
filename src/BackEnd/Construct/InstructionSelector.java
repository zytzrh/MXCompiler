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
//        for (GlobalVariable IRGlobalVariable : module.getGlobalVariableMap().values()) {
//            String name = IRGlobalVariable.getName();
//            ASMGlobalVar gv = new ASMGlobalVar(name);
//            ASMRISCVModule.getGlobalVariableMap().put(name, gv);
//
//            Operand init = IRGlobalVariable.getInit();
//            assert init != null;
//
//            if (IRGlobalVariable.getLlvMtype() instanceof LLVMArrayType) {
//                assert IRGlobalVariable.getInit() instanceof ConstString;
//                gv.setString(((ConstString) init).getValue());
//            } else if (IRGlobalVariable.getLlvMtype() instanceof LLVMIntType
//                    && ((LLVMIntType) IRGlobalVariable.getLlvMtype()).getBitWidth() == LLVMIntType.BitWidth.int1) {
//                assert init instanceof ConstBool;
//                gv.setBool(((ConstBool) init).getValue() ? 1 : 0);
//            } else if (IRGlobalVariable.getLlvMtype() instanceof LLVMIntType
//                    && ((LLVMIntType) IRGlobalVariable.getLlvMtype()).getBitWidth() == LLVMIntType.BitWidth.int32) {
//                assert init instanceof ConstInt;
//                gv.setInt(((int) ((ConstInt) init).getValue()));
//            } else if (IRGlobalVariable.getLlvMtype() instanceof LLVMPointerType) {
//                assert init instanceof ConstNull;
//                gv.setInt(0);
//            }
//        }
        for (LLVMfunction IRExternalFunction : module.getBuiltInFunctionMap().values()) {
            String name = IRExternalFunction.getFunctionName();
            ASMRISCVModule.getExternalFunctionMap().put(name,
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

        // ------ Save return address ------
        VirtualASMRegister savedRA = new VirtualASMRegister(PhysicalASMRegister.raVR.getName() + ".save");
        currentRISCVFunction.getSymbolTable().putASM(savedRA.getName(), savedRA);
        currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                savedRA, PhysicalASMRegister.raVR));

        // ------ Save callee-save registers ------
        for (VirtualASMRegister vr : PhysicalASMRegister.calleeSaveVRs) {
            VirtualASMRegister savedVR = new VirtualASMRegister(vr.getName() + ".save");
            currentRISCVFunction.getSymbolTable().putASM(savedVR.getName(), savedVR);
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, savedVR, vr));
        }

        // ------ Parameters ------
        ArrayList<Register> IRParameters = function.getParas();
        // Fix the color of the first 8 parameters.
        for (int i = 0; i < Integer.min(IRParameters.size(), 8); i++) {
            Register parameter = IRParameters.get(i);
            VirtualASMRegister vr = currentRISCVFunction.getSymbolTable().getVR(parameter.getName());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                    vr, PhysicalASMRegister.argVR.get(i)));
        }
        // Load spilled parameters from the frame of the caller.
        for (int i = 8; i < IRParameters.size(); i++) {
            Register parameter = IRParameters.get(i);
            VirtualASMRegister vr = currentRISCVFunction.getSymbolTable().getVR(parameter.getName());
            StackLocation stackLocation = new StackLocation(parameter.getName() + ".para");
            stackFrame.addFormalParameterLocation(stackLocation);
            currentBlock.addInstruction(new ASMLoadInst(currentBlock, vr,
                    ASMLoadInst.ByteSize.lw, stackLocation));
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
            VirtualASMRegister returnValue = getVROfOperand(inst.getReturnValue());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                    PhysicalASMRegister.argVR.get(0), returnValue));
        }

        // ------ Recover saved callee-save registers ------
        for (VirtualASMRegister vr : PhysicalASMRegister.calleeSaveVRs) {
            VirtualASMRegister savedVR = currentRISCVFunction.getSymbolTable().getVR(vr.getName() + ".save");
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, vr, savedVR));
        }

        VirtualASMRegister savedRA = currentRISCVFunction.getSymbolTable().getVR(
                PhysicalASMRegister.raVR.getName() + ".save");
        currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                PhysicalASMRegister.raVR, savedRA));

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
                if (icmp.shouldSwap(true))
                    icmp.swapOps();

                LLVMtype type = icmp.getIrType();
                IcmpInst.IcmpName op = icmp.getOperator();
                Operand op1 = icmp.getOp1();
                Operand op2 = icmp.getOp2();
                VirtualASMRegister rs1 = currentRISCVFunction.getSymbolTable().getVR(op1.getName());
                VirtualASMRegister rs2;
                if (type instanceof LLVMIntType) {
                    if (op2 instanceof Constant) {
                        long value = op2 instanceof ConstBool
                                ? (((ConstBool) op2).getValue() ? 1 : 0) : ((ConstInt) op2).getValue();

                        if (value != 0) {
                            rs2 = new VirtualASMRegister("loadImmediate");
                            currentRISCVFunction.getSymbolTable().putASMRename(rs2.getName(), rs2);
                            if (needToLoadImmediate(value)) {
                                currentBlock.addInstruction(new ASMLoadImmediate(currentBlock,
                                        rs2, new IntImmediate(value)));
                            } else {
                                currentBlock.addInstruction(new ITypeBinary(currentBlock, addi, PhysicalASMRegister.zeroVR,
                                        new IntImmediate(value), rs2));
                            }
                        } else
                            rs2 = PhysicalASMRegister.zeroVR;
                    } else
                        rs2 = currentRISCVFunction.getSymbolTable().getVR(op2.getName());
                } else if (type instanceof LLVMPointerType) {
                    if (op2 instanceof Constant) {
                        assert op2 instanceof ConstNull;
                        rs2 = PhysicalASMRegister.zeroVR;
                    } else
                        rs2 = currentRISCVFunction.getSymbolTable().getVR(op2.getName());
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

            VirtualASMRegister condVR = currentRISCVFunction.getSymbolTable().getVR(cond.getName());
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
        VirtualASMRegister result = currentRISCVFunction.getSymbolTable().getVR(inst.getResult().getName());

        Object opName;
        BinaryOpInst.BinaryOpName instOp = inst.getOp();
        switch (instOp) {
            case add: case and: case or: case xor:
                lhsOperand = getVROfOperand(lhs);
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
                lhsOperand = getVROfOperand(lhs);
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
                lhsOperand = getVROfOperand(lhs);
                rhsOperand = getVROfOperand(rhs);
                currentBlock.addInstruction(new RTypeBinary(currentBlock, ((RTypeBinary.OpName) opName),
                        lhsOperand, ((VirtualASMRegister) rhsOperand), result));
                break;
            case shl: case ashr:
                if (rhs instanceof ConstInt && (((ConstInt) rhs).getValue() >= 32 || ((ConstInt) rhs).getValue() <= 0))
                    break;
                lhsOperand = getVROfOperand(lhs);
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
        VirtualASMRegister rd = currentRISCVFunction.getSymbolTable().getVR(inst.getResult().getName());
        assert inst.getType() instanceof LLVMIntType || inst.getType() instanceof LLVMPointerType;
        ASMLoadInst.ByteSize size = inst.getType().getByte() == 1
                ? ASMLoadInst.ByteSize.lb
                : ASMLoadInst.ByteSize.lw;

        if (inst.getAddr() instanceof GlobalVar) {
            ASMGlobalVar gv =
                    ASMRISCVModule.getGlobalVariableMap().get(inst.getAddr().getName());
            VirtualASMRegister lui = new VirtualASMRegister("luiHigh");
            currentRISCVFunction.getSymbolTable().putASMRename(lui.getName(), lui);
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
                VirtualASMRegister pointer = currentRISCVFunction.getSymbolTable().getVR(inst.getAddr().getName());
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
        VirtualASMRegister value = getVROfOperand(inst.getValue());
        LLVMtype irType = inst.getValue().getLlvMtype();
        assert irType instanceof LLVMIntType || irType instanceof LLVMPointerType;
        ASMStoreInst.ByteSize size = irType.getByte() == 1
                ? ASMStoreInst.ByteSize.sb
                : ASMStoreInst.ByteSize.sw;

        if (inst.getAddr() instanceof GlobalVar) {
            ASMGlobalVar gv =
                    ASMRISCVModule.getGlobalVariableMap().get(inst.getAddr().getName());
            VirtualASMRegister lui = new VirtualASMRegister("luiHigh");
            currentRISCVFunction.getSymbolTable().putASMRename(lui.getName(), lui);
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
                VirtualASMRegister pointer = currentRISCVFunction.getSymbolTable().getVR(inst.getAddr().getName());
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
        VirtualASMRegister rd = currentRISCVFunction.getSymbolTable().getVR(inst.getResult().getName());

        if (inst.getPointer() instanceof GlobalVar) { // gep string
            currentBlock.addInstruction(new ASMLoadAddressInst(currentBlock, rd,
                    ASMRISCVModule.getGlobalVariableMap().get(inst.getPointer().getName())));
        } else if (inst.getIndexs().size() == 1) { // gep array
            VirtualASMRegister pointer = currentRISCVFunction.getSymbolTable().getVR(inst.getPointer().getName());
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
                VirtualASMRegister rs1 = currentRISCVFunction.getSymbolTable().getVR(index.getName());
                VirtualASMRegister rs2 = new VirtualASMRegister("slli");
                currentRISCVFunction.getSymbolTable().putASMRename(rs2.getName(), rs2);
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
                VirtualASMRegister pointer = currentRISCVFunction.getSymbolTable().getVR(inst.getPointer().getName());
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
        VirtualASMRegister src = currentRISCVFunction.getSymbolTable().getVR(inst.getSource().getName());
        VirtualASMRegister dest = currentRISCVFunction.getSymbolTable().getVR(inst.getResult().getName());
        currentBlock.addInstruction(new ASMMoveInst(currentBlock, dest, src));
    }

    @Override
    public void visit(IcmpInst inst) {
        if (inst.getResult().onlyHaveOneBranchUse()) {
            // Do nothing. Wait until dealing with BranchInst.
            return;
        }

        if (inst.shouldSwap(true))
            inst.swapOps();

        LLVMtype type = inst.getIrType();
        Operand op1 = inst.getOp1();
        Operand op2 = inst.getOp2();
        VirtualASMRegister rd = currentRISCVFunction.getSymbolTable().getVR(inst.getResult().getName());
        if (type instanceof LLVMIntType) {
            VirtualASMRegister rs1 = currentRISCVFunction.getSymbolTable().getVR(op1.getName());
            if (op2 instanceof Constant) { // I-type
                inst.convertLeGeToLtGt();
                op1 = inst.getOp1();
                op2 = inst.getOp2();
                IcmpInst.IcmpName op = inst.getOperator();

                long value = op2 instanceof ConstBool
                        ? (((ConstBool) op2).getValue() ? 1 : 0) : ((ConstInt) op2).getValue();
                VirtualASMRegister rs2 = new VirtualASMRegister("loadImmediate");
                VirtualASMRegister rs3 = new VirtualASMRegister("xor");
                switch (op) {
                    case slt:
                        if (needToLoadImmediate(value)) {
                            currentRISCVFunction.getSymbolTable().putASMRename(rs2.getName(), rs2);
                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(value)));
                            currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs1, rs2, rd));
                        } else if (value != 0) {
                            currentBlock.addInstruction(new ITypeBinary(currentBlock, slti, rs1,
                                    new IntImmediate(value), rd));
                        } else { // value == 0
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, sltz, rs1, rd));
                        }
                        break;
                    case sgt:
                        if (needToLoadImmediate(value)) {
                            currentRISCVFunction.getSymbolTable().putASMRename(rs2.getName(), rs2);
                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(value)));
                            currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs2, rs1, rd));
                        } else if (value != 0) {
                            currentRISCVFunction.getSymbolTable().putASMRename(rs2.getName(), rs2);
                            currentBlock.addInstruction(new ITypeBinary(currentBlock, addi, PhysicalASMRegister.zeroVR,
                                    new IntImmediate(value), rs2));
                            currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs2, rs1, rd));
                        } else { // value == 0
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, sgtz, rs1, rd));
                        }
                        break;
                    case eq: case ne:
                        ASMUnaryInst.OpName opName = op == IcmpInst.IcmpName.eq ? seqz : snez;
                        if (needToLoadImmediate(value)) {
                            currentRISCVFunction.getSymbolTable().putASMRename(rs2.getName(), rs2);
                            currentRISCVFunction.getSymbolTable().putASMRename(rs3.getName(), rs3);

                            currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, rs2, new IntImmediate(value)));
                            currentBlock.addInstruction(new RTypeBinary(currentBlock, xor, rs1, rs2, rs3));
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, opName, rs3, rd));
                        } else if (value != 0) {
                            currentRISCVFunction.getSymbolTable().putASMRename(rs3.getName(), rs3);
                            currentBlock.addInstruction(new ITypeBinary(currentBlock, xori, rs1,
                                    new IntImmediate(value), rs3));
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, opName, rs3, rd));
                        } else { // value = 0
                            currentBlock.addInstruction(new ASMUnaryInst(currentBlock, opName, rs1, rd));
                        }
                        break;
                    default:
                        System.out.println(op);
                        System.out.println(op1);
                        System.out.println(op2);
                        throw new RuntimeException();
                }
            } else {
                IcmpInst.IcmpName op = inst.getOperator();
                VirtualASMRegister rs2 = currentRISCVFunction.getSymbolTable().getVR(op2.getName());
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
                        currentRISCVFunction.getSymbolTable().putASMRename(rs3.getName(), rs3);
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs2, rs1, rs3));
                        currentBlock.addInstruction(new ITypeBinary(currentBlock, xori, rs3,
                                new IntImmediate(1), rd));
                        break;
                    case sge:
                        currentRISCVFunction.getSymbolTable().putASMRename(rs3.getName(), rs3);
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, slt, rs1, rs2, rs3));
                        currentBlock.addInstruction(new ITypeBinary(currentBlock, xori, rs3,
                                new IntImmediate(1), rd));
                        break;
                    case eq:
                        currentRISCVFunction.getSymbolTable().putASMRename(rs4.getName(), rs4);
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, xor, rs1, rs2, rs4));
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, seqz, rs4, rd));
                        break;
                    case ne:
                        currentRISCVFunction.getSymbolTable().putASMRename(rs4.getName(), rs4);
                        currentBlock.addInstruction(new RTypeBinary(currentBlock, xor, rs1, rs2, rs4));
                        currentBlock.addInstruction(new ASMUnaryInst(currentBlock, snez, rs4, rd));
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        } else if (type instanceof LLVMPointerType) {
            VirtualASMRegister rs1 = currentRISCVFunction.getSymbolTable().getVR(op1.getName());
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
                VirtualASMRegister rs2 = currentRISCVFunction.getSymbolTable().getVR(op2.getName());
                VirtualASMRegister rs3 = new VirtualASMRegister("xor");

                currentRISCVFunction.getSymbolTable().putASMRename(rs3.getName(), rs3);
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
    public void visit(PhiInst inst) {
        // Do nothing.
    }

    @Override
    public void visit(DefineGlobal defineGlobal) {
        //gugu changed
    }

    @Override
    public void visit(CallInst inst) {
        RISCVFunction callee;
        if (ASMRISCVModule.getFunctionMap().containsKey(inst.getLlvMfunction().getFunctionName()))
            callee = ASMRISCVModule.getFunctionMap().get(inst.getLlvMfunction().getFunctionName());
        else
            callee = ASMRISCVModule.getExternalFunctionMap().get(inst.getLlvMfunction().getFunctionName());
        ArrayList<Operand> parameters = inst.getParas();

        for (int i = 0; i < Integer.min(8, parameters.size()); i++) {
            VirtualASMRegister parameter = getVROfOperand(parameters.get(i));
            currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                    PhysicalASMRegister.argVR.get(i), parameter));
        }

        StackFrame stackFrame = currentRISCVFunction.getStackFrame();
        if (stackFrame.getParameterLocation().containsKey(callee)) {
            ArrayList<StackLocation> stackLocations = stackFrame.getParameterLocation().get(callee);
            for (int i = 8; i < parameters.size(); i++) {
                VirtualASMRegister parameter = getVROfOperand(parameters.get(i));
                currentBlock.addInstruction(new ASMStoreInst(currentBlock, parameter,
                        ASMStoreInst.ByteSize.sw, stackLocations.get(i - 8)));
            }
        } else {
            ArrayList<StackLocation> stackLocations = new ArrayList<>();
            for (int i = 8; i < parameters.size(); i++) {
                VirtualASMRegister parameter = getVROfOperand(parameters.get(i));
                StackLocation stackLocation = new StackLocation(".para" + i);
                stackLocations.add(stackLocation);

                currentBlock.addInstruction(new ASMStoreInst(currentBlock, parameter,
                        ASMStoreInst.ByteSize.sw, stackLocation));
            }
            stackFrame.getParameterLocation().put(callee, stackLocations);
        }

        ASMCallInst ASMCallInst = new ASMCallInst(currentBlock,
                callee);
        currentBlock.addInstruction(ASMCallInst);

        if (!inst.isVoidCall()) {
            VirtualASMRegister result = currentRISCVFunction.getSymbolTable().getVR(inst.getResult().getName());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock,
                    result, PhysicalASMRegister.argVR.get(0)));
        }
    }

    @Override
    public void visit(MoveInst inst) {
        VirtualASMRegister dest = currentRISCVFunction.getSymbolTable().getVR(inst.getResult().getName());
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
            VirtualASMRegister src = currentRISCVFunction.getSymbolTable().getVR(inst.getSource().getName());
            currentBlock.addInstruction(new ASMMoveInst(currentBlock, dest, src));
        }
    }

    @Override
    public void visit(ParallelCopyInst parallelCopyInst) {
        //gugu changed
    }

    static private boolean needToLoadImmediate(long value) {
        return value >= (1 << 11) || value < -(1 << 11);
    }

    private VirtualASMRegister getVROfOperand(Operand operand) {
        if (operand instanceof ConstBool) {
            if (((ConstBool) operand).getValue()) {
                VirtualASMRegister constBool = new VirtualASMRegister("constBool");
                currentRISCVFunction.getSymbolTable().putASMRename(constBool.getName(), constBool);
                currentBlock.addInstruction(new ITypeBinary(currentBlock, addi,
                        PhysicalASMRegister.zeroVR, new IntImmediate(1), constBool));
                return constBool;
            } else
                return PhysicalASMRegister.zeroVR;
        } else if (operand instanceof ConstInt) {
            long value = ((ConstInt) operand).getValue();
            if (value == 0)
                return PhysicalASMRegister.zeroVR;
            else {
                VirtualASMRegister constInt = new VirtualASMRegister("constInt");
                currentRISCVFunction.getSymbolTable().putASMRename(constInt.getName(), constInt);
                if (needToLoadImmediate(value)) {
                    currentBlock.addInstruction(new ASMLoadImmediate(currentBlock, constInt, new IntImmediate(value)));
                } else {
                    currentBlock.addInstruction(new ITypeBinary(currentBlock, addi,
                            PhysicalASMRegister.zeroVR, new IntImmediate(value), constInt));
                }
                return constInt;
            }
        } else if (operand instanceof ConstNull) {
            return PhysicalASMRegister.zeroVR;
        } else if (operand instanceof GlobalVar) {
            throw new RuntimeException();
        } else if (operand instanceof Register && ((Register) operand).isParameter()) {
            return currentRISCVFunction.getSymbolTable().getVR(operand.getName());
        } else if (operand instanceof Register) {
            return currentRISCVFunction.getSymbolTable().getVR(operand.getName());
        } else
            throw new RuntimeException();
    }

    private ASMOperand getOperand(Operand operand) {
        if (operand instanceof ConstBool) {
            boolean value = ((ConstBool) operand).getValue();
            return new IntImmediate(value ? 1 : 0);
        } else if (operand instanceof ConstInt) {
            long value = ((ConstInt) operand).getValue();
            if (needToLoadImmediate(value))
                return getVROfOperand(operand);
            else
                return new IntImmediate(value);
        } else if (operand instanceof ConstNull) {
            return PhysicalASMRegister.zeroVR;
        } else if (operand instanceof GlobalVar) {
            throw new RuntimeException();
        } else if (operand instanceof Register) {
            return getVROfOperand(operand);
        } else
            throw new RuntimeException();
    }
}