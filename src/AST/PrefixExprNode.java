package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class PrefixExprNode extends ExprNode {
    private ExprNode expr;
    private String op;  //"++" "--" "+" "-" "!" "~"

    public PrefixExprNode(String text, Location location, String op, ExprNode expr) {
        super(text, location);
        this.op = op;
        this.expr = expr;
    }
}
