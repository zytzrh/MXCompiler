package AST.VariableEntity;

import Semantic.ASTtype.Type;

public class VariableEntity {
    private String id;
    private Type type;
    public VariableEntity(String id, Type type){
        this.id = id;
        this.type = type;
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
}
