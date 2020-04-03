package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class BinaryExprNode extends ExprNode {
    private ExprNode lhs;
    private ExprNode rhs;
    private String op;


    public BinaryExprNode(String text, Location location, ExprNode lhs, ExprNode rhs, String op) {
        super(text, location);
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
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

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
