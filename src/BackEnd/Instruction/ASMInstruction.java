package BackEnd.Instruction;

import BackEnd.ASMBlock;
import BackEnd.ASMVisitor;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

abstract public class ASMInstruction {
    private ASMBlock ASMBlock;
    private ASMInstruction prevInst;
    private ASMInstruction nextInst;

    private Map<VirtualASMRegister, Integer> def;
    private Map<VirtualASMRegister, Integer> use;

    public ASMInstruction(ASMBlock ASMBlock) {
        this.ASMBlock = ASMBlock;
        prevInst = null;
        nextInst = null;

        def = new LinkedHashMap<>();
        use = new LinkedHashMap<>();
    }

    public ASMBlock getASMBlock() {
        return ASMBlock;
    }

    public ASMInstruction getPrevInst() {
        return prevInst;
    }

    public void setPrevInst(ASMInstruction prevInst) {
        this.prevInst = prevInst;
    }

    public ASMInstruction getNextInst() {
        return nextInst;
    }

    public void setNextInst(ASMInstruction nextInst) {
        this.nextInst = nextInst;
    }

    public void addDef(VirtualASMRegister vr) {
        if (def.containsKey(vr))
            def.replace(vr, def.get(vr) + 1);
        else
            def.put(vr, 1);
    }

    public void removeDef(VirtualASMRegister vr) {
        assert def.containsKey(vr);
        if (def.get(vr) == 1)
            def.remove(vr);
        else
            def.replace(vr, def.get(vr) - 1);
    }

    public void replaceDef(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        int cnt = 0;
        for (Map.Entry<VirtualASMRegister, Integer> entry : this.def.entrySet()) {
            VirtualASMRegister def = entry.getKey();
            if (def == oldVR) {
                for (int i = 0; i < entry.getValue(); i++)
                    cnt++;
            }
        }
        for (int i = 0; i < cnt; i++) {
            this.removeDef(oldVR);
            oldVR.removeDef(this);
        }
        for (int i = 0; i < cnt; i++) {
            this.addDef(newVR);
            newVR.addDef(this);
        }
    }

    public Set<VirtualASMRegister> getDef() {
        return def.keySet();
    }

    public void addUse(VirtualASMRegister vr) {
        if (use.containsKey(vr))
            use.replace(vr, use.get(vr) + 1);
        else
            use.put(vr, 1);
    }

    public void removeUse(VirtualASMRegister vr) {
        assert use.containsKey(vr);
        if (use.get(vr) == 1)
            use.remove(vr);
        else
            use.replace(vr, use.get(vr) - 1);
    }

    public void replaceUse(VirtualASMRegister oldVR, VirtualASMRegister newVR) {
        int cnt = 0;
        for (Map.Entry<VirtualASMRegister, Integer> entry : this.use.entrySet()) {
            VirtualASMRegister use = entry.getKey();
            if (use == oldVR) {
                for (int i = 0; i < entry.getValue(); i++)
                    cnt++;
            }
        }
        for (int i = 0; i < cnt; i++) {
            this.removeUse(oldVR);
            oldVR.removeUse(this);
        }
        for (int i = 0; i < cnt; i++) {
            this.addUse(newVR);
            newVR.addUse(this);
        }
    }

    public Set<VirtualASMRegister> getUse() {
        return use.keySet();
    }

    public Set<VirtualASMRegister> getDefUseUnion() {
        Set<VirtualASMRegister> union = new HashSet<>(getDef());
        union.addAll(getUse());
        return union;
    }

    public void addToUEVarAndVarKill(Set<VirtualASMRegister> UEVar, Set<VirtualASMRegister> varKill) {

    }

    abstract public String emitCode();

    @Override
    abstract public String toString();

    abstract public void accept(ASMVisitor visitor);
}
