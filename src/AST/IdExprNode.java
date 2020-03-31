package AST;

import AST.NodeProperties.ExprNode;

public class IdExprNode extends ExprNode {
    private String id;


    public IdExprNode(String text, Location location) {
        super(text, location);
        id = text;
    }
}
