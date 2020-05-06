package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

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

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public NonArrayTypeNode getBaseType() {
        return baseType;
    }

    public void setBaseType(NonArrayTypeNode baseType) {
        this.baseType = baseType;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public ArrayList<ExprNode> getLenPerDim() {
        return lenPerDim;
    }

    public void setLenPerDim(ArrayList<ExprNode> lenPerDim) {
        this.lenPerDim = lenPerDim;
    }
}
