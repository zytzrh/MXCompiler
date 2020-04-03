package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class PostfixExprNode extends ExprNode{
    private ExprNode expr;
    private String op;  //"++" or "--"


    public PostfixExprNode(String text, Location location, ExprNode expr, String op) {
        super(text, location);
        this.expr = expr;
        this.op = op;
    }
}
