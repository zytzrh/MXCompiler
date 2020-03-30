import AST.ArrayTypeNode;
import AST.Location;
import AST.NodeProperties.ASTNode;
import AST.NodeProperties.TypeNode;
import AST.NonArrayTypeNode;

public class ASTBuilder extends MXgrammarBaseVisitor<ASTNode>{
    //for type recognition
    @Override
    public ASTNode visitArrayType(MXgrammarParser.ArrayTypeContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        TypeNode baseType = (TypeNode) visit(ctx.type());
        int new_dim = 1;
        if(baseType instanceof  ArrayTypeNode){
            new_dim = ((ArrayTypeNode) baseType).getDim() + 1;
        }
        return new ArrayTypeNode(text, location, new_dim, baseType);
    }

    @Override
    public ASTNode visitNonArrayType(MXgrammarParser.NonArrayTypeContext ctx) {
        return visit(ctx.nonArray());
    }

    @Override
    public ASTNode visitNonArray(MXgrammarParser.NonArrayContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        return new NonArrayTypeNode(text, location);
    }


}
