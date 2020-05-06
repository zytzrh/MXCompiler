package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class NewExprNode_nonArray extends ExprNode {
    private NonArrayTypeNode type;

    public NewExprNode_nonArray(String text, Location location, NonArrayTypeNode type) {
        super(text, location);
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public NonArrayTypeNode getType() {
        return type;
    }

    public void setType(NonArrayTypeNode type) {
        this.type = type;
    }
}
