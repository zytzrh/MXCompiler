import AST.*;
import AST.NodeProperties.ASTNode;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.TypeNode;

public class ASTBuilder extends MXgrammarBaseVisitor<ASTNode>{

    /*for type recognition***********************************/


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

    /*for expr************************************************/

    @Override
    public ASTNode visitConst_expr(MXgrammarParser.Const_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        //System.out.println(text);
        return new ThisExprNode(text, location);
    }

    @Override
    public ASTNode visitThis_expr(MXgrammarParser.This_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        return new ThisExprNode(text, location);
    }

    @Override
    public ASTNode visitId_expr(MXgrammarParser.Id_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        return new ThisExprNode(text, location);
    }

    @Override
    public ASTNode visitPostfix_expr(MXgrammarParser.Postfix_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode expr = (ExprNode) visit(ctx.expr());
        String op = ctx.op.getText();
        return new PostfixExprNode(text, location, expr, op);
    }

    @Override
    public ASTNode visitNew_expr(MXgrammarParser.New_exprContext ctx) {
        return super.visitNew_expr(ctx);
    }
}
