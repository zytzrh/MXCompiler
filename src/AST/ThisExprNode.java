package AST;

import AST.NodeProperties.ExprNode;

public class ThisExprNode extends ExprNode {
    public ThisExprNode(String text, Location location) {
        super(text, location);
    }
}