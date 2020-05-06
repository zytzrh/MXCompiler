package AST;

import AST.Location.Location;
import AST.NodeProperties.TypeNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class NonArrayTypeNode extends TypeNode {
    private String typeName;

    public NonArrayTypeNode(String text, Location location) {
        super(text, location);
        this.typeName = text;
    }

    public String getTypeName(){
        return this.typeName;
    }

//    @Override
//    public boolean equal(TypeNode other) {
//        return this.id.equals(other.getId());
//    }


    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }
}
