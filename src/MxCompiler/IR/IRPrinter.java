package MxCompiler.IR;

import IR.Block;
import IR.IRVisitor;
import IR.Instruction.*;
import IR.LLVMfunction;
import IR.Module;
import IR.TypeSystem.LLVMStructType;
import IR.TypeSystem.LLVMtype;

import java.io.*;
import java.util.HashMap;

public class IRPrinter implements IRVisitor {
    private File outputFile;
    private OutputStream os;
    private PrintWriter writer;
    private String indent;

    public IRPrinter(String filename) {
        try {
            outputFile = new File(filename);
            assert outputFile.exists() || outputFile.createNewFile();
            os = new FileOutputStream(filename, false);
            writer = new PrintWriter(os);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        indent = "    ";
    }


    private void print(String string) {
        writer.print(string);
    }

    private void println(String string) {
        writer.println(string);
    }


    @Override
    public void visit(Module module) throws IOException {
        //struct
        for(LLVMtype llvMtype : module.getTypeMap().values()){
            if(llvMtype instanceof LLVMStructType)
                println(((LLVMStructType) llvMtype).printInnerStructure());
        }
        println("");
        //global variable
        for(DefineGlobal defineGlobal : module.getDefineGlobals()){
            println(defineGlobal.toString());
        }
        println("");
        //builtIn function
        for(LLVMfunction llvMfunction : module.getBuiltInFunctionMap().values()){
            println(llvMfunction.printDeclaratiion());
        }
        println("");
        //function
        for(LLVMfunction llvMfunction : module.getFunctionMap().values()){
            llvMfunction.accept(this);
        }

        writer.close();
        os.close();
    }



    @Override
    public void visit(LLVMfunction function) {
        println(function.printDeclaratiion().replace("declare", "define") + " {");

        for(HashMap<String, Block> blockHashMap : function.getBlockNameManager().values()){
            for(Block block : blockHashMap.values()){
                block.accept(this);
                println("");
            }
        }

        println("}");
    }

    @Override
    public void visit(Block block) {
        print(block.getName() + ":");
        if (block.getDirectPredecessor() != null) {
            print(" ".repeat(50 - (block.getName().length() + 1)));
            print("; preds = ");
            print(block.getDirectPredecessor().getName());
        }


        println("");

        for(LLVMInstruction llvmInstruction : block.getInstructions()){
            llvmInstruction.accept(this);
        }
    }

    @Override
    public void visit(ReturnInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(BranchInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(BinaryOpInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(AllocInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(LoadInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(StoreInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(GEPInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(BitCastInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(IcmpInst inst) {
        println(indent + inst.toString());
    }


    @Override
    public void visit(CallInst inst) {
        println(indent + inst.toString());
    }

    @Override
    public void visit(DefineGlobal defineGlobal) {
        println(indent + defineGlobal.toString());
    }

}
