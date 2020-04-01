package AST;

import AST.NodeProperties.StatementNode;

public class EmptyNode extends StatementNode {

    public EmptyNode(String text, Location location) {
        super(text, location);
    }
}
