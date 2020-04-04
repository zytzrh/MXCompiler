package AST.Scope;

import ExceptionHandle.CompileError;
import Type.Type;

import java.util.HashMap;

abstract public class Scope {
    private HashMap<String, Type> varTable;
    public Scope(){
        varTable = new HashMap<String, Type>();
    }
    public boolean hasVar(String varName){
        return varTable.containsKey(varName);
    }
    public Type getVarType(String varName) throws CompileError {
        if(!hasVar(varName))
            throw new CompileError(null, "No variable name exist in this scope");
        return varTable.get(varName);
    }
    public void put(String varName, Type varType) throws CompileError {
        if(hasVar(varName))
            throw new CompileError(null, "Variable name already exist");
        varTable.put(varName, varType);
    }

    public void setVarTable(HashMap<String, Type> varTable) {
        this.varTable = varTable;
    }
}
