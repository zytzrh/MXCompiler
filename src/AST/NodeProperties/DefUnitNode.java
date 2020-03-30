package AST.NodeProperties;

import AST.Location;

abstract public class DefUnitNode extends ASTNode{
    public DefUnitNode(String text, Location location) {
        super(text, location);
    }
}
