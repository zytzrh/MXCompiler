package AST.Function;


import ExceptionHandle.CompileError;

import java.util.HashMap;

public class FunctionTable {
    private HashMap<String, Function> normalTable;

    public FunctionTable(){
        normalTable = new HashMap<String, Function>();
    }
    public void putFunc(String func_name, Function function) throws CompileError {
        if(normalTable.containsKey(func_name)){
            throw new CompileError(null, "Duplicate function name");
        }else{
            normalTable.put(func_name, function);
        }
    }

}
