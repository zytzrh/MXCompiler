package AST.NodeProperties;

import AST.Location;
import Type.Type;

abstract public class ExprNode extends ASTNode{
    private Boolean lvalue;
    private Type exprType;
    public ExprNode(String text, Location location) {
        super(text, location);
    }

}
