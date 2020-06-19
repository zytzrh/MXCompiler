package BackEnd;

import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.BaseOffsetAddr;
import IR.LLVMfunction;

import java.util.*;

public class ASMFunction {
    private ASMModule ASMModule;

    private String name;
    private StackFrame stackFrame;

    private ASMBlock entranceBlock;
    private ASMBlock exitBlock;

    private Map<String, ASMBlock> blockMap;

    private Map<VirtualASMRegister, BaseOffsetAddr> gepAddrMap;

    private Map<String, VirtualASMRegister> VRSymbolTable;

    //for VRsymboltable
    public void registerVR(VirtualASMRegister virtualASMRegister){
        String name = virtualASMRegister.getName();
        assert !VRSymbolTable.containsKey(name);
        VRSymbolTable.put(name, virtualASMRegister);
    }

    public void registerVRDuplicateName(VirtualASMRegister virtualASMRegister){
        int postfix = 0;
        String newName = name + "." + postfix;
        while(VRSymbolTable.containsKey(newName)){
            postfix++;
            newName = name + "." + postfix;
        }
        virtualASMRegister.setName(newName);
        VRSymbolTable.put(newName, virtualASMRegister);
    }

    public boolean contains(String name){
        return VRSymbolTable.containsKey(name);
    }

    public VirtualASMRegister getVR(String name) {
        return VRSymbolTable.get(name);
    }

    public Set<VirtualASMRegister> getAllVRSet() {
        Set<VirtualASMRegister> VRs = new HashSet<>();
        for (VirtualASMRegister virtualASMRegister : VRSymbolTable.values()) {
            if (!virtualASMRegister.getDef().isEmpty())
                VRs.add(virtualASMRegister);
        }
        return VRs;
    }

    public void removeVR(VirtualASMRegister vr) {
        assert VRSymbolTable.containsKey(vr.getName());
        VRSymbolTable.remove(vr.getName());
    }

    //
    public ASMFunction(ASMModule ASMModule, String name, LLVMfunction llvMfunction) {
        this.ASMModule = ASMModule;
        this.name = name;
        this.stackFrame = new StackFrame(this);
        gepAddrMap = new HashMap<>();
    }


    public String getName() {
        return name;
    }

    public BackEnd.ASMModule getASMModule() {
        return ASMModule;
    }

    public void setASMModule(BackEnd.ASMModule ASMModule) {
        this.ASMModule = ASMModule;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEntranceBlock(ASMBlock entranceBlock) {
        this.entranceBlock = entranceBlock;
    }

    public ASMBlock getExitBlock() {
        return exitBlock;
    }

    public void setExitBlock(ASMBlock exitBlock) {
        this.exitBlock = exitBlock;
    }

    public void setBlockMap(Map<String, ASMBlock> blockMap) {
        this.blockMap = blockMap;
    }

    public void setGepAddrMap(Map<VirtualASMRegister, BaseOffsetAddr> gepAddrMap) {
        this.gepAddrMap = gepAddrMap;
    }

    public Map<String, VirtualASMRegister> getVRSymbolTable() {
        return VRSymbolTable;
    }

    public void setVRSymbolTable(Map<String, VirtualASMRegister> VRSymbolTable) {
        this.VRSymbolTable = VRSymbolTable;
    }

    public StackFrame getStackFrame() {
        return stackFrame;
    }

    public void setStackFrame(StackFrame stackFrame) {
        this.stackFrame = stackFrame;
    }

    public ASMBlock getEntranceBlock() {
        return entranceBlock;
    }

    public Map<String, ASMBlock> getBlockMap() {
        return blockMap;
    }


    public Map<VirtualASMRegister, BaseOffsetAddr> getGepAddrMap() {
        return gepAddrMap;
    }

    public ArrayList<ASMBlock> getBlocks() {
        ArrayList<ASMBlock> blocks = new ArrayList<>();

        ASMBlock ptr = entranceBlock;
        while (ptr != null) {
            blocks.add(ptr);
            ptr = ptr.getNextBlock();
        }
        return blocks;
    }

    public void addBasicBlock(ASMBlock block) {
        if (entranceBlock == null)
            entranceBlock = block;
        else
            exitBlock.appendBlock(block);
        exitBlock = block;
    }


    private void dfsBasicBlocks(ASMBlock block, ArrayList<ASMBlock> dfsOrder, Set<ASMBlock> dfsVisit) {
        dfsOrder.add(block);
        dfsVisit.add(block);

        for (ASMBlock successor : block.getSuccessors()) {
            if (!dfsVisit.contains(successor)) {
                dfsBasicBlocks(successor, dfsOrder, dfsVisit);
            }
        }
    }

    public ArrayList<ASMBlock> getDFSOrder() {
        ArrayList<ASMBlock> dfsOrder = new ArrayList<>();
        Set<ASMBlock> dfsVisit = new HashSet<>();
        dfsBasicBlocks(entranceBlock, dfsOrder, dfsVisit);
        return dfsOrder;
    }

    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
