package IR;

import IR.Instruction.AllocInst;
import IR.Instruction.LLVMInstruction;
import IR.Instruction.ReturnInst;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMtype;

import java.util.*;

public class LLVMfunction {
    private String functionName;
    private ArrayList<Register> paras;
    private LLVMtype resultType;
    private Register returnAddr;
    private Register thisAddr;

    private Block initBlock;
    private Block returnBlock;
    private Block exitBlock;    //gugu changed: act as the last block, maybe can be changed

    private HashMap<String, HashMap<String, Register>> varNameManager;
    private HashMap<String, HashMap<String, Block>> blockNameManager;

    private Map<LLVMInstruction, Integer> use;  //gugu changed: name can be modified


    private boolean sideEffect;
    private boolean builtIn;

    public LLVMfunction(String functionName, ArrayList<Register> paras,
                        LLVMtype resultType) {
        this.functionName = functionName;
        this.paras = paras;
        this.resultType = resultType;
        this.thisAddr = null;

        initBlock = null;
        returnBlock = null;

        varNameManager = new HashMap<String, HashMap<String, Register>>();
        blockNameManager = new HashMap<String, HashMap<String, Block>>();

        this.use = new LinkedHashMap<>();
    }

    public void registerBlock(String name, Block block){
        registerBlockName(name, block);

        addBlock(block);
    }

    public void registerBlockName(String name, Block block){
        HashMap<String, Block> sameNameMap;
        if(blockNameManager.containsKey(name)){
            sameNameMap = blockNameManager.get(name);
        }else{
            sameNameMap = new HashMap<String , Block>();
            blockNameManager.put(name, sameNameMap);
        }
        String newName = name + "." + sameNameMap.size();
        block.setName(newName);
        sameNameMap.put(newName, block);
    }

    public void registerVar(String name, Register register){
        HashMap<String, Register> sameNameMap;
        if(varNameManager.containsKey(name)){
            sameNameMap = varNameManager.get(name);
        }else{
            sameNameMap = new HashMap<String, Register>();
            varNameManager.put(name, sameNameMap);
        }
        String newName = name + "." + sameNameMap.size();
        register.setName(newName);
        sameNameMap.put(newName, register);
    }

    public String printDeclaratiion() {
        StringBuilder string = new StringBuilder("declare ");
        string.append(resultType.toString());
        string.append(" @").append(functionName);

        string.append("(");
        for (int i = 0; i < paras.size(); i++) {
            Register para = paras.get(i);
            string.append(para.getLlvMtype().toString()).append(" ");
            string.append(para.toString());
            if (i != paras.size() - 1)
                string.append(", ");
        }
        string.append(")");

        return string.toString();
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

    public void addBlock(Block block) {
        if (exitBlock == null){
            initBlock = exitBlock = block;
        }
        else{
            exitBlock.appendBlock(block);
            exitBlock = block;
        }
    }

    public boolean isFunctional(){
        int returnCount  = 0;
        ReturnInst returnInst = null;
        for(Block block = initBlock; block != null; block = block.getNext()){
            LLVMInstruction instTail = block.getInstTail();
            if(instTail == null || !instTail.isTerminalInst()){
                return false;                        //gugu changed: why??
            }
            if(instTail instanceof ReturnInst){
                returnInst = (ReturnInst) instTail;
                returnCount++;
                if(returnCount > 1)
                    return false;                    //gugu changed: unpossible situation
            }
        }
        Block block = returnInst.getBlock();
        if(block != this.exitBlock){            //gugu changed
            //move to exit
            if(block.getPrev() == null)
                this.setInitBlock(block.getNext());
            else
                block.getPrev().setNext(block.getNext());
            if(block.getNext() == null)
                this.setExitBlock(block.getPrev());
            else
                block.getNext().setPrev(block.getPrev());
            block.setPrev(null);
            block.setNext(null);
            this.addBlock(block);
        }
        return true;

    }

    public ArrayList<Block> getDFSOrder() {
        ArrayList<Block> dfsOrder = new ArrayList<>();
        HashSet<Block>dfsVisit = new HashSet<>();
        Stack<Block> blockStack = new Stack<>();

        initBlock.setDfsParent(null);
        blockStack.push(initBlock);

        while(!blockStack.empty()){
            Block currentBlock = blockStack.pop();
            if(!dfsVisit.contains(currentBlock)){
                currentBlock.setDfsNum(dfsOrder.size());
                dfsOrder.add(currentBlock);
                dfsVisit.add(currentBlock);
                for(Block successor : currentBlock.getSuccessors()){
                    if(!dfsVisit.contains(successor)){
                        successor.setDfsParent(currentBlock);
                        blockStack.push(successor);
                    }
                }
            }
        }

        return dfsOrder;
    }

    public ArrayList<Block> getReverseDFSOrder() {
        ArrayList<Block>reverseDfsOrder = new ArrayList<>();
        HashSet<Block>dfsVisit = new HashSet<>();
        Stack<Block> blockStack = new Stack<>();

        initBlock.setR_dfsParent(null);
        blockStack.push(exitBlock);

        while(!blockStack.empty()){
            Block currentBlock = blockStack.pop();
            if(!dfsVisit.contains(currentBlock)){
                currentBlock.setR_dfsNum(reverseDfsOrder.size());
                reverseDfsOrder.add(currentBlock);
                dfsVisit.add(currentBlock);
                for(Block predecessor : currentBlock.getPredecessors()){
                    if(!dfsVisit.contains(predecessor)){
                        predecessor.setR_dfsParent(currentBlock);
                        blockStack.push(predecessor);
                    }
                }
            }
        }

        return reverseDfsOrder;
    }






    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Block getInitBlock() {
        return initBlock;
    }

    public void setInitBlock(Block initBlock) {
        this.initBlock = initBlock;
    }

    public Block getReturnBlock() {
        return returnBlock;
    }

    public void setReturnBlock(Block returnBlock) {
        this.returnBlock = returnBlock;
    }

    public LLVMtype getResultType() {
        return resultType;
    }

    public void setResultType(LLVMtype resultType) {
        this.resultType = resultType;
    }

    public ArrayList<Register> getParas() {
        return paras;
    }

    public void setParas(ArrayList<Register> paras) {
        this.paras = paras;
    }

    public HashMap<String, HashMap<String, Register>> getVarNameManager() {
        return varNameManager;
    }

    public void setVarNameManager(HashMap<String, HashMap<String, Register>> varNameManager) {
        this.varNameManager = varNameManager;
    }

    public HashMap<String, HashMap<String, Block>> getBlockNameManager() {
        return blockNameManager;
    }

    public void setBlockNameManager(HashMap<String, HashMap<String, Block>> blockNameManager) {
        this.blockNameManager = blockNameManager;
    }

    public Register getThisAddr() {
        return thisAddr;
    }

    public void setThisAddr(Register thisRegister) {
        this.thisAddr = thisRegister;
    }

    public Register getReturnAddr() {
        return returnAddr;
    }

    public void setReturnAddr(Register returnAddr) {
        this.returnAddr = returnAddr;
    }

    public void setUse(Map<LLVMInstruction, Integer> use) {
        this.use = use;
    }

    public boolean isSideEffect() {
        return sideEffect;
    }

    public void setSideEffect(boolean sideEffect) {
        this.sideEffect = sideEffect;
    }

    public Block getExitBlock() {
        return exitBlock;
    }

    public void setExitBlock(Block exitBlock) {
        this.exitBlock = exitBlock;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }

    public ArrayList<Block> getBlocks() {           //gugu changde: delete
        ArrayList<Block> blocks = new ArrayList<>();

        Block ptr = initBlock;
        while (ptr != null) {
            blocks.add(ptr);
            ptr = ptr.getNext();
        }
        return blocks;
    }

    public ArrayList<AllocInst> getAllocaInstructions() {
        ArrayList<AllocInst> allocaInst = new ArrayList<>();
        LLVMInstruction ptr = initBlock.getInstHead();
        while (ptr != null) {
            if (ptr instanceof AllocInst)
                allocaInst.add((AllocInst) ptr);
            ptr = ptr.getPostInst();
        }
        return allocaInst;
    }
    public void addBasicBlockPrev(Block block1, Block block2) {
        // Assume that block1 is in this function.
        assert block1.getPrev() != null;
        block2.setPrev(block1.getPrev());
        block2.setNext(block1);
        block1.getPrev().setNext(block2);
        block1.setPrev(block2);
    }

    public boolean hasSideEffect() {
        return sideEffect;
    }


}
