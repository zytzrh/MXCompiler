package AST.NodeProperties;

import AST.Location;

abstract public class ExprNode extends ASTNode{
    public ExprNode(String text, Location location) {
        super(text, location);
    }
}
