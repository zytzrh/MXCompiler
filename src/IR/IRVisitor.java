package IR;

import IR.Instruction.*;

import java.io.IOException;

public interface IRVisitor {

    void visit(Module module) throws IOException;

    void visit(LLVMfunction function);

    void visit(Block block);


    void visit(ReturnInst inst);
    void visit(BranchInst inst);
    void visit(BinaryOpInst inst);
    void visit(AllocInst inst);
    void visit(LoadInst inst);
    void visit(StoreInst inst);
    void visit(GEPInst inst);
    void visit(BitCastInst inst);
    void visit(IcmpInst inst);
    void visit(CallInst inst);

    void visit(DefineGlobal defineGlobal);
}
