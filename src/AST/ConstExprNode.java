package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class ConstExprNode extends ExprNode {
    private String constant;


    public ConstExprNode(String text, Location location) {
        super(text, location);
        this.constant = text;
    }
}
