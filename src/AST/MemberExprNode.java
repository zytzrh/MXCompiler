package AST;

import AST.NodeProperties.ExprNode;
import org.stringtemplate.v4.ST;

public class MemberExprNode extends ExprNode {
    private ExprNode expr;
    private String id;

    public MemberExprNode(String text, Location location, ExprNode expr, String id) {
        super(text, location);
        this.expr = expr;
        this.id = id;
    }
}
