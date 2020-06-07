package IR;

import IR.Instruction.*;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;

import java.util.*;

public class Block {
    private String name;
    private LLVMfunction function;

    private LLVMInstruction instHead;
    private LLVMInstruction instTail;


    private Block prev;
    private Block next;
    private Set<Block> predecessors;
    private Set<Block> successors;

    private boolean Terminal;

    private Map<LLVMInstruction, Integer> use;  //gugu changed: maybe useless?

    public Block(String name, LLVMfunction function) {
        this.name = name;
        this.function = function;

        this.instHead = null;
        this.instTail = null;

        this.prev = null;
        this.next = null;
        this.predecessors = new LinkedHashSet<>();   //gugu changed
        this.successors = new LinkedHashSet<>();

        this.Terminal = false;
        this.use = new LinkedHashMap<>();   //why linkedHashMap ? gugu changed
    }

    public void addInstruction(LLVMInstruction newInstruction){
        if(!Terminal){
            if(instHead == null && instTail == null){
                instHead = newInstruction;
                instTail = newInstruction;
            }else{
                instTail.setPostInst(newInstruction);
                newInstruction.setPreInst(instTail);    //gugu changed
                instTail = newInstruction;
            }

            /*more to do with specific instruction adding*/
            //BranchInst
            if(newInstruction instanceof BranchInst){
                Terminal = true;
                BranchInst branchInst = (BranchInst) newInstruction;
                if(branchInst.getCondition() != null){
                    Block thenBlock = branchInst.getThenBlock();
                    Block elseBlock = branchInst.getElseBlock();
                    Block block = branchInst.getBlock();
                    Operand cond = branchInst.getCondition();

                    block.getSuccessors().add(thenBlock);
                    block.getSuccessors().add(elseBlock);
                    thenBlock.getPredecessors().add(block);
                    elseBlock.getPredecessors().add(block);

                    cond.addUse(branchInst);
                    thenBlock.addUse(branchInst);
                    elseBlock.addUse(branchInst);
                }else{
                    Block thenBlock = branchInst.getThenBlock();
                    Block block = branchInst.getBlock();

                    block.getSuccessors().add(thenBlock);
                    thenBlock.getPredecessors().add(block);

                    thenBlock.addUse(branchInst);
                }
            }
            //ReturnInst
            else if(newInstruction instanceof ReturnInst){
                Terminal = true;
                ReturnInst returnInst = (ReturnInst) newInstruction;
                Operand returnValue = returnInst.getReturnValue();

                if(returnValue != null)
                    returnValue.addUse(returnInst);
            }
            //AlloInst
            else if(newInstruction instanceof AllocInst){
                AllocInst allocInst = (AllocInst) newInstruction;
                Register result = allocInst.getResult();

                result.setDef(allocInst);
            }
            //BinaryOpInst
            else if(newInstruction instanceof BinaryOpInst){
                BinaryOpInst binaryOpInst = (BinaryOpInst) newInstruction;
                Register result = binaryOpInst.getResult();
                Operand lhs = binaryOpInst.getLhs();
                Operand rhs = binaryOpInst.getRhs();

                result.setDef(binaryOpInst);
                lhs.addUse(binaryOpInst);
                rhs.addUse(binaryOpInst);//gugu changed: lhs/rhs may be not const
            }
            //BitCastInst
            else if(newInstruction instanceof BitCastInst){
                BitCastInst bitCastInst = (BitCastInst) newInstruction;
                Operand source = bitCastInst.getSource();
                Register result = bitCastInst.getResult();

                result.setDef(bitCastInst);
                source.addUse(bitCastInst);
            }
            //callInst
            else if(newInstruction instanceof CallInst){
                CallInst callInst = (CallInst) newInstruction;
                ArrayList<Operand> paras = callInst.getParas();
                Register result = callInst.getResult();
                LLVMfunction function = callInst.getLlvMfunction();
                for(Operand para: paras){
                    para.addUse(callInst);
                }
                if(result != null){
                    result.setDef(callInst);
                }
                function.addUse(callInst);
            }
            //GEPInst
            else if(newInstruction instanceof GEPInst){
                GEPInst gepInst = (GEPInst) newInstruction;
                ArrayList<Operand> indexs = gepInst.getIndexs();
                Register result = gepInst.getResult();
                Operand pointer = gepInst.getPointer();

                result.setDef(gepInst);
                pointer.addUse(gepInst);
                for(Operand operand:indexs){
                    operand.addUse(gepInst);
                }
            }
            //IcmpInst
            else if(newInstruction instanceof IcmpInst){
                IcmpInst icmpInst  = (IcmpInst) newInstruction;
                Register result = icmpInst.getResult();
                Operand op1 = icmpInst.getOp1();
                Operand op2 = icmpInst.getOp2();

                result.setDef(icmpInst);
                op1.addUse(icmpInst);
                op2.addUse(icmpInst);
            }
            //LoadInst
            else if(newInstruction instanceof LoadInst){
                LoadInst loadInst = (LoadInst) newInstruction;
                Register result = loadInst.getResult();
                Operand addr = loadInst.getAddr();

                result.setDef(loadInst);
                addr.addUse(loadInst);
            }
            //StoreInst
            else if(newInstruction instanceof StoreInst){
                StoreInst storeInst = (StoreInst) newInstruction;
                Operand value = storeInst.getValue();
                Operand addr = storeInst.getAddr();

                value.addUse(storeInst);
                addr.addUse(storeInst);
            }

        }else{
            //is Terminal and do nothing

        }
    }

    public void appendBlock(Block block) {
        this.setNext(block);
        block.setPrev(this);
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

    @Override
    public String toString() {
        return "%" + name;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public void addUse(LLVMInstruction instruction){
        if(!use.containsKey(instruction)){
            use.put(instruction,1);
        }else{
            use.put(instruction, use.get(instruction) + 1);
        }
    }

    public void removeUse(LLVMInstruction instruction){
        int cnt = use.get(instruction);
        if(cnt == 1)
            use.remove(instruction);
        else
            use.replace(instruction, cnt-1);
    }

    public Map<LLVMInstruction, Integer> getUse() {
        return use;
    }

    public LLVMInstruction getInstHead() {
        return instHead;
    }

    public void setInstHead(LLVMInstruction instHead) {
        this.instHead = instHead;
    }

    public LLVMInstruction getInstTail() {
        return instTail;
    }

    public void setInstTail(LLVMInstruction instTail) {
        this.instTail = instTail;
    }

    public Set<Block> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(Set<Block> predecessors) {
        this.predecessors = predecessors;
    }

    public Set<Block> getSuccessors() {
        return successors;
    }

    public void setSuccessors(Set<Block> successors) {
        this.successors = successors;
    }

    public Block getPrev() {
        return prev;
    }

    public void setPrev(Block prev) {
        this.prev = prev;
    }

    public Block getNext() {
        return next;
    }

    public void setNext(Block next) {
        this.next = next;
    }

    public boolean hasPredecessor(){
        return this.getPredecessors().size() != 0;
    }


}


