package AST.Location;
import org.antlr.v4.runtime.Token;

public class Location {
    private int line, column;

    public int getLine(){
        return this.line;
    }
    public int getColumn(){
        return this.column;
    }
    public Location(int line, int column){
        this.line = line;
        this.column = column;
    }
    public static Location getTokenLoc(Token token){
        int line = token.getLine();
        int column = token.getCharPositionInLine();
        return new Location(line, column);
    }

    public String toString(){
        return "Loc->" + "Line:" + this.line + " Column:" + this.column + " ";
    }

    public static Location unknownLocation(){
        return new Location(0,0);
    }
}
