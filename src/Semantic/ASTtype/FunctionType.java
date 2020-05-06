package Semantic.ASTtype;

import AST.Function.Function;
import Semantic.ExceptionHandle.CompileError;

public class FunctionType implements Type {
    Function function;

    @Override
    public boolean equal(Type other) {
        return other instanceof FunctionType;
    }

    @Override
    public boolean assignable(Type other) {
        return false;
    }

    @Override
    public boolean hasVarMember(String memberName) {
        return false;
    }

    @Override
    public boolean hasMethod(String methodName) {
        return false;
    }

    @Override
    public Type getMemberType(String memberName) throws CompileError {
        throw new CompileError(null, "Function type has no variable member");
    }

    @Override
    public Function getMethod(String methodName) throws CompileError {
        throw new CompileError(null, "Function type has no method");
    }

    @Override
    public Function getConstructor() throws CompileError {
        throw new CompileError(null, "Function Type has no constructor");
    }

    @Override
    public void addMethod(String methodName, Function method) throws CompileError {

    }

    @Override
    public void setConstructor(Function constructor) {

    }

    @Override
    public void addVarMember(String memberName, Type memberType) throws CompileError {

    }


    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
