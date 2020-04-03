package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class IdExprNode extends ExprNode {
    private String id;


    public IdExprNode(String text, Location location) {
        super(text, location);
        this.id = text;
    }
}
