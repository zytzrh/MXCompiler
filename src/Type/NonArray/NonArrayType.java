package Type.NonArray;


import AST.Function.Function;
import ExceptionHandle.CompileError;
import Type.Type;

import java.util.HashMap;

abstract public class NonArrayType implements Type{
    private String name;
    private HashMap<String, Type> varMembers;
    private HashMap<String, Function> methods;
    private Function constructor;
    public NonArrayType(String name, HashMap<String, Type> varMembers, HashMap<String, Function> methods){
        this.name = name;
        this.varMembers = varMembers;
        this.methods = methods;
        this.constructor = null;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public boolean equal(Type other) {
        if(other instanceof NonArrayType){
            return ((NonArrayType) other).getName().equals(this.name);
        }else{
            return false;
        }
    }

    @Override
    public boolean hasVarMember(String memberName) {
        return varMembers.containsKey(memberName);
    }

    @Override
    public boolean hasMethod(String methodName) {
        return methods.containsKey(methodName);
    }

    @Override
    public Type getMemberType(String memberName) throws CompileError {
        if(!hasVarMember(memberName))
            throw new CompileError(null, "Variable member name not exist");
        return varMembers.get(memberName);
    }

    @Override
    public Function getMethod(String methodName) throws CompileError {
        if(!hasMethod(methodName))
            throw new CompileError(null, "Method name not exist");
        return methods.get(methodName);
    }

    @Override
    public Function getConstructor() throws CompileError {
        if(this.constructor == null)
            throw new CompileError(null, "Invalid construction");
        return constructor;
    }

    public HashMap<String, Function> getMethods() {
        return methods;
    }

    public HashMap<String, Type> getVarMembers() {
        return varMembers;
    }

    @Override
    public void addVarMember(String memberName, Type memberType) throws CompileError {
        if(hasVarMember(memberName))
            throw new CompileError(null, "Variable member name already exist");
        varMembers.put(memberName, memberType);
    }

    @Override
    public void addMethod(String methodName, Function method) throws CompileError {
        if(hasMethod(methodName))
            throw new CompileError(null, "Method name already exist");
        methods.put(methodName, method);
    }


    @Override
    public void setConstructor(Function constructor) {
        this.constructor = constructor;
    }
}
