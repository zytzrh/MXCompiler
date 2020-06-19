package BackEnd;

import BackEnd.Instruction.*;
import BackEnd.Instruction.BinaryInst.ITypeBinaryInst;
import BackEnd.Instruction.BinaryInst.RTypeBinaryInst;
import BackEnd.Instruction.Branch.BinaryBranchInst;
import BackEnd.Instruction.Branch.UnaryBranchInst;
import BackEnd.Operand.ASMGlobalVar;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class ASMPrinter implements ASMVisitor{
    PrintStream stdout;
    PrintStream newout;
    String indent;
    private int functionCnt;            //gugu changed

    public ASMPrinter(String fileName) throws FileNotFoundException {
        stdout = System.out;
        indent = "    ";
        if(fileName != null)
            newout = new PrintStream(fileName);
        else
            newout = System.out;
    }

    private void print(String string) {
            System.out.print(string);
    }

    private void println(String string) {
            System.out.println(string);
    }

    public void run(ASMModule ASMModule) {
        System.setOut(newout);
        ASMModule.accept(this);
        System.setOut(stdout);
    }

    @Override
    public void visit(ASMModule ASMModule) {
        println(indent + ".text");
        println("");

        functionCnt = 0;
        for (ASMFunction ASMFunction : ASMModule.getFunctionMap().values())
            ASMFunction.accept(this);

        println("");

        println(indent + ".section\t.sdata,\"aw\",@progbits");
        for (ASMGlobalVar gv : ASMModule.getGlobalVariableMap().values())
            gv.accept(this);
    }

    @Override
    public void visit(ASMFunction ASMFunction) {
        print(indent + ".globl" + indent + ASMFunction.getName());
        print(" ".repeat(Integer.max(1, 24 - ASMFunction.getName().length())));
        println("# -- Begin function " + ASMFunction.getName());
        println(indent + ".p2align" + indent + "2");

        print(ASMFunction.getName() + ":" + " ".repeat(Integer.max(1, 31 - ASMFunction.getName().length())));
        println("# @" + ASMFunction.getName());

        ArrayList<ASMBlock> blocks = ASMFunction.getBlocks();
        for (ASMBlock block : blocks)
            block.accept(this);

//        println(".Lfunc_end" + functionCnt + ":");
        println(" ".repeat(40) + "# -- End function");
        println("");

        functionCnt++;
    }

    @Override
    public void visit(ASMBlock block) {
        String name = block.getAsmName();
        println(name + ":" + " ".repeat(40 - 1 - name.length()) + "# " + block.getName());

        ASMInstruction ptr = block.getInstHead();
        while (ptr != null) {
                println(ptr.emitCode());
            ptr = ptr.getNextInst();
        }
    }

    @Override
    public void visit(ASMGlobalVar gv) {
        if (!gv.isString()) {
            println(indent + ".globl" + indent + gv.getName());
            println(indent + ".p2align" + indent + "2");
        }
        println(gv.getName() + ":");
        println(gv.emitCode());
        println("");
    }

    @Override
    public void visit(ASMMoveInst inst) {

    }

    @Override
    public void visit(ASMUnaryInst inst) {

    }

    @Override
    public void visit(ITypeBinaryInst inst) {

    }

    @Override
    public void visit(RTypeBinaryInst inst) {

    }

    @Override
    public void visit(ASMLoadAddressInst inst) {

    }

    @Override
    public void visit(ASMLoadImmediate inst) {

    }

    @Override
    public void visit(ASMLoadUpperImmediate inst) {

    }

    @Override
    public void visit(ASMLoadInst inst) {

    }

    @Override
    public void visit(ASMStoreInst inst) {

    }

    @Override
    public void visit(ASMJumpInst inst) {

    }

    @Override
    public void visit(BinaryBranchInst inst) {

    }

    @Override
    public void visit(UnaryBranchInst inst) {

    }

    @Override
    public void visit(ASMCallInst inst) {

    }

    @Override
    public void visit(ASMReturnInst inst) {

    }
}
