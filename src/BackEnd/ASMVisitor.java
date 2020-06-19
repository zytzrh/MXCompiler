package BackEnd;

import BackEnd.Instruction.BinaryInst.ITypeBinaryInst;
import BackEnd.Instruction.BinaryInst.RTypeBinaryInst;
import BackEnd.Instruction.Branch.BinaryBranchInst;
import BackEnd.Instruction.Branch.UnaryBranchInst;
import BackEnd.Instruction.*;
import BackEnd.Operand.ASMGlobalVar;

public interface ASMVisitor {
    void visit(ASMModule ASMModule);
    void visit(ASMFunction ASMFunction);
    void visit(ASMBlock block);

    void visit(ASMGlobalVar gv);

    void visit(ASMMoveInst inst);
    void visit(ASMUnaryInst inst);
    void visit(ITypeBinaryInst inst);
    void visit(RTypeBinaryInst inst);

    void visit(ASMLoadAddressInst inst);
    void visit(ASMLoadImmediate inst);
    void visit(ASMLoadUpperImmediate inst);

    void visit(ASMLoadInst inst);
    void visit(ASMStoreInst inst);

    void visit(ASMJumpInst inst);
    void visit(BinaryBranchInst inst);
    void visit(UnaryBranchInst inst);
    void visit(ASMCallInst inst);
    void visit(ASMReturnInst inst);
}
