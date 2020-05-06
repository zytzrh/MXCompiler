package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class MemberExprNode extends ExprNode {
    private ExprNode expr;
    private String id;

    public MemberExprNode(String text, Location location, ExprNode expr, String id) {
        super(text, location);
        this.expr = expr;
        this.id = id;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public ExprNode getExpr() {
        return expr;
    }

    public void setExpr(ExprNode expr) {
        this.expr = expr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
