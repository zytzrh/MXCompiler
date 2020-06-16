package BackEnd;

import BackEnd.Instruction.ASMInstruction;
import BackEnd.Instruction.ASMJumpInst;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import IR.Block;

import java.util.LinkedHashSet;
import java.util.Set;

public class ASMBlock {
    private RISCVFunction RISCVFunction;
    private String name;
    private String asmName;

    private Block irBlock;

    private ASMInstruction instHead;
    private ASMInstruction instTail;
    private ASMBlock prevBlock;
    private ASMBlock nextBlock;

    private Set<ASMBlock> predecessors;
    private Set<ASMBlock> successors;

    private Set<VirtualASMRegister> liveOut;
    private Set<VirtualASMRegister> UEVar;
    private Set<VirtualASMRegister> varKill;

    public ASMBlock(RISCVFunction RISCVFunction, Block irBlock, String name, String asmName) {
        this.RISCVFunction = RISCVFunction;
        this.name = name;
        this.asmName = asmName;

        this.irBlock = irBlock;

        instHead = null;
        instTail = null;
        prevBlock = null;
        nextBlock = null;

        predecessors = new LinkedHashSet<>();
        successors = new LinkedHashSet<>();
    }

    public RISCVFunction getRISCVFunction() {
        return RISCVFunction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsmName() {
        return asmName;
    }

    public Block getIrBlock() {
        return irBlock;
    }

    public boolean isEmpty() {
        return instHead == instTail && instHead == null;
    }

    public ASMInstruction getInstHead() {
        return instHead;
    }

    public void setInstHead(ASMInstruction instHead) {
        this.instHead = instHead;
    }

    public ASMInstruction getInstTail() {
        return instTail;
    }

    public void setInstTail(ASMInstruction instTail) {
        this.instTail = instTail;
    }

    public void setPrevBlock(ASMBlock prevBlock) {
        this.prevBlock = prevBlock;
    }

    public ASMBlock getPrevBlock() {
        return prevBlock;
    }

    public void setNextBlock(ASMBlock nextBlock) {
        this.nextBlock = nextBlock;
    }

    public ASMBlock getNextBlock() {
        return nextBlock;
    }

    public Set<ASMBlock> getPredecessors() {
        return predecessors;
    }

    public Set<ASMBlock> getSuccessors() {
        return successors;
    }

    public Set<VirtualASMRegister> getLiveOut() {
        return liveOut;
    }

    public void setLiveOut(Set<VirtualASMRegister> liveOut) {
        this.liveOut = liveOut;
    }

    public Set<VirtualASMRegister> getUEVar() {
        return UEVar;
    }

    public void setUEVar(Set<VirtualASMRegister> UEVar) {
        this.UEVar = UEVar;
    }

    public Set<VirtualASMRegister> getVarKill() {
        return varKill;
    }

    public void setVarKill(Set<VirtualASMRegister> varKill) {
        this.varKill = varKill;
    }

    public void appendBlock(ASMBlock block) {
        block.prevBlock = this;
        this.nextBlock = block;
    }

    public void addInstruction(ASMInstruction instruction) {
        if (isEmpty())
            instHead = instruction;
        else {
            instTail.setNextInst(instruction);
            instruction.setPrevInst(instTail);
        }
        instTail = instruction;
    }

    public void addInstructionAtFront(ASMInstruction instruction) {
        if (isEmpty())
            instTail = instruction;
        else {
            instHead.setPrevInst(instruction);
            instruction.setNextInst(instHead);
        }
        instHead = instruction;
    }

    public void addInstructionNext(ASMInstruction inst1, ASMInstruction inst2) {
        if (inst1 == instTail) {
            inst2.setPrevInst(inst1);
            inst2.setNextInst(null);
            inst1.setNextInst(inst2);
            instTail = inst2;
        } else {
            inst2.setPrevInst(inst1);
            inst2.setNextInst(inst1.getNextInst());
            inst1.getNextInst().setPrevInst(inst2);
            inst1.setNextInst(inst2);
        }
    }

    public void addInstructionPrev(ASMInstruction inst1, ASMInstruction inst2) {
        if (inst1 == instHead) {
            inst2.setNextInst(inst1);
            inst2.setPrevInst(null);
            inst1.setPrevInst(inst2);
            instHead = inst2;
        } else {
            inst2.setNextInst(inst1);
            inst2.setPrevInst(inst1.getPrevInst());
            inst1.getPrevInst().setNextInst(inst2);
            inst1.setPrevInst(inst2);
        }
    }

    public void removeTailJump() {
        assert instTail instanceof ASMJumpInst;
        ASMJumpInst jump = ((ASMJumpInst) instTail);
        if (jump.getPrevInst() == null) {
            instHead = null;
            instTail = null;
        } else {
            jump.getPrevInst().setNextInst(null);
            instTail = jump.getPrevInst();
        }
        jump.setDest(null);
    }

    public String emitCode() {
        return asmName;
    }

    @Override
    public String toString() {
        return name;
    }

    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
