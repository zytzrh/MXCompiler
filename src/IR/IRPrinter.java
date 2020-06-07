package IR;

import IR.Instruction.*;
import IR.TypeSystem.LLVMStructType;
import IR.TypeSystem.LLVMtype;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class IRPrinter implements IRVisitor {
    PrintStream stdout;
    PrintStream newout;
    String indent;

    public IRPrinter(String filename) throws FileNotFoundException {
        stdout = System.out;
        newout = new PrintStream("IRout.txt");;
        indent = "    ";
    }


    private void print(String string) {
        System.out.print(string);
    }

    private void println(String string) {
        System.out.println(string);
    }


    @Override
    public void visit(Module module) throws IOException {
        System.setOut(newout);
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
            //System.out.println(llvMfunction.printDeclaratiion());
        }
        println("");
        //function
        for(LLVMfunction llvMfunction : module.getFunctionMap().values()){
            llvMfunction.accept(this);
        }

        System.setOut(stdout);
    }



    @Override
    public void visit(LLVMfunction function) {
        println(function.printDeclaratiion().replace("declare", "define") + " {");

        Block nowBlock = function.getInitBlock();
        while (nowBlock != null){
            nowBlock.accept(this);
            if(nowBlock.getNext() != null)  println("");
            nowBlock = nowBlock.getNext();
        }

        println("}");
    }

    @Override
    public void visit(Block block) {            //gugu changde: can be changed a lot
        print(block.getName() + ":");
        if(block.hasPredecessor()){
            print(" ".repeat(50 - (block.getName().length() + 1)));
            print("; preds = ");
            int size = block.getPredecessors().size();
            int cnt = 0;
            for (Block predecessor : block.getPredecessors()) {
                print(predecessor.toString());
                if (++cnt != size)
                    print(", ");
            }
        }
        println("");

        LLVMInstruction nowInstruction = block.getInstHead();
        while(nowInstruction != null){
            nowInstruction.accept(this);
            nowInstruction = nowInstruction.getPostInst();
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
