import AST.ProgramNode;
import AST.Visit.ASTBuilder;
import ExceptionHandle.CompileError;
import ExceptionHandle.ExceptionListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import ParserAndLexer.*;

import java.io.*;

public class Main {


    public static void main(String[] args) throws CompileError, IOException {
        ExceptionListener exceptionListener = new ExceptionListener();

        InputStream is = System.in;
        /*for file*******************/
//        is = new FileInputStream("basic-2.mx");
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
    }

}
