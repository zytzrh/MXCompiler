package AST;

import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;

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
}
