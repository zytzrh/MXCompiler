package AST.Scope;

import AST.VariableEntity.VariableEntity;
import Semantic.ExceptionHandle.CompileError;

import java.util.HashMap;

abstract public class Scope {
    private HashMap<String, VariableEntity> varTable;
    public Scope(){
        varTable = new HashMap<String, VariableEntity>();
    }
    public boolean hasVar(String varName){
        return varTable.containsKey(varName);
    }
    public VariableEntity getVarEntity(String varName) throws CompileError {
        if(!hasVar(varName))
            throw new CompileError(null, "No variable name exist in this scope");
        return varTable.get(varName);
    }
    public void put(String varName, VariableEntity varEntity) throws CompileError {
        if(hasVar(varName))
            throw new CompileError(null, "Variable name already exist");
        varTable.put(varName, varEntity);
    }

}
