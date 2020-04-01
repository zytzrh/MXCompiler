import AST.*;
import AST.NodeProperties.ASTNode;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;
import AST.NodeProperties.TypeNode;
import ExceptionHandle.ExceptionListener;

import java.util.ArrayList;

public class ASTBuilder extends MXgrammarBaseVisitor<ASTNode>{

    ExceptionListener exceptionListener;
    public ASTBuilder(ExceptionListener exceptionListener){
        this.exceptionListener = exceptionListener;
    }

    /*for type recognition***********************************/

    @Override
    public ASTNode visitArrayType(MXgrammarParser.ArrayTypeContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        TypeNode type = (TypeNode) visit(ctx.type());
        NonArrayTypeNode baseType = null;
        int dim = 0;
        if(type instanceof NonArrayTypeNode){
            baseType = (NonArrayTypeNode) type;
            dim = 1;
        }else if(type instanceof ArrayTypeNode){
            baseType = ((ArrayTypeNode) type).getBaseType();
            dim = ((ArrayTypeNode) type).getDim() + 1;
        }else{
            exceptionListener.errorOut(location, "Unknown Error");
        }

        return new ArrayTypeNode(text, location, dim, baseType);
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
        NonArrayTypeNode baseType = new NonArrayTypeNode(ctx.nonArray().getText(), location);
        int dim = 0;
        for(var child : ctx.children)
            if(child.getText().equals("["))
                dim++;

        ArrayList<ExprNode> lenPerDim = new ArrayList<>();
        for(var expr : ctx.expr())
            lenPerDim.add((ExprNode) visit(expr));

        return new NewExprNode_array(text, location, baseType, dim, lenPerDim);
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

    /*VarDef***************************************************************/

    @Override
    public ASTNode visitVarDefOne(MXgrammarParser.VarDefOneContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        String id = ctx.ID().getText();
        ExprNode initValue;
        if(ctx.expr() != null)
            initValue = (ExprNode) visit(ctx.expr());
        else
            initValue = null;
        return new VarDefOneNode(text, location, id, initValue);    //hasn't decide type yet
    }

    @Override
    public ASTNode visitVarDef(MXgrammarParser.VarDefContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ArrayList<VarDefOneNode> varDefs = new ArrayList<VarDefOneNode>();
        TypeNode type = (TypeNode) visit(ctx.type());
        for(var varDef : ctx.varDefOne()){
            VarDefOneNode varDefOneNode = (VarDefOneNode) visit(varDef);
            varDefOneNode.setType(type);
            varDefs.add(varDefOneNode);
        }
        return new VarDefNode(text, location, varDefs);
    }

    /*for statement***********************************************/

    @Override
    public ASTNode visitBlock_st(MXgrammarParser.Block_stContext ctx) {
        return visit(ctx.block());
    }

    @Override
    public ASTNode visitBlock(MXgrammarParser.BlockContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ArrayList<StatementNode> statements = new ArrayList<>();
        for(var statement : ctx.statement()){
            StatementNode statementNode = (StatementNode) visit(statement);
            statements.add(statementNode);
        }
        return new BlockNode(text, location, statements);
    }

    @Override
    public ASTNode visitVarDef_st(MXgrammarParser.VarDef_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        return new VarDefStNode(text, location, (VarDefNode) visit(ctx.varDef()));
    }

    @Override
    public ASTNode visitIf_st(MXgrammarParser.If_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode cond = (ExprNode) visit(ctx.cond);
        StatementNode then_st = (StatementNode) visit(ctx.then_st);
        StatementNode else_st = null;
        if(ctx.else_st != null){
            else_st = (StatementNode) visit(ctx.else_st);
        }
        return new IfNode(text, location, cond, then_st, else_st);
    }

    @Override
    public ASTNode visitWhile_st(MXgrammarParser.While_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode cond = null;
        if(ctx.cond != null)
            cond = (ExprNode) visit(ctx.cond);
        StatementNode statement = (StatementNode) visit(ctx.statement());
        return new WhileNode(text, location, cond, statement);
    }

    @Override
    public ASTNode visitFor_st(MXgrammarParser.For_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        BlockNode for_init = null;
        if(ctx.for_init() != null)
            for_init = (BlockNode) visit(ctx.for_init());
        ExprNode cond = null;
        if(ctx.cond != null)
            cond = (ExprNode) visit(ctx.cond);
        BlockNode for_update = null;
        if(ctx.for_update() != null)
            for_update = (BlockNode) visit(ctx.for_update());
        StatementNode statement = (StatementNode) visit(ctx.statement());
        return new ForNode(text, location, for_init, cond, for_update, statement);
    }

    @Override
    public ASTNode visitFor_init_withDef(MXgrammarParser.For_init_withDefContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        //package into a StatementNode
        VarDefStNode init_statement = new VarDefStNode(text, location, (VarDefNode) visit(ctx.varDef()));
        //package into a BlockNode
        ArrayList<StatementNode> statements = new ArrayList<>();
        statements.add(init_statement);
        return new BlockNode(text, location, statements);
    }

    @Override
    public ASTNode visitFor_init_withoutDef(MXgrammarParser.For_init_withoutDefContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ArrayList<StatementNode> statements = new ArrayList<>();
        for(var expr : ctx.expr()){
            ExprNode exprNode = (ExprNode) visit(expr);
            //package into a statementNode
            ExprStNode exprStNode = new ExprStNode(exprNode.getText(), exprNode.getLocation(), exprNode);
            //package into a block
            statements.add(exprStNode);
        }
        return new BlockNode(text, location, statements);
    }

    @Override
    public ASTNode visitReturn_st(MXgrammarParser.Return_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode returnExpr = null;
        if(ctx.expr() != null)
            returnExpr = (ExprNode) visit(ctx.expr());
        return new ReturnNode(text, location, returnExpr);
    }

    @Override
    public ASTNode visitBreak_st(MXgrammarParser.Break_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        return new BreakNode(text, location);
    }

    @Override
    public ASTNode visitContinue_st(MXgrammarParser.Continue_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        return new ContinueNode(text, location);
    }

    @Override
    public ASTNode visitEmpty_st(MXgrammarParser.Empty_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        return new EmptyNode(text, location);
    }

    @Override
    public ASTNode visitExpr_st(MXgrammarParser.Expr_stContext ctx) {
        Location location = Location.getTokenLoc(ctx.getStart());
        String text = ctx.getText();
        ExprNode expr = (ExprNode) visit(ctx.expr());
        return new ExprStNode(text, location, expr);
    }


}

