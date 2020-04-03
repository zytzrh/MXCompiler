package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;

public class ExprStNode extends StatementNode {
    ExprNode expr;

    public ExprStNode(String text, Location location, ExprNode expr) {
        super(text, location);
        this.expr = expr;
    }
}
