package Type;

import AST.Function.Function;
import ExceptionHandle.CompileError;

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
}
