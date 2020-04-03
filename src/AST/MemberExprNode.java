package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class MemberExprNode extends ExprNode {
    private ExprNode expr;
    private String id;

    public MemberExprNode(String text, Location location, ExprNode expr, String id) {
        super(text, location);
        this.expr = expr;
        this.id = id;
    }
}
