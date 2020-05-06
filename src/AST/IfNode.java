package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class IfNode extends StatementNode {
    private ExprNode cond;
    private StatementNode then_st;
    private StatementNode else_st;

    public IfNode(String text, Location location, ExprNode cond, StatementNode then_st, StatementNode else_st) {
        super(text, location);
        this.cond = cond;
        this.then_st = then_st;
        this.else_st = else_st;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public ExprNode getCond() {
        return cond;
    }

    public void setCond(ExprNode cond) {
        this.cond = cond;
    }

    public StatementNode getThen_st() {
        return then_st;
    }

    public void setThen_st(StatementNode then_st) {
        this.then_st = then_st;
    }

    public StatementNode getElse_st() {
        return else_st;
    }

    public void setElse_st(StatementNode else_st) {
        this.else_st = else_st;
    }
}
