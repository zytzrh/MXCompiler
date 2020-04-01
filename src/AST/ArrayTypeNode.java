package AST;

import AST.NodeProperties.*;

public class ArrayTypeNode extends TypeNode {
    private int dim;
    private NonArrayTypeNode baseType;

    public ArrayTypeNode(String text, Location location, int dim, NonArrayTypeNode baseType) {
        super(text, location);
        this.dim = dim;
        this.baseType = baseType;
    }

    public NonArrayTypeNode getBaseType(){
        return this.baseType;
    }

    public int getDim(){
        return this.dim;
    }

//    @Override
//    public boolean equal(TypeNode other) {
//        if(other instanceof ArrayTypeNode){
//            if(this.dim == ((ArrayTypeNode) other).getDim() && this.baseType.equal(((ArrayTypeNode) other).baseType)){
//                return true;
//            }
//        }
//        return false;
//    }

}
