package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class ExprStNode extends StatementNode {
    ExprNode expr;

    public ExprStNode(String text, Location location, ExprNode expr) {
        super(text, location);
        this.expr = expr;
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
}
