import AST.ProgramNode;
import AST.Visit.ASTBuilder;
import BackEnd.ASMPrinter;
import BackEnd.Construct.InstructionSelector;
import BackEnd.Construct.RegisterAllocator;
import BackEnd.RISCVModule;
import IR.IRBuilder;
import IR.IRPrinter;
import IR.Module;
import Optimization.*;
import Optimization.Loop.LoopAnalysis;
import Semantic.ExceptionHandle.CompileError;
import Semantic.ExceptionHandle.ExceptionListener;
import Semantic.ParserAndLexer.MXgrammarLexer;
import Semantic.ParserAndLexer.MXgrammarParser;
import Semantic.SemanticCheck;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {


    public static void main(String[] args) throws CompileError, IOException {
        ExceptionListener exceptionListener = new ExceptionListener();

        InputStream is = System.in;
        /*for file*******************/
        is = new FileInputStream("basic-2.mx");
        /*for file******************/
        ANTLRInputStream input = new ANTLRInputStream(is);

        MXgrammarLexer lexer = new MXgrammarLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(exceptionListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MXgrammarParser parser = new MXgrammarParser(tokens);
        parser.removeErrorListeners();;
        parser.addErrorListener(exceptionListener);
        ParseTree tree = parser.program(); // parse; start at prog15
        //System.out.println(tree.toStringTree(parser)); // print tree as text

        ASTBuilder astBuilder = new ASTBuilder(exceptionListener);
        ProgramNode programNode = (ProgramNode) astBuilder.visit(tree);
        if(exceptionListener.getErrorNum() != 0){
            System.out.println("Building AST found error");
            throw new CompileError();
        }
        SemanticCheck semanticCheck = new SemanticCheck(exceptionListener);
        try{
            programNode.accept(semanticCheck);
        } catch (CompileError compileError) {
            exceptionListener.errorOut(compileError);
        }
        if(exceptionListener.getErrorNum() !=0 ){
            System.out.println("Semantic error");
            throw new CompileError();
        }
        IRBuilder irBuilder = new IRBuilder(semanticCheck);
        irBuilder.visit(programNode);
        Module module = irBuilder.getModule();
//        IRPrinter irPrinter = new IRPrinter("out.ll");
//        irPrinter.visit(irBuilder.getModule());

        if(args[args.length-1].equals("codegen")){
            try{
                CFGSimplifier cfgSimplifier = new CFGSimplifier(module);
                cfgSimplifier.run();
                DTreeConstructor dTreeConstructor = new DTreeConstructor(module);
                dTreeConstructor.run();
                SSAConstructor ssaConstructor = new SSAConstructor(module);
                ssaConstructor.run();

                SideEffectChecker sideEffectChecker = new SideEffectChecker(module);
                LoopAnalysis loopAnalysis = new LoopAnalysis(module);
                DeadCodeEliminator deadCodeEliminator = new DeadCodeEliminator(module, sideEffectChecker, loopAnalysis);
                ConstOptim constOptim = new ConstOptim(module);
                InlineExpander inlineExpander = new InlineExpander(module);
                int optimizeCnt = 0;
                while(true){
                    optimizeCnt++;
                    boolean changed = false;
                    dTreeConstructor.run();
                    changed |= constOptim.run();
                    changed |= deadCodeEliminator.run();
                    changed |= cfgSimplifier.run();
                    loopAnalysis.run();
//                    if(optimizeCnt == 1){
//                        IRPrinter irPrinter = new IRPrinter("preInline.txt");
//                        irPrinter.visit(module);
//                    }
                    changed |= inlineExpander.run();
//                    if(optimizeCnt == 1){
//                        IRPrinter irPrinter = new IRPrinter("afterInline.txt");
//                        irPrinter.visit(module);
//                    }
                    changed |= cfgSimplifier.run();
                    if (!changed)
                        break;
                }

                IRPrinter irPrinter = new IRPrinter("IRout.txt");
                irPrinter.visit(module);

                new SSADestructor(module).run();
                InstructionSelector instructionSelector = new InstructionSelector();
                module.accept(instructionSelector);
                RISCVModule ASMRISCVModule = instructionSelector.getASMRISCVModule();

                dTreeConstructor.run();
                loopAnalysis.run();

                new RegisterAllocator(ASMRISCVModule, loopAnalysis).run();
                new ASMPrinter("output.s").run(ASMRISCVModule);
                new ASMPrinter(null).run(ASMRISCVModule);
            }catch (Exception e){

            }
        }

    }

}
