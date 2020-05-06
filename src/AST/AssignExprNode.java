package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class AssignExprNode extends ExprNode {
    private ExprNode lhs;
    private ExprNode rhs;

    public AssignExprNode(String text, Location location, ExprNode lhs, ExprNode rhs) {
        super(text, location);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public ExprNode getLhs() {
        return lhs;
    }

    public void setLhs(ExprNode lhs) {
        this.lhs = lhs;
    }

    public ExprNode getRhs() {
        return rhs;
    }

    public void setRhs(ExprNode rhs) {
        this.rhs = rhs;
    }
}
