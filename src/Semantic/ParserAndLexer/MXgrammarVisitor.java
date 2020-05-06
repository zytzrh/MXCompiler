// Generated from D:/Code/Compiler/mynext2/src\MXgrammar.g4 by ANTLR 4.8
package Semantic.ParserAndLexer;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MXgrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MXgrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MXgrammarParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#defUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefUnit(MXgrammarParser.DefUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#classDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDef(MXgrammarParser.ClassDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#funcDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDef(MXgrammarParser.FuncDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef(MXgrammarParser.VarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#constructDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructDef(MXgrammarParser.ConstructDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MXgrammarParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(MXgrammarParser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MXgrammarParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonArrayType(MXgrammarParser.NonArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#nonArray}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonArray(MXgrammarParser.NonArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#formalPara}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalPara(MXgrammarParser.FormalParaContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#varDefOne}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDefOne(MXgrammarParser.VarDefOneContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MXgrammarParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code block_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_st(MXgrammarParser.Block_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varDef_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef_st(MXgrammarParser.VarDef_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code if_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_st(MXgrammarParser.If_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code while_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_st(MXgrammarParser.While_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_st(MXgrammarParser.For_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code return_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_st(MXgrammarParser.Return_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code break_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreak_st(MXgrammarParser.Break_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continue_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinue_st(MXgrammarParser.Continue_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code empty_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmpty_st(MXgrammarParser.Empty_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_st(MXgrammarParser.Expr_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for_init_withDef}
	 * labeled alternative in {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_init_withDef(MXgrammarParser.For_init_withDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for_init_withoutDef}
	 * labeled alternative in {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_init_withoutDef(MXgrammarParser.For_init_withoutDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#for_update}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_update(MXgrammarParser.For_updateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCall_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCall_expr(MXgrammarParser.FuncCall_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sub_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSub_expr(MXgrammarParser.Sub_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code member_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMember_expr(MXgrammarParser.Member_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binary_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_expr(MXgrammarParser.Binary_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code this_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThis_expr(MXgrammarParser.This_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code id_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId_expr(MXgrammarParser.Id_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code new_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNew_expr(MXgrammarParser.New_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assign_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign_expr(MXgrammarParser.Assign_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code postfix_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfix_expr(MXgrammarParser.Postfix_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefix_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefix_expr(MXgrammarParser.Prefix_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code subscript_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscript_expr(MXgrammarParser.Subscript_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code const_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst_expr(MXgrammarParser.Const_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#exprs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprs(MXgrammarParser.ExprsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code wrong_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWrong_newType(MXgrammarParser.Wrong_newTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code array_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_newType(MXgrammarParser.Array_newTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code normal_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormal_newType(MXgrammarParser.Normal_newTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(MXgrammarParser.ConstantContext ctx);
}