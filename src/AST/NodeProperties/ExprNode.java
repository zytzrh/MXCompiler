package AST.NodeProperties;

import AST.Location;
import ExprType.ExprType;

abstract public class ExprNode extends ASTNode{
    private Boolean lvalue;
    private ExprType exprType;
    public ExprNode(String text, Location location) {
        super(text, location);
    }
}
