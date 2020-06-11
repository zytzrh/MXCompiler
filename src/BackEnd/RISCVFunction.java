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
    private SymbolTable symbolTable;
    private Set<PhysicalASMRegister> usedCalleeRegister;

    private Map<VirtualASMRegister, BaseOffsetAddr> gepAddrMap;

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
                    ".LBB" + functionCnt + "_" + blockCnt);
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


        symbolTable = new SymbolTable();
        for (Register parameter : IRFunction.getParas()) {
            VirtualASMRegister vr = new VirtualASMRegister(parameter.getName());
            symbolTable.putASM(parameter.getName(), vr);
        }
        for (Block IRBlock : IRBlocks) {
            LLVMInstruction ptr = IRBlock.getInstHead();
            while (ptr != null) {
                if (ptr.hasResult()) {
                    String registerName = ptr.getResult().getName();
                    if (!(ptr instanceof MoveInst)) {
                        VirtualASMRegister vr = new VirtualASMRegister(registerName);
                        symbolTable.putASM(registerName, vr);
                    } else { // "Move" is special.
                        if (!symbolTable.contains(registerName)) {
                            VirtualASMRegister vr = new VirtualASMRegister(registerName);
                            symbolTable.putASM(registerName, vr);
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

    public SymbolTable getSymbolTable() {
        return symbolTable;
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

    public void addBasicBlockNext(ASMBlock block1, ASMBlock block2) {
        // It is ensured that block1 is in this function.
        if (block1 == exitBlock) {
            block2.setNextBlock(null);
            block2.setPrevBlock(block1);
            block1.setNextBlock(block2);
            exitBlock = block2;
        } else {
            block2.setNextBlock(block1.getNextBlock());
            block2.setPrevBlock(block1);
            block1.getNextBlock().setPrevBlock(block2);
            block1.setNextBlock(block2);
        }
    }

    public void splitBlockFromFunction(ASMBlock block) {
        // It is ensured that block is in this function.
        if (block.getPrevBlock() == null)
            entranceBlock = block.getNextBlock();
        else
            block.getPrevBlock().setNextBlock(block.getNextBlock());
        if (block.getNextBlock() == null)
            exitBlock = block.getPrevBlock();
        else
            block.getNextBlock().setPrevBlock(block.getPrevBlock());

        block.setPrevBlock(null);
        block.setNextBlock(null);
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
