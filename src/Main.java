import ExceptionHandle.ExceptionListener;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        ExceptionListener exceptionListener = new ExceptionListener();
        CharStream input;
        try{
            File f = new File("basic-2.mx");
            InputStream is = new FileInputStream(f);
            input = CharStreams.fromStream(is);
        } catch (Exception e) {
            System.out.println("file open failed");
            throw new RuntimeException();
        }
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
        astBuilder.visit(tree);

    }
}
