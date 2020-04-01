package AST.NodeProperties;

import AST.Location;

abstract public class TypeNode extends ASTNode{
    public TypeNode(String text, Location location) {
        super(text, location);
    }

//    abstract public boolean equal(TypeNode other);
}
