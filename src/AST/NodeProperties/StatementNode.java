package AST.NodeProperties;

import AST.Location.Location;

public abstract class StatementNode extends ASTNode {

    public StatementNode(String text, Location location) {
        super(text, location);
    }
}
