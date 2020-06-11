package BackEnd.Construct;

import BackEnd.ASMBlock;
import BackEnd.ASMVisitor;
import BackEnd.Instruction.*;
import BackEnd.Instruction.BinaryInst.ITypeBinary;
import BackEnd.Instruction.BinaryInst.RTypeBinary;
import BackEnd.Instruction.Branch.BinaryBranch;
import BackEnd.Instruction.Branch.UnaryBranch;
import BackEnd.Operand.ASMGlobalVar;
import BackEnd.RISCVFunction;
import BackEnd.RISCVModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CodeEmitter implements ASMVisitor {
    private File outputFile;
    private OutputStream os;
    private PrintWriter writer;
    private String indent;

    private int functionCnt;
    private boolean printRealASM;

    public CodeEmitter(String filename, boolean printRealASM) {
        this.printRealASM = printRealASM;
        if (filename != null) {
            try {
                outputFile = new File(filename);
                assert outputFile.exists() || outputFile.createNewFile();
                os = new FileOutputStream(filename);
                writer = new PrintWriter(os);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            os = null;
            writer = null;
        }

        indent = "\t";
    }

    public void run(RISCVModule RISCVModule) {
        RISCVModule.accept(this);

        if (os != null) {
            try {
                writer.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private void print(String string) {
        if (printRealASM)
            System.out.print(string);
        if (os != null)
            writer.print(string);
    }

    private void println(String string) {
        if (printRealASM)
            System.out.println(string);
        if (os != null)
            writer.println(string);
    }

    @Override
    public void visit(RISCVModule RISCVModule) {
        println(indent + ".text");
        println("");

        functionCnt = 0;
        for (BackEnd.RISCVFunction RISCVFunction : RISCVModule.getFunctionMap().values())
            RISCVFunction.accept(this);

        println("");

        println(indent + ".section\t.sdata,\"aw\",@progbits");
        for (ASMGlobalVar gv : RISCVModule.getGlobalVariableMap().values())
            gv.accept(this);
    }

    @Override
    public void visit(RISCVFunction RISCVFunction) {
        print(indent + ".globl" + indent + RISCVFunction.getName());
        print(" ".repeat(Integer.max(1, 24 - RISCVFunction.getName().length())));
        println("# -- Begin function " + RISCVFunction.getName());
        println(indent + ".p2align" + indent + "2");

        print(RISCVFunction.getName() + ":" + " ".repeat(Integer.max(1, 31 - RISCVFunction.getName().length())));
        println("# @" + RISCVFunction.getName());

        ArrayList<ASMBlock> blocks = RISCVFunction.getBlocks();
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
            if (printRealASM)
                println(ptr.emitCode());
            else
                println(indent + ptr.toString());
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
    public void visit(ITypeBinary inst) {

    }

    @Override
    public void visit(RTypeBinary inst) {

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
    public void visit(BinaryBranch inst) {

    }

    @Override
    public void visit(UnaryBranch inst) {

    }

    @Override
    public void visit(ASMCallInst inst) {

    }

    @Override
    public void visit(ASMReturnInst inst) {

    }
}

