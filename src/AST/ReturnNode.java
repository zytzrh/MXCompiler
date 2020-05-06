package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class ReturnNode extends StatementNode {
    ExprNode returnExpr;


    public ReturnNode(String text, Location location, ExprNode returnExpr) {
        super(text, location);
        this.returnExpr = returnExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public ExprNode getReturnExpr() {
        return returnExpr;
    }

    public void setReturnExpr(ExprNode returnExpr) {
        this.returnExpr = returnExpr;
    }
}
