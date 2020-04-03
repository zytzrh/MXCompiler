package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

public class NewExprNode_nonArray extends ExprNode {
    private NonArrayTypeNode baseType;

    public NewExprNode_nonArray(String text, Location location, NonArrayTypeNode baseType) {
        super(text, location);
        this.baseType = baseType;
    }
}
