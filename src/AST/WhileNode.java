package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class WhileNode extends StatementNode {
    private ExprNode cond;
    private StatementNode statement;

    public WhileNode(String text, Location location, ExprNode cond, StatementNode statement) {
        super(text, location);
        this.cond = cond;
        this.statement = statement;
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

    public StatementNode getStatement() {
        return statement;
    }

    public void setStatement(StatementNode statement) {
        this.statement = statement;
    }
}
