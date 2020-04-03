package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class PostfixExprNode extends ExprNode{
    private ExprNode expr;
    private String op;  //"++" or "--"


    public PostfixExprNode(String text, Location location, ExprNode expr, String op) {
        super(text, location);
        this.expr = expr;
        this.op = op;
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
