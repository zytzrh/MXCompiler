package IR.Instruction;

import IR.Block;
import IR.IRVisitor;
import IR.LLVMfunction;
import IR.LLVMoperand.ConstString;
import IR.LLVMoperand.GlobalVar;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMPointerType;
import Optimization.Andersen;
import Optimization.CSE;
import Optimization.ConstOptim;
import Optimization.Loop.LoopAnalysis;
import Optimization.SideEffectChecker;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DefineGlobal extends LLVMInstruction {
    private GlobalVar globalVar;
    private Operand init;

    public DefineGlobal(GlobalVar globalVar, Operand init) {
        super(null);
        this.globalVar = globalVar;
        this.init = init;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(globalVar.toString() + " = ");
        //maybe need modified
        assert globalVar.getLlvMtype() instanceof LLVMPointerType;
        if(init instanceof ConstString){
            string.append("private unnamed_addr constant " +
                    ((LLVMPointerType) globalVar.getLlvMtype()).getBaseType().toString() +
                    " " + init.toString());
        }else{
            string.append("global " + ((LLVMPointerType) globalVar.getLlvMtype()).getBaseType().toString()+
                    " " + init.toString());
        }
        return string.toString();
    }

    @Override
    public void accept(IRVisitor irVisitor) {
        irVisitor.visit(this);
    }

    @Override
    public void overrideObject(Object oldUse, Object newUse) {
        assert false;
    }

    @Override
    public boolean replaceResultWithConstant(ConstOptim constOptim) {
        assert false;           //gugu changed
        return false;
    }

    @Override
    public Register getResult() {
        assert false;       //gugu changed
        return null;
    }

    public GlobalVar getGlobalVar() {
        return globalVar;
    }

    public void setGlobalVar(GlobalVar globalVar) {
        this.globalVar = globalVar;
    }

    public Operand getInit() {
        return init;
    }

    public void setInit(Operand init) {
        this.init = init;
    }

    @Override
    public void markUseAsLive(Set<LLVMInstruction> live, Queue<LLVMInstruction> queue) {

    }

    @Override
    public boolean updateResultScope(Map<Operand, SideEffectChecker.Scope> scopeMap, Map<LLVMfunction, SideEffectChecker.Scope> returnValueScope) {
        return false;
    }

    @Override
    public boolean dceRemoveFromBlock(LoopAnalysis loopAnalysis) {
        return super.dceRemoveFromBlock(loopAnalysis);
    }


    @Override
    public LLVMInstruction makeCopy() {
        assert false;
        return null;
    }

    @Override
    public void clonedUseReplace(Map<Block, Block> blockMap, Map<Operand, Operand> operandMap) {
        assert false;
    }

    @Override
    public void addConstraintsForAndersen(Map<Operand, Andersen.Node> nodeMap, Set<Andersen.Node> nodes) {
        assert false;
    }

    @Override
    public CSE.Expression convertToExpression() {
        return null;
    }
}
