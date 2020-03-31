// Generated from D:/Code/Compiler/mynext2/src\MXgrammar.g4 by ANTLR 4.8
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
	 * Visit a parse tree produced by {@link MXgrammarParser#formalParas}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParas(MXgrammarParser.FormalParasContext ctx);
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
	 * Visit a parse tree produced by the {@code block_stat}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_stat(MXgrammarParser.Block_statContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varDef_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef_state(MXgrammarParser.VarDef_stateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code if_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_state(MXgrammarParser.If_stateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code while_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_state(MXgrammarParser.While_stateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_state(MXgrammarParser.For_stateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code return_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_state(MXgrammarParser.Return_stateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code break_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreak_state(MXgrammarParser.Break_stateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continue_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinue_state(MXgrammarParser.Continue_stateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code empty_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmpty_state(MXgrammarParser.Empty_stateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr_state(MXgrammarParser.Expr_stateContext ctx);
	/**
	 * Visit a parse tree produced by {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_init(MXgrammarParser.For_initContext ctx);
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