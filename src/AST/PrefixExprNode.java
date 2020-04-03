package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class PrefixExprNode extends ExprNode {
    private ExprNode expr;
    private String op;  //"++" "--" "+" "-" "!" "~"

    public PrefixExprNode(String text, Location location, String op, ExprNode expr) {
        super(text, location);
        this.op = op;
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

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
