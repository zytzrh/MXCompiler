package AST;

import AST.NodeProperties.StatementNode;

public class ContinueNode extends StatementNode {

    public ContinueNode(String text, Location location) {
        super(text, location);
    }
}