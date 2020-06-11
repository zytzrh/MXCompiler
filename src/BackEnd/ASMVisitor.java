package BackEnd;

import BackEnd.Instruction.BinaryInst.ITypeBinary;
import BackEnd.Instruction.BinaryInst.RTypeBinary;
import BackEnd.Instruction.Branch.BinaryBranch;
import BackEnd.Instruction.Branch.UnaryBranch;
import BackEnd.Instruction.*;
import BackEnd.Operand.ASMGlobalVar;

public interface ASMVisitor {
    void visit(RISCVModule RISCVModule);
    void visit(RISCVFunction RISCVFunction);
    void visit(ASMBlock block);

    void visit(ASMGlobalVar gv);

    void visit(ASMMoveInst inst);
    void visit(ASMUnaryInst inst);
    void visit(ITypeBinary inst);
    void visit(RTypeBinary inst);

    void visit(ASMLoadAddressInst inst);
    void visit(ASMLoadImmediate inst);
    void visit(ASMLoadUpperImmediate inst);

    void visit(ASMLoadInst inst);
    void visit(ASMStoreInst inst);

    void visit(ASMJumpInst inst);
    void visit(BinaryBranch inst);
    void visit(UnaryBranch inst);
    void visit(ASMCallInst inst);
    void visit(ASMReturnInst inst);
}
