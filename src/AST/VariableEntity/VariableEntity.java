package AST.VariableEntity;

import IR.LLVMoperand.Operand;
import Semantic.ASTtype.Type;

public class VariableEntity {
    private String id;
    private Type type;
    private Operand allocAddr;
    public VariableEntity(String id, Type type){
        this.id = id;
        this.type = type;
        allocAddr = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Operand getAllocAddr() {
        return allocAddr;
    }

    public void setAllocAddr(Operand allocAddr) {
        this.allocAddr = allocAddr;
    }
}
