package BackEnd;

import BackEnd.Operand.ASMGlobalVar;

import java.util.LinkedHashMap;
import java.util.Map;

public class RISCVModule {
    private Map<String, RISCVFunction> functionMap;
    private Map<String, RISCVFunction> builtInFunctionMap;
    private Map<String, ASMGlobalVar> globalVariableMap;

    public RISCVModule() {
        functionMap = new LinkedHashMap<>();
        builtInFunctionMap = new LinkedHashMap<>();
        globalVariableMap = new LinkedHashMap<>();
    }

    public Map<String, RISCVFunction> getFunctionMap() {
        return functionMap;
    }

    public RISCVFunction getFunction(String funcName){
        RISCVFunction callee;
        if (functionMap.containsKey(funcName))
            callee = functionMap.get(funcName);
        else{
            assert builtInFunctionMap.containsKey(funcName);
            callee = builtInFunctionMap.get(funcName);
        }
        return callee;
    }
    public Map<String, RISCVFunction> getBuiltInFunctionMap() {
        return builtInFunctionMap;
    }

    public Map<String, ASMGlobalVar> getGlobalVariableMap() {
        return globalVariableMap;
    }

    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
