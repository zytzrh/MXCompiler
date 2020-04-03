package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class SubscriptExprNode extends ExprNode {
    private ExprNode array_name;
    private ExprNode index;

    public SubscriptExprNode(String text, Location location, ExprNode array_name, ExprNode index) {
        super(text, location);
        this.array_name = array_name;
        this.index = index;
    }
}
