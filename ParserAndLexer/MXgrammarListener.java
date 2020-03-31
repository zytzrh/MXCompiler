// Generated from D:/Code/Compiler/mynext2/src\MXgrammar.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MXgrammarParser}.
 */
public interface MXgrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MXgrammarParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MXgrammarParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#defUnit}.
	 * @param ctx the parse tree
	 */
	void enterDefUnit(MXgrammarParser.DefUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#defUnit}.
	 * @param ctx the parse tree
	 */
	void exitDefUnit(MXgrammarParser.DefUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#classDef}.
	 * @param ctx the parse tree
	 */
	void enterClassDef(MXgrammarParser.ClassDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#classDef}.
	 * @param ctx the parse tree
	 */
	void exitClassDef(MXgrammarParser.ClassDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void enterFuncDef(MXgrammarParser.FuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void exitFuncDef(MXgrammarParser.FuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(MXgrammarParser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(MXgrammarParser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#constructDef}.
	 * @param ctx the parse tree
	 */
	void enterConstructDef(MXgrammarParser.ConstructDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#constructDef}.
	 * @param ctx the parse tree
	 */
	void exitConstructDef(MXgrammarParser.ConstructDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MXgrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(MXgrammarParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MXgrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(MXgrammarParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MXgrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayType(MXgrammarParser.NonArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MXgrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayType(MXgrammarParser.NonArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#nonArray}.
	 * @param ctx the parse tree
	 */
	void enterNonArray(MXgrammarParser.NonArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#nonArray}.
	 * @param ctx the parse tree
	 */
	void exitNonArray(MXgrammarParser.NonArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#formalParas}.
	 * @param ctx the parse tree
	 */
	void enterFormalParas(MXgrammarParser.FormalParasContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#formalParas}.
	 * @param ctx the parse tree
	 */
	void exitFormalParas(MXgrammarParser.FormalParasContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#formalPara}.
	 * @param ctx the parse tree
	 */
	void enterFormalPara(MXgrammarParser.FormalParaContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#formalPara}.
	 * @param ctx the parse tree
	 */
	void exitFormalPara(MXgrammarParser.FormalParaContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#varDefOne}.
	 * @param ctx the parse tree
	 */
	void enterVarDefOne(MXgrammarParser.VarDefOneContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#varDefOne}.
	 * @param ctx the parse tree
	 */
	void exitVarDefOne(MXgrammarParser.VarDefOneContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MXgrammarParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MXgrammarParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code block_stat}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlock_stat(MXgrammarParser.Block_statContext ctx);
	/**
	 * Exit a parse tree produced by the {@code block_stat}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlock_stat(MXgrammarParser.Block_statContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varDef_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVarDef_state(MXgrammarParser.VarDef_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varDef_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVarDef_state(MXgrammarParser.VarDef_stateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code if_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_state(MXgrammarParser.If_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code if_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_state(MXgrammarParser.If_stateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code while_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhile_state(MXgrammarParser.While_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code while_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhile_state(MXgrammarParser.While_stateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterFor_state(MXgrammarParser.For_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitFor_state(MXgrammarParser.For_stateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code return_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturn_state(MXgrammarParser.Return_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code return_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturn_state(MXgrammarParser.Return_stateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code break_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreak_state(MXgrammarParser.Break_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code break_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreak_state(MXgrammarParser.Break_stateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continue_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinue_state(MXgrammarParser.Continue_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continue_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinue_state(MXgrammarParser.Continue_stateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code empty_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEmpty_state(MXgrammarParser.Empty_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code empty_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEmpty_state(MXgrammarParser.Empty_stateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpr_state(MXgrammarParser.Expr_stateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_state}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpr_state(MXgrammarParser.Expr_stateContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 */
	void enterFor_init(MXgrammarParser.For_initContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 */
	void exitFor_init(MXgrammarParser.For_initContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#for_update}.
	 * @param ctx the parse tree
	 */
	void enterFor_update(MXgrammarParser.For_updateContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#for_update}.
	 * @param ctx the parse tree
	 */
	void exitFor_update(MXgrammarParser.For_updateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCall_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFuncCall_expr(MXgrammarParser.FuncCall_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCall_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFuncCall_expr(MXgrammarParser.FuncCall_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sub_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSub_expr(MXgrammarParser.Sub_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sub_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSub_expr(MXgrammarParser.Sub_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code member_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMember_expr(MXgrammarParser.Member_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code member_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMember_expr(MXgrammarParser.Member_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binary_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBinary_expr(MXgrammarParser.Binary_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binary_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBinary_expr(MXgrammarParser.Binary_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code this_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterThis_expr(MXgrammarParser.This_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code this_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitThis_expr(MXgrammarParser.This_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterId_expr(MXgrammarParser.Id_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitId_expr(MXgrammarParser.Id_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code new_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNew_expr(MXgrammarParser.New_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code new_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNew_expr(MXgrammarParser.New_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assign_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAssign_expr(MXgrammarParser.Assign_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assign_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAssign_expr(MXgrammarParser.Assign_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code postfix_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPostfix_expr(MXgrammarParser.Postfix_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code postfix_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPostfix_expr(MXgrammarParser.Postfix_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefix_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPrefix_expr(MXgrammarParser.Prefix_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefix_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPrefix_expr(MXgrammarParser.Prefix_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code subscript_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSubscript_expr(MXgrammarParser.Subscript_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code subscript_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSubscript_expr(MXgrammarParser.Subscript_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code const_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterConst_expr(MXgrammarParser.Const_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code const_expr}
	 * labeled alternative in {@link MXgrammarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitConst_expr(MXgrammarParser.Const_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#exprs}.
	 * @param ctx the parse tree
	 */
	void enterExprs(MXgrammarParser.ExprsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#exprs}.
	 * @param ctx the parse tree
	 */
	void exitExprs(MXgrammarParser.ExprsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code wrong_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 */
	void enterWrong_newType(MXgrammarParser.Wrong_newTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code wrong_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 */
	void exitWrong_newType(MXgrammarParser.Wrong_newTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code array_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 */
	void enterArray_newType(MXgrammarParser.Array_newTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code array_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 */
	void exitArray_newType(MXgrammarParser.Array_newTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code normal_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 */
	void enterNormal_newType(MXgrammarParser.Normal_newTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code normal_newType}
	 * labeled alternative in {@link MXgrammarParser#newType}.
	 * @param ctx the parse tree
	 */
	void exitNormal_newType(MXgrammarParser.Normal_newTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MXgrammarParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(MXgrammarParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link MXgrammarParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(MXgrammarParser.ConstantContext ctx);
}