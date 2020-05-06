package AST.NodeProperties;

import AST.Location.Location;
import Semantic.ASTtype.Type;

abstract public class TypeNode extends ASTNode{
     Type type;

    public TypeNode(String text, Location location) {
        super(text, location);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    //    abstract public boolean equal(TypeNode other);
}
