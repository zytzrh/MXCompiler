package AST.Scope;

import Type.Type;

public class FunctionScope extends Scope {
    private Type returnType;
    public FunctionScope(Type returnType){
        super();
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }
}
