package ExceptionHandle;

import AST.Location;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ExceptionListener extends BaseErrorListener {
    private int errorNum;
    public ExceptionListener(){
        errorNum = 0;
    }
    public void errorOut(Location location, String msg){
        errorNum++;
        System.out.println(String.format("Error %d ", errorNum) + location.toString() + msg);
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        errorNum++;
        errorOut(new Location(line, charPositionInLine), "(Antlr Auto)"+msg);
    }

    public int getErrorNum(){
        return this.errorNum;
    }
}
