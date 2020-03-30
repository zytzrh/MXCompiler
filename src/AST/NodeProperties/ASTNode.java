package AST.NodeProperties;

import AST.Location;
import AST.Scope;

abstract public class ASTNode{
    private String  text;   // for output
    private Location location;

    private Scope scope;    //modify when semantic

    //Some Method
    public ASTNode(String text, Location location){
        this.location = location;
        this.text = text;
    }

    //About Location
    public Location getLocation(){ return this.location;};

    public void setLocation(Location location){ this.location = location;}
    //

}