package Type;

import ExceptionHandle.CompileError;
import Type.NonArray.NonArrayType;

import java.util.HashMap;

public class TypeTable {
    private HashMap<String, NonArrayType> typetable;

    public TypeTable(){
        typetable = new HashMap<String, NonArrayType>();
    }

    public void put(String type_name, NonArrayType type) throws CompileError {
        if(typetable.containsKey(type_name)){
            throw new CompileError(null, "Duplicate class name");
        }else{
            typetable.put(type_name, type);
        }
    }

    public NonArrayType get(String typeName) throws CompileError {
        if(!typetable.containsKey(typeName)){
            throw new CompileError(null, "Type not exist");
        }else{
            return typetable.get(typeName);
        }
    }

    public boolean hasType(String typeName){
        return typetable.containsKey(typeName);
    }
}
