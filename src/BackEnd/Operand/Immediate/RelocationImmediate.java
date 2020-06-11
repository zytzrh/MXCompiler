package BackEnd.Operand.Immediate;

import BackEnd.Operand.ASMGlobalVar;

public class RelocationImmediate extends Immediate {
    public enum Type {
        high, low
    }

    private Type type;
    private ASMGlobalVar ASMGlobalVar;

    public RelocationImmediate(Type type, ASMGlobalVar ASMGlobalVar) {
        this.type = type;
        this.ASMGlobalVar = ASMGlobalVar;
    }

    @Override
    public String emitCode() {
        return "%" + (type == Type.high ? "hi" : "lo") + "(" + ASMGlobalVar.getName() + ")";
    }

    @Override
    public String toString() {
        return "%" + (type == Type.high ? "hi" : "lo") + "(" + ASMGlobalVar.getName() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RelocationImmediate))
            return false;
        return type == ((RelocationImmediate) obj).type
                && ASMGlobalVar == ((RelocationImmediate) obj).ASMGlobalVar;
    }
}
