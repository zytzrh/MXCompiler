package AST.NodeProperties;

import AST.Location;

abstract public class TypeNode extends ASTNode{
    protected String id;

    public TypeNode(String text, Location location) {
        super(text, location);
    }

    abstract public boolean equal(TypeNode other);

    public String getId(){
        return id;
    }
}
