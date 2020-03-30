package AST;

import AST.NodeProperties.TypeNode;

public class NonArrayTypeNode extends TypeNode {

    public NonArrayTypeNode(String text, Location location) {
        super(text, location);
        this.id = text;
    }

    @Override
    public boolean equal(TypeNode other) {
        return this.id.equals(other.getId());
    }
}
