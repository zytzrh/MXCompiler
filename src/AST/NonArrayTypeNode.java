package AST;

import AST.NodeProperties.TypeNode;

public class NonArrayTypeNode extends TypeNode {
    private String id;

    public NonArrayTypeNode(String text, Location location) {
        super(text, location);
        this.id = text;
    }

    public String getId(){
        return this.id;
    }

//    @Override
//    public boolean equal(TypeNode other) {
//        return this.id.equals(other.getId());
//    }
}
