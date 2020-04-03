package AST.NodeProperties;

import AST.Location.Location;
import Type.Type;

abstract public class ExprNode extends ASTNode{
    private Boolean lvalue;
    private Type exprType;
    public ExprNode(String text, Location location) {
        super(text, location);
    }

    public Boolean getLvalue() {
        return lvalue;
    }

    public void setLvalue(Boolean lvalue) {
        this.lvalue = lvalue;
    }

    public Type getExprType() {
        return exprType;
    }

    public void setExprType(Type exprType) {
        this.exprType = exprType;
    }
}
