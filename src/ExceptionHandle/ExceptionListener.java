package ExceptionHandle;

import AST.Location.Location;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ExceptionListener extends BaseErrorListener {
    private int errorNum;
    private int warningNum;
    private boolean warningOption;

    public ExceptionListener(){
        errorNum = 0;
        warningNum = 0;
        warningOption = true;
    }

    public void errorOut(Location location, String msg){
        errorNum++;
        System.out.println(String.format("Error %d ", errorNum) + location.toString() + msg);
    }

    public void errorOut(CompileError e){
        Location location = e.getLocation();
        String msg = e.getMsg();
        errorOut(location, msg);
    }

    public void setWarningOption(boolean warningOption){
        this.warningOption = warningOption;
    }

    public void warningOut(Location location, String msg){
        if(warningOption){
            warningNum++;
            System.out.println(String.format("Warning %d", warningNum) + location.toString() + msg);
        }
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        errorOut(new Location(line, charPositionInLine), "(Antlr Auto Error) "+msg);
    }

    public int getErrorNum(){
        return this.errorNum;
    }

}
