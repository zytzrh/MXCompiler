package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class BinaryExprNode extends ExprNode {
    private ExprNode lhs;
    private ExprNode rhs;
    private String op;


    public BinaryExprNode(String text, Location location, ExprNode lhs, ExprNode rhs, String op) {
        super(text, location);
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }
}
