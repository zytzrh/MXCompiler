package BackEnd;

import BackEnd.Operand.ASMRegister.PhysicalASMRegister;
import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.BaseOffsetAddr;
import IR.Block;
import IR.Instruction.LLVMInstruction;
import IR.Instruction.MoveInst;
import IR.LLVMfunction;
import IR.LLVMoperand.Register;

import java.util.*;

public class RISCVFunction {
    private RISCVModule RISCVModule;

    private String name;
    private StackFrame stackFrame;

    private ASMBlock entranceBlock;
    private ASMBlock exitBlock;

    private Map<String, ASMBlock> blockMap;
    private Set<PhysicalASMRegister> usedCalleeRegister;

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
    public RISCVFunction(RISCVModule RISCVModule, String name, LLVMfunction IRFunction) {
        this.RISCVModule = RISCVModule;
        this.name = name;
        this.stackFrame = null;

        if (IRFunction == null)
            return;

        usedCalleeRegister = new HashSet<>();
        gepAddrMap = new HashMap<>();


        int functionCnt = RISCVModule.getFunctionMap().size();
        int blockCnt = 0;
        blockMap = new HashMap<>();
        ArrayList<Block> IRBlocks = IRFunction.getBlocks();
        for (Block IRBlock : IRBlocks) {
            ASMBlock block = new ASMBlock(this, IRBlock, IRBlock.getName(),
                    ".ASMBlock" + functionCnt + "_" + blockCnt);
            this.addBasicBlock(block);
            blockMap.put(block.getName(), block);
            blockCnt++;
        }
        for (Block IRBlock : IRBlocks) {
            ASMBlock block = blockMap.get(IRBlock.getName());
            Set<ASMBlock> predecessors = block.getPredecessors();
            Set<ASMBlock> successors = block.getSuccessors();

            for (Block predecessor : IRBlock.getPredecessors())
                predecessors.add(blockMap.get(predecessor.getName()));
            for (Block successor : IRBlock.getSuccessors())
                successors.add(blockMap.get(successor.getName()));
        }
        entranceBlock = blockMap.get(IRBlocks.get(0).getName());
        exitBlock = blockMap.get(IRBlocks.get(IRBlocks.size() - 1).getName());


        VRSymbolTable = new HashMap<>();
        for (Register parameter : IRFunction.getParas()) {
            VirtualASMRegister vr = new VirtualASMRegister(parameter.getName());
            registerVR(vr);
        }
        for (Block IRBlock : IRBlocks) {
            LLVMInstruction ptr = IRBlock.getInstHead();
            while (ptr != null) {
                if (ptr.hasResult()) {
                    String registerName = ptr.getResult().getName();
                    if (!(ptr instanceof MoveInst)) {
                        VirtualASMRegister vr = new VirtualASMRegister(registerName);
                        registerVR(vr);
                    } else {
                        //for Move
                        if (!contains(registerName)) {
                            VirtualASMRegister vr = new VirtualASMRegister(registerName);
                            registerVR(vr);
                        }
                    }
                }
                ptr = ptr.getPostInst();
            }
        }
    }

    public String getName() {
        return name;
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
