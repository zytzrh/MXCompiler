package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;

import java.util.ArrayList;

public class NewExprNode_array extends ExprNode {
    private NonArrayTypeNode baseType;
    private int dim;
    private ArrayList<ExprNode> lenPerDim;

    public NewExprNode_array(String text, Location location, NonArrayTypeNode baseType, int dim, ArrayList<ExprNode> lenPerDim) {
        super(text, location);
        this.baseType = baseType;
        this.dim = dim;
        this.lenPerDim = lenPerDim;
    }

}
