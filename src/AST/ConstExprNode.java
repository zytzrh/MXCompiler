package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class ConstExprNode extends ExprNode {
    private String constant;


    public ConstExprNode(String text, Location location) {
        super(text, location);
        this.constant = text;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }
}
