package IR;

import IR.Instruction.*;
import IR.LLVMoperand.Operand;
import IR.LLVMoperand.Register;
import Utility.Pair;

import java.util.*;

public class Block implements Cloneable{
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

    private Block dfsParent;
    private Block r_dfsParent;
    private int dfsNum;
    private int r_dfsNum;
    /************************/
    private Block idom;
    private Block semiDom;
    private ArrayList<Block> semiDomChildren;
    private HashSet<Block> strictDominators;

    private Block postIdom;
    private Block postSemiDom;
    private ArrayList<Block> postSemiDomChildren;
    private HashSet<Block> postStrictDominators;

    private HashSet<Block> DF; // Dominance Frontier
    private HashSet<Block> postDF;

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

    public void addInst(LLVMInstruction newInstruction){
        updateTerminal();
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
            afterAddInst(newInstruction);

        }else{
            //is Terminal and do nothing

        }
    }

    public void updateTerminal(){
        if(instTail instanceof BranchInst || instTail instanceof ReturnInst){
            Terminal = true;
        }else{
            Terminal = false;
        }
    }

    public void addInstFront(LLVMInstruction newInstruction){
        if(instHead == null && instTail == null){
            instTail = newInstruction;
            instHead = newInstruction;
            afterAddInst(newInstruction);
        }else{
            instHead.setPreInst(newInstruction);
            newInstruction.setPostInst(instHead);
            instHead = newInstruction;
            afterAddInst(newInstruction);
        }
    }

    public void afterAddInst(LLVMInstruction newInstruction){
        //BranchInst
        if(newInstruction instanceof BranchInst){
            Terminal = true;
            BranchInst branchInst = (BranchInst) newInstruction;
            if(branchInst.getCondition() != null){
                Block thenBlock = branchInst.getIfTrueBlock();
                Block elseBlock = branchInst.getIfFalseBlock();
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
                Block thenBlock = branchInst.getIfTrueBlock();
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
        //PhiInst
        else if(newInstruction instanceof PhiInst){
            PhiInst phiInst = (PhiInst) newInstruction;
            Set<Pair<Operand, Block>> branches = phiInst.getBranches();
            Register result = phiInst.getResult();

            for(Pair<Operand, Block> branch: branches){
                Operand operand = branch.getFirst();
                Block block = branch.getSecond();
                operand.addUse(phiInst);
                block.addUse(phiInst);
            }
            result.setDef(phiInst);
        }
        //moveInst
        else if(newInstruction instanceof MoveInst){
            MoveInst moveInst = (MoveInst) newInstruction;
            Operand source = moveInst.getSource();
            Register result = moveInst.getResult();

            source.addUse(moveInst);
            result.setDef(moveInst);
        }
        //ParallelCopyInst
        else if(newInstruction instanceof ParallelCopyInst){
            //nothing
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

    public void cutSuccessor(Block block){
        this.getSuccessors().remove(block);
        block.getPredecessors().remove(this);
    }

    public void cutBlockForPhi(Block block) {
        LLVMInstruction currentInstruction = instHead;
        while (currentInstruction instanceof PhiInst) {
            LLVMInstruction postInst = currentInstruction.getPostInst();
            ((PhiInst) currentInstruction).cutBlock(block);
            currentInstruction = postInst;
        }
    }

    public void removeFromFunction(){
        LLVMInstruction instruction = instHead;
        while(instruction != null){
            LLVMInstruction nextInstruction = instruction.getPostInst();
            instruction.removeFromBlock();
            instruction = nextInstruction;
        }
        for(Block successor : successors)
            successor.cutBlockForPhi(successor);

        removeFromList();
        cutCFGLink();
    }

    public void removeFromList(){
        assert prev != null;        //gugu changed: never delete initBlock??
        prev.setNext(next);
        if(next == null)
            function.setExitBlock(prev);
        else
            next.setPrev(prev);
    }

    public void cutCFGLink(){
        Iterator<Block> iterator;
        iterator = predecessors.iterator();
        while(iterator.hasNext()){
            Block predecessor = iterator.next();
            predecessor.getSuccessors().remove(this);
            iterator.remove();
        }
        iterator = successors.iterator();
        while(iterator.hasNext()){
            Block successor = iterator.next();
            successor.getPredecessors().remove(this);
            iterator.remove();
        }
    }

    public void justAddInst(LLVMInstruction instruction){
        instruction.setBlock(this);
        instruction.setPreInst(this.instTail);
        if(this.instHead == null && this.instTail == null){
            this.instHead = instruction;
        }else{
            this.instTail.setPostInst(instruction);
        }
        this.instTail = instruction;
    }

    public void beOverriden(Object newUse){
        ArrayList<LLVMInstruction> instructions = new ArrayList<>(use.keySet());
        for(LLVMInstruction instruction : instructions){
            instruction.overrideObject(this, newUse);
        }
        use.clear();
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

    public Block getDfsParent() {
        return dfsParent;
    }

    public void setDfsParent(Block dfsParent) {
        this.dfsParent = dfsParent;
    }

    public Block getR_dfsParent() {
        return r_dfsParent;
    }

    public void setR_dfsParent(Block r_dfsParent) {
        this.r_dfsParent = r_dfsParent;
    }

    public int getDfsNum() {
        return dfsNum;
    }

    public void setDfsNum(int dfsNum) {
        this.dfsNum = dfsNum;
    }

    public int getR_dfsNum() {
        return r_dfsNum;
    }

    public void setR_dfsNum(int r_dfsNum) {
        this.r_dfsNum = r_dfsNum;
    }

    public Block getIdom() {
        return idom;
    }

    public void setIdom(Block idom) {
        this.idom = idom;
    }

    public Block getSemiDom() {
        return semiDom;
    }

    public void setSemiDom(Block semiDom) {
        this.semiDom = semiDom;
    }

    public ArrayList<Block> getSemiDomChildren() {
        return semiDomChildren;
    }

    public void setSemiDomChildren(ArrayList<Block> semiDomChildren) {
        this.semiDomChildren = semiDomChildren;
    }

    public HashSet<Block> getStrictDominators() {
        return strictDominators;
    }

    public void setStrictDominators(HashSet<Block> strictDominators) {
        this.strictDominators = strictDominators;
    }

    public Block getPostIdom() {
        return postIdom;
    }

    public void setPostIdom(Block postIdom) {
        this.postIdom = postIdom;
    }

    public Block getPostSemiDom() {
        return postSemiDom;
    }

    public void setPostSemiDom(Block postSemiDom) {
        this.postSemiDom = postSemiDom;
    }

    public ArrayList<Block> getPostSemiDomChildren() {
        return postSemiDomChildren;
    }

    public void setPostSemiDomChildren(ArrayList<Block> postSemiDomChildren) {
        this.postSemiDomChildren = postSemiDomChildren;
    }

    public HashSet<Block> getPostStrictDominators() {
        return postStrictDominators;
    }

    public void setPostStrictDominators(HashSet<Block> postStrictDominators) {
        this.postStrictDominators = postStrictDominators;
    }

    public HashSet<Block> getDF() {
        return DF;
    }

    public void setDF(HashSet<Block> DF) {
        this.DF = DF;
    }

    public HashSet<Block> getPostDF() {
        return postDF;
    }

    public void setPostDF(HashSet<Block> postDF) {
        this.postDF = postDF;
    }



    public ArrayList<LLVMInstruction> getInstructions() {
        ArrayList<LLVMInstruction> instructions = new ArrayList<>();
        LLVMInstruction ptr = instHead;
        while (ptr != null) {
            instructions.add(ptr);
            ptr = ptr.getPostInst();
        }
        return instructions;
    }

    public void addInstructionPrev(LLVMInstruction inst1, LLVMInstruction inst2) {
        // Assure that inst1 is in this block.
        if (inst1.getPreInst() == null) {
            inst1.setPreInst(inst2);
            inst2.setPostInst(inst1);
            this.setInstHead(inst2);
        } else {
            inst2.setPreInst(inst1.getPreInst());
            inst2.setPostInst(inst1);
            inst1.getPreInst().setPostInst(inst2);
            inst1.setPreInst(inst2);
        }
        afterAddInst(inst2);
    }

    public ParallelCopyInst getParallelCopy() {
        LLVMInstruction ptr = this.getInstTail();
        while (ptr != null && !(ptr instanceof ParallelCopyInst))
            ptr = ptr.getPreInst();
        return ptr == null ? null : ((ParallelCopyInst) ptr);
    }

    public String getNameWithoutDot() {
        if (name.contains(".")) {
            String[] strings = name.split("\\.");
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < strings.length - 2; i++)
                res.append(strings[i]).append('.');
            res.append(strings[strings.length - 2]);
            return res.toString();
        } else
            throw new RuntimeException();
    }

    public boolean dominate(Block block) {
        return this == block || block.getStrictDominators().contains(this);
    }

    public boolean isNotExitBlock() {
        return !(instTail instanceof ReturnInst);
    }

    public boolean dceRemoveFromFunction() {
        if (successors.size() != 1)
            return false;
        if (prev == null)
            function.setInitBlock(next);
        else
            prev.setNext(next);

        if (next == null)
            function.setExitBlock(prev);
        else
            next.setPrev(prev);

        Block successor = successors.iterator().next();
        this.beOverriden(successor);
        successor.getPredecessors().remove(this);
        for (Block predecessor : predecessors) {
            predecessor.getInstHead().overrideObject(this, successor);
            predecessor.getSuccessors().remove(this);
            predecessor.getSuccessors().add(successor);
            successor.getPredecessors().add(predecessor);
        }
        return true;
    }

    public void fixBlockOfInstruction(){
        LLVMInstruction ptr = instHead;
        while (ptr != null) {
            ptr.setBlock(this);
            ptr = ptr.getPostInst();
        }
    }

    public Block makeCopy(){
        Block block = new Block(this.getName(), this.function);
        block.setUse(new LinkedHashMap<>());
        ArrayList<LLVMInstruction> instructions = new ArrayList<>();
        LLVMInstruction ptr = this.instHead;
        while (ptr != null) {
            instructions.add((LLVMInstruction) ptr.makeCopy());
            ptr = ptr.getPostInst();
        }
        for (int i = 0; i < instructions.size(); i++) {
            LLVMInstruction instruction = instructions.get(i);
            instruction.setPreInst(i != 0 ? instructions.get(i - 1) : null);
            instruction.setPostInst(i != instructions.size() - 1 ? instructions.get(i + 1) : null);
            instruction.setBlock(block);
        }

        if (instructions.isEmpty()) {
            block.instHead = null;
            block.instTail = null;
        } else {
            block.instHead = instructions.get(0);
            block.instTail = instructions.get(instructions.size() - 1);
        }
        block.prev = this.prev;
        block.next = this.next;
        block.predecessors = new HashSet<>(this.predecessors);
        block.successors = new HashSet<>(this.successors);
        return block;
    }





    public Block split(LLVMInstruction instruction){
        Block splitBlock = new Block("inlineMergeBlock", function);
        function.registerBlockName(splitBlock.getName(), splitBlock);
//        function.getSymbolTable().put(splitBlock.getName(), splitBlock);
        for (Block successor : this.successors) {
            splitBlock.getSuccessors().add(successor);
            successor.getPredecessors().remove(this);
            successor.getPredecessors().add(splitBlock);

            LLVMInstruction ptr = successor.getInstHead();
            while (ptr instanceof PhiInst) {
                Operand operand = null;
                for (Pair<Operand, Block> pair : ((PhiInst) ptr).getBranches()) {
                    if (pair.getSecond() == this) {
                        operand = pair.getFirst();
                    }
                }
                assert operand != null;
                ((PhiInst) ptr).cutBlock(this);
                ((PhiInst) ptr).addBranch(operand, splitBlock);
                ptr = ptr.getPostInst();
            }
        }


        splitBlock.setInstHead(instruction.getPostInst());
        splitBlock.setInstTail(this.instTail);
        this.setInstTail(instruction);

        instruction.getPostInst().setPreInst(null);
        instruction.setPostInst(null);

        splitBlock.setNext(this.next);
        if (this.next != null)
            this.next.setPrev(splitBlock);
        splitBlock.setPrev(this);
        this.setNext(splitBlock);

        if (this.getFunction().getExitBlock() == this)
            this.getFunction().setExitBlock(splitBlock);

        this.successors = new LinkedHashSet<>();


        LLVMInstruction ptr = splitBlock.getInstHead();
        while (ptr != null) {
            ptr.setBlock(splitBlock);
            ptr = ptr.getPostInst();
        }

        return splitBlock;

    }

    public void setUse(Map<LLVMInstruction, Integer> use) {
        this.use = use;
    }

    @Override
    public Object clone() {
        Block block;
        try {
            block = ((Block) super.clone());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        block.setUse(new LinkedHashMap<>());
        ArrayList<LLVMInstruction> instructions = new ArrayList<>();
        LLVMInstruction ptr = this.instHead;
        while (ptr != null) {
            instructions.add((LLVMInstruction) ptr.clone());
            ptr = ptr.getPostInst();
        }
        for (int i = 0; i < instructions.size(); i++) {
            LLVMInstruction instruction = instructions.get(i);
            instruction.setPreInst(i != 0 ? instructions.get(i - 1) : null);
            instruction.setPostInst(i != instructions.size() - 1 ? instructions.get(i + 1) : null);
            instruction.setBlock(block);
        }

        block.function = this.function;
        block.name = this.name;
        if (instructions.isEmpty()) {
            block.instHead = null;
            block.instTail = null;
        } else {
            block.instHead = instructions.get(0);
            block.instTail = instructions.get(instructions.size() - 1);
        }
        block.prev = this.prev;
        block.next = this.next;
        block.predecessors = new HashSet<>(this.predecessors);
        block.successors = new HashSet<>(this.successors);
        return block;
    }
}


