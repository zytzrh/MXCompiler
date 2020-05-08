package IR.Instruction;

import IR.IRVisitor;
import IR.LLVMoperand.ConstString;
import IR.LLVMoperand.GlobalVar;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMPointerType;

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
}
