package BackEnd.Operand.ASMRegister;

import BackEnd.Instruction.ASMInstruction;
import BackEnd.Instruction.ASMLoadInst;
import BackEnd.Instruction.ASMMoveInst;
import BackEnd.Instruction.ASMStoreInst;

import java.util.*;

public class VirtualASMRegister extends ASMRegister {
    private String name;

    private Map<ASMInstruction, Integer> use;
    private Map<ASMInstruction, Integer> def;

    // Register allocator
    private boolean isColorFixed;
    private PhysicalASMRegister coloredPR;

    private ArrayList<VirtualASMRegister> adjList;
    private int degree;
    private Set<ASMMoveInst> moveList;
    private VirtualASMRegister alias;

    private double spillCost;

    public VirtualASMRegister(String name) {
        this.name = name;
        isColorFixed = false;
        coloredPR = null;
        use = new HashMap<>();
        def = new HashMap<>();
        adjList = new ArrayList<>();
        degree = 0;
        moveList = new HashSet<>();
        alias = null;
        spillCost = 0;
    }

    public void fixColor(PhysicalASMRegister pr) {
        isColorFixed = true;
        coloredPR = pr;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addUse(ASMInstruction instruction) {
        if (use.containsKey(instruction))
            use.replace(instruction, use.get(instruction) + 1);
        else
            use.put(instruction, 1);
    }

    public void removeUse(ASMInstruction instruction) {
        assert use.containsKey(instruction);
        if (use.get(instruction) == 1)
            use.remove(instruction);
        else
            use.replace(instruction, use.get(instruction) - 1);
    }

    public void addDef(ASMInstruction instruction) {
        if (def.containsKey(instruction))
            def.replace(instruction, def.get(instruction) + 1);
        else
            def.put(instruction, 1);
    }

    public void removeDef(ASMInstruction instruction) {
        assert def.containsKey(instruction);
        if (def.get(instruction) == 1)
            def.remove(instruction);
        else
            def.replace(instruction, def.get(instruction) - 1);
    }

    public Map<ASMInstruction, Integer> getUse() {
        return use;
    }

    public Map<ASMInstruction, Integer> getDef() {
        return def;
    }


    public void clearColoringData() {
        assert !isColorFixed;

        adjList = new ArrayList<>();
        degree = 0;
        moveList = new HashSet<>();
        alias = null;
        coloredPR = null;
        spillCost = 0;
    }

    public ArrayList<VirtualASMRegister> getAdjList() {
        return adjList;
    }

    public int getDegree() {
        return degree;
    }

    public void increaseDegree() {
        degree++;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public Set<ASMMoveInst> getMoveList() {
        return moveList;
    }

    public VirtualASMRegister getAlias() {
        return alias;
    }

    public void setAlias(VirtualASMRegister alias) {
        this.alias = alias;
    }

    public PhysicalASMRegister getColoredPR() {
        assert coloredPR != null;
        return coloredPR;
    }

    public boolean hasAColor() {
        return coloredPR != null;
    }

    public void setColoredPR(PhysicalASMRegister coloredPR) {
        assert coloredPR != null;
        this.coloredPR = coloredPR;
    }

    public void increaseSpillCost(double cost) {
        spillCost += cost;
    }

    private boolean haveNegativeSpillCosts() {
        if (getDef().size() == 1 && getUse().size() == 1) {
            ASMInstruction def = getDef().keySet().iterator().next();
            ASMInstruction use = getUse().keySet().iterator().next();
            if (def instanceof ASMLoadInst && use instanceof ASMStoreInst)
                return ((ASMLoadInst) def).getAddr().equals(((ASMStoreInst) use).getAddr());
        }
        return false;
    }

    private boolean haveInfiniteSpillCosts() {
        return getDef().size() == 1 && getUse().size() == 1
                && getDef().keySet().iterator().next().getNextInst() == getUse().keySet().iterator().next();
    }

    public double computeSpillRatio() {
        if (haveNegativeSpillCosts())
            return Double.NEGATIVE_INFINITY;
        else if (haveInfiniteSpillCosts())
            return Double.POSITIVE_INFINITY;
        else
            return spillCost / degree;
    }

    @Override
    public String emitCode() {
        assert hasAColor();
        return coloredPR.getName();
    }

    @Override
    public String toString() {
        return name;
    }
}
