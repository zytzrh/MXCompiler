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
	 * Enter a parse tree produced by the {@code block_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlock_st(MXgrammarParser.Block_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code block_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlock_st(MXgrammarParser.Block_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varDef_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVarDef_st(MXgrammarParser.VarDef_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varDef_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVarDef_st(MXgrammarParser.VarDef_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code if_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_st(MXgrammarParser.If_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code if_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_st(MXgrammarParser.If_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code while_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhile_st(MXgrammarParser.While_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code while_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhile_st(MXgrammarParser.While_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterFor_st(MXgrammarParser.For_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitFor_st(MXgrammarParser.For_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code return_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturn_st(MXgrammarParser.Return_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code return_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturn_st(MXgrammarParser.Return_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code break_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreak_st(MXgrammarParser.Break_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code break_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreak_st(MXgrammarParser.Break_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continue_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinue_st(MXgrammarParser.Continue_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continue_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinue_st(MXgrammarParser.Continue_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code empty_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEmpty_st(MXgrammarParser.Empty_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code empty_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEmpty_st(MXgrammarParser.Empty_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expr_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpr_st(MXgrammarParser.Expr_stContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expr_st}
	 * labeled alternative in {@link MXgrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpr_st(MXgrammarParser.Expr_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for_init_withDef}
	 * labeled alternative in {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 */
	void enterFor_init_withDef(MXgrammarParser.For_init_withDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for_init_withDef}
	 * labeled alternative in {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 */
	void exitFor_init_withDef(MXgrammarParser.For_init_withDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for_init_withoutDef}
	 * labeled alternative in {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 */
	void enterFor_init_withoutDef(MXgrammarParser.For_init_withoutDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for_init_withoutDef}
	 * labeled alternative in {@link MXgrammarParser#for_init}.
	 * @param ctx the parse tree
	 */
	void exitFor_init_withoutDef(MXgrammarParser.For_init_withoutDefContext ctx);
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