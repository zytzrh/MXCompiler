package AST.VariableEntity;

import Type.Type;

public class VariableEntity {
    private String id;
    private Type type;
    public VariableEntity(String id, Type type){
        this.id = id;
        this.type = type;
    }
}
