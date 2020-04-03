package AST.Visit;

import AST.*;
import ExceptionHandle.CompileError;

abstract public class ASTVisitor {
    abstract public void visit(ProgramNode node) throws CompileError;
    abstract public void visit(ArrayTypeNode node) throws CompileError;
    abstract public void visit(NonArrayTypeNode node) throws CompileError;
    abstract public void visit(VarDefNode node) throws CompileError;
    abstract public void visit(VarDefOneNode node);

}
