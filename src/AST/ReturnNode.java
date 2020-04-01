package AST;

import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;

public class ReturnNode extends StatementNode {
    ExprNode returnExpr;


    public ReturnNode(String text, Location location, ExprNode returnExpr) {
        super(text, location);
        this.returnExpr = returnExpr;
    }
}
