package Semantic.ASTtype;

import AST.Function.Function;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMtype;
import Semantic.ExceptionHandle.CompileError;

import java.util.HashMap;

public interface Type {
    public boolean equal(Type other);
    public boolean assignable(Type other);
    public boolean hasVarMember(String memberName);
    public Type getMemberType(String memberName) throws CompileError;
    public void addVarMember(String memberName, Type memberType) throws CompileError;
    public boolean hasMethod(String methodName);
    public Function getMethod(String methodName) throws CompileError;
    public void addMethod(String methodName, Function method) throws CompileError;
    public Function getConstructor() throws CompileError;
    public void setConstructor(Function constructor);
    public LLVMtype convert2LLVM(HashMap<Type, LLVMtype> typeMap);
    public Operand getDefaultValue();
}
