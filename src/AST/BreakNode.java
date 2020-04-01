package AST;

import AST.NodeProperties.StatementNode;

public class BreakNode extends StatementNode {

    public BreakNode(String text, Location location) {
        super(text, location);
    }
}
