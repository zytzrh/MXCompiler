package AST.Scope;

import ExceptionHandle.CompileError;
import Type.Type;

import java.util.HashMap;

abstract public class Scope {
    private HashMap<String, Type> varType;
    public Scope(){
        varType = new HashMap<String, Type>();
    }
    public boolean hasVar(String varName){
        return varType.containsKey(varName);
    }
    public Type getVarType(String varName) throws CompileError {
        if(!hasVar(varName))
            throw new CompileError(null, "No variable name exist in this scope");
        return varType.get(varName);
    }
}
