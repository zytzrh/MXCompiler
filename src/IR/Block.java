package IR;

import IR.Instruction.BranchInst;
import IR.Instruction.LLVMInstruction;
import IR.Instruction.ReturnInst;

import java.util.ArrayList;
import java.util.Set;

public class Block {
    private String name;
    private LLVMfunction function;
    private ArrayList<LLVMInstruction> instructions;

    private Set<Block> predecessors;
    private Set<Block> successors;
    private Block directPredecessor;
    private boolean Terminal;


    public Block(String name, LLVMfunction function) {
        this.name = name;
        this.function = function;
        instructions = new ArrayList<LLVMInstruction>();
        directPredecessor = null;
        Terminal = false;
    }

    public void addInstruction(LLVMInstruction llvmInstruction){
        if(!Terminal){
            instructions.add(llvmInstruction);
            if(llvmInstruction instanceof BranchInst){
                if(((BranchInst) llvmInstruction).getElseBlock() != null)
                    ((BranchInst) llvmInstruction).getElseBlock().setDirectPredecessor(this);
                if(((BranchInst) llvmInstruction).getThenBlock() != null)
                    ((BranchInst) llvmInstruction).getThenBlock().setDirectPredecessor(this);
                Terminal = true;
            }else if(llvmInstruction instanceof ReturnInst){
                Terminal = true;
            }
        }
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LLVMfunction getFunction() {
        return function;
    }

    public void setFunction(LLVMfunction function) {
        this.function = function;
    }

    public Block getDirectPredecessor() {
        return directPredecessor;
    }

    public void setDirectPredecessor(Block directPredecessor) {
        this.directPredecessor = directPredecessor;
    }

    @Override
    public String toString() {
        return "%" + name;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public ArrayList<LLVMInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<LLVMInstruction> instructions) {
        this.instructions = instructions;
    }
}


