package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;

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
}
