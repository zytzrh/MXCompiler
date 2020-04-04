package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class ForNode extends StatementNode {
    BlockNode for_init;
    ExprNode cond;
    BlockNode for_update;
    StatementNode statement;

    public ForNode(String text, Location location, BlockNode for_init, ExprNode cond, BlockNode for_update, StatementNode statement) {
        super(text, location);
        this.for_init = for_init;
        this.cond = cond;
        this.for_update = for_update;
        this.statement = statement;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public BlockNode getFor_init() {
        return for_init;
    }

    public void setFor_init(BlockNode for_init) {
        this.for_init = for_init;
    }

    public ExprNode getCond() {
        return cond;
    }

    public void setCond(ExprNode cond) {
        this.cond = cond;
    }

    public BlockNode getFor_update() {
        return for_update;
    }

    public void setFor_update(BlockNode for_update) {
        this.for_update = for_update;
    }

    public StatementNode getStatement() {
        return statement;
    }

    public void setStatement(StatementNode statement) {
        this.statement = statement;
    }
}
