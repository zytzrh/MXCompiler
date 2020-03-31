package AST;

import AST.NodeProperties.ExprNode;
import AST.NodeProperties.TypeNode;

public class NewExprNode_nonArray extends ExprNode {
    private NonArrayTypeNode baseType;

    public NewExprNode_nonArray(String text, Location location, NonArrayTypeNode baseType) {
        super(text, location);
        this.baseType = baseType;
    }
}
