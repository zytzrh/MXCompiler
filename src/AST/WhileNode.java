package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;

public class WhileNode extends StatementNode {
    private ExprNode cond;
    private StatementNode statement;

    public WhileNode(String text, Location location, ExprNode cond, StatementNode statement) {
        super(text, location);
        this.cond = cond;
        this.statement = statement;
    }
}
