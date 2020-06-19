package BackEnd;

import BackEnd.Operand.ASMGlobalVar;

import java.util.LinkedHashMap;
import java.util.Map;

public class ASMModule {
    private Map<String, ASMFunction> functionMap;
    private Map<String, ASMFunction> builtInFunctionMap;
    private Map<String, ASMGlobalVar> globalVariableMap;

    public ASMModule() {
        functionMap = new LinkedHashMap<>();
        builtInFunctionMap = new LinkedHashMap<>();
        globalVariableMap = new LinkedHashMap<>();
    }

    public Map<String, ASMFunction> getFunctionMap() {
        return functionMap;
    }

    public ASMFunction getFunction(String funcName){
        ASMFunction callee;
        if (functionMap.containsKey(funcName))
            callee = functionMap.get(funcName);
        else{
            assert builtInFunctionMap.containsKey(funcName);
            callee = builtInFunctionMap.get(funcName);
        }
        return callee;
    }
    public Map<String, ASMFunction> getBuiltInFunctionMap() {
        return builtInFunctionMap;
    }

    public Map<String, ASMGlobalVar> getGlobalVariableMap() {
        return globalVariableMap;
    }

    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
