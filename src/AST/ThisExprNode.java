package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class ThisExprNode extends ExprNode {
    public ThisExprNode(String text, Location location) {
        super(text, location);
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }
}
