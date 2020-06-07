package IR;

import IR.Instruction.LLVMInstruction;
import IR.LLVMoperand.Register;
import IR.TypeSystem.LLVMtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

        addBlock(block);
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
        register.setRegisterId(newName);
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
        }
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
}
