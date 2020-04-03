package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class AssignExprNode extends ExprNode {
    private ExprNode lhs;
    private ExprNode rhs;

    public AssignExprNode(String text, Location location, ExprNode lhs, ExprNode rhs) {
        super(text, location);
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
