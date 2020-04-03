package AST.NodeProperties;

import AST.Location.Location;
import AST.Scope.Scope;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;


abstract public class ASTNode{
    private String  text;   // for output
    private Location location;

    private Scope scope;    //modify when semantic

    //Some Method
    public ASTNode(String text, Location location){
        this.location = location;
        this.text = text;
    }

    public Location getLocation(){ return this.location;};

    public void setLocation(Location location){ this.location = location;}

    public String getText() {
        return this.text;
    }

    public void accept(ASTVisitor visitor) throws CompileError {
        throw new CompileError(null, "no possible error");
    }
}