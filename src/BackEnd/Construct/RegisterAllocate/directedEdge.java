package BackEnd.Construct.RegisterAllocate;

import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import Utility.Pair;

class directedEdge extends Pair<VirtualASMRegister, VirtualASMRegister> {
    public directedEdge(VirtualASMRegister first, VirtualASMRegister second) {
        super(first, second);
        if (first.hashCode() > second.hashCode()) {
            setFirst(second);
            setSecond(first);
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof directedEdge))
            return false;
        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return "(" + getFirst().getName() + ", " + getSecond().getName() + ")";
    }
}
