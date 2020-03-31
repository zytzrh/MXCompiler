import AST.*;
import AST.NodeProperties.ASTNode;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.TypeNode;
import ExceptionHandle.ExceptionListener;

import java.util.ArrayList;

public class ASTBuilder extends MXgrammarBaseVisitor<ASTNode>{

    /*for type recognition***********************************/
    ExceptionListener exceptionListener;
    public ASTBuilder(ExceptionListener exceptionListener){
        this.exceptionListener = exceptionListener;
    }

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
        System.out.println(text);
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
        return visit(ctx.newType());
    }

    @Override
    public ASTNode visitNormal_newType(MXgrammarParser.Normal_newTypeContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        //System.out.println(text);
        NonArrayTypeNode baseType = new NonArrayTypeNode(ctx.nonArray().getText(), location);
        return new NewExprNode_nonArray(text, location, baseType);
    }

    @Override
    public ASTNode visitArray_newType(MXgrammarParser.Array_newTypeContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        NonArrayTypeNode arrayBaseType = new NonArrayTypeNode(ctx.nonArray().getText(), location);
        int dim = 0;
        for(var child : ctx.children)
            if(child.getText().equals("["))
                dim++;
        ArrayTypeNode baseType = new ArrayTypeNode(text, location, dim, arrayBaseType);

        ArrayList<ExprNode> lenPerDim = new ArrayList<>();
        for(var expr : ctx.expr())
            lenPerDim.add((ExprNode) visit(expr));

        return new NewExprNode_array(text, location, baseType, lenPerDim);
    }

    @Override
    public ASTNode visitWrong_newType(MXgrammarParser.Wrong_newTypeContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        exceptionListener.errorOut(location, "Wrong New Type:"+ctx.getText());

        return super.visitWrong_newType(ctx); //maybe some error
    }

    @Override
    public ASTNode visitMember_expr(MXgrammarParser.Member_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode expr = (ExprNode) visit(ctx.expr());
        String id = ctx.ID().getText();
        return new MemberExprNode(text, location, expr, id);
    }

    @Override
    public ASTNode visitFuncCall_expr(MXgrammarParser.FuncCall_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode func_name = (ExprNode) visit(ctx.func_name);
        ArrayList<ExprNode> paras;
        if(ctx.exprs() != null){
            FuncExprNode exprs = (FuncExprNode) visit(ctx.exprs());
            paras = exprs.getParas();
        }else{
            paras = new ArrayList<ExprNode>();
        }
        return new FuncExprNode(text, location, func_name, paras);
    }

    @Override
    public ASTNode visitExprs(MXgrammarParser.ExprsContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ArrayList<ExprNode> paras = new ArrayList<ExprNode>();
        for(var expr: ctx.expr()){
            paras.add((ExprNode) visit(expr));
        }
        return new FuncExprNode(text, location, null, paras);
    }

    @Override
    public ASTNode visitSubscript_expr(MXgrammarParser.Subscript_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode array_name = (ExprNode) visit(ctx.array_name);
        ExprNode index = (ExprNode) visit(ctx.index);
        return new SubscriptExprNode(text, location, array_name, index);
    }

    @Override
    public ASTNode visitPrefix_expr(MXgrammarParser.Prefix_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode expr = (ExprNode) visit(ctx.expr());
        String op = ctx.op.getText();
        return new PrefixExprNode(text, location, op, expr);
    }

    @Override
    public ASTNode visitBinary_expr(MXgrammarParser.Binary_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode lhs = (ExprNode) visit(ctx.lhs);
        ExprNode rhs = (ExprNode) visit(ctx.rhs);
        String op = ctx.op.getText();
        return new BinaryExprNode(text, location, lhs, rhs, op);
    }

    @Override
    public ASTNode visitAssign_expr(MXgrammarParser.Assign_exprContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode lhs = (ExprNode) visit(ctx.lhs);
        ExprNode rhs = (ExprNode) visit(ctx.rhs);
        return new AssignExprNode(text, location, lhs, rhs);
    }

    @Override
    public ASTNode visitSub_expr(MXgrammarParser.Sub_exprContext ctx) {
        return visit(ctx.expr());
    }
}
