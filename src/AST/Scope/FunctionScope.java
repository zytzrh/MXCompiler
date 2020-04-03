package AST.Scope;

import Type.Type;

public class FunctionScope extends Scope {
    private Type returnType;
    public FunctionScope(Type returnType){
        super();
        this.returnType = returnType;
    }
}
