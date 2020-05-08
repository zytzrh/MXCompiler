package AST.Function;


import Semantic.ExceptionHandle.CompileError;

import java.util.HashMap;

public class FunctionTable {
    private HashMap<String, Function> globalTable;
    private HashMap<String, Function> methodTable;
    public FunctionTable(){
        globalTable = new HashMap<String, Function>();
        methodTable = null;
    }

    public void putFunc(String funcName, Function function) throws CompileError {
        if(globalTable.containsKey(funcName)){
            throw new CompileError(null, "Duplicate function name");
        }else{
            globalTable.put(funcName, function);
        }
    }

    public void putMethod(HashMap<String, Function> methodTable){
        this.methodTable = methodTable;
    }

    public void clearMethod(){
        this.methodTable = null;
    }

    public boolean hasFunc(String funcName){
        if(methodTable != null && methodTable.containsKey(funcName)){
            return true;
        }else if(globalTable.containsKey(funcName)){
            return true;
        }else{
            return false;
        }
    }

    //Find method in priority
    public Function getFunc(String funcName) throws CompileError {
        if(!hasFunc(funcName)){
            throw new CompileError(null, "Function name not exist");
        } else{
            if(methodTable != null && methodTable.containsKey(funcName))
                return methodTable.get(funcName);
            else
                return globalTable.get(funcName);
        }
    }

    public HashMap<String, Function> getGlobalTable() {
        return globalTable;
    }
}
