package AST.Visit;

import AST.*;
import ExceptionHandle.CompileError;

abstract public class ASTVisitor {
    abstract public void visit(ProgramNode node) throws CompileError;
    abstract public void visit(ArrayTypeNode node) throws CompileError;
    abstract public void visit(NonArrayTypeNode node) throws CompileError;
    abstract public void visit(VarDefNode node) throws CompileError;
    abstract public void visit(VarDefOneNode node);
    //for ExprNode
    abstract public void visit(ThisExprNode node) throws CompileError;
    abstract public void visit(ConstExprNode node) throws CompileError;
    abstract public void visit(IdExprNode node) throws CompileError;
    abstract public void visit(PostfixExprNode node) throws CompileError;
    abstract public void visit(NewExprNode_array node) throws CompileError;
    abstract public void visit(NewExprNode_nonArray node) throws CompileError;
    abstract public void visit(MemberExprNode node) throws CompileError;
    abstract public void visit(FuncExprNode node) throws CompileError;
    abstract public void visit(SubscriptExprNode node) throws CompileError;
    abstract public void visit(PrefixExprNode node) throws CompileError;
    abstract public void visit(BinaryExprNode node) throws CompileError;
    abstract public void visit(AssignExprNode node) throws CompileError;

}
