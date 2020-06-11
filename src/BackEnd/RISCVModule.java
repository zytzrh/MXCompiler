package BackEnd;

import BackEnd.Operand.ASMGlobalVar;

import java.util.LinkedHashMap;
import java.util.Map;

public class RISCVModule {
    private Map<String, RISCVFunction> functionMap;
    private Map<String, RISCVFunction> externalFunctionMap;
    private Map<String, ASMGlobalVar> globalVariableMap;

    public RISCVModule() {
        functionMap = new LinkedHashMap<>();
        externalFunctionMap = new LinkedHashMap<>();
        globalVariableMap = new LinkedHashMap<>();
    }

    public Map<String, RISCVFunction> getFunctionMap() {
        return functionMap;
    }

    public Map<String, RISCVFunction> getExternalFunctionMap() {
        return externalFunctionMap;
    }

    public Map<String, ASMGlobalVar> getGlobalVariableMap() {
        return globalVariableMap;
    }

    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
