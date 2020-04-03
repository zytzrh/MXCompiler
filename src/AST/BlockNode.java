package AST;


import AST.Location.Location;
import AST.NodeProperties.StatementNode;

import java.util.ArrayList;

public class BlockNode extends StatementNode {
    private ArrayList<StatementNode> statements;

    public BlockNode(String text, Location location, ArrayList<StatementNode>statements) {
        super(text, location);
        this.statements = statements;
    }
}
