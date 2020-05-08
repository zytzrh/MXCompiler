package Semantic.ASTtype;

import AST.Function.Function;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;
import Semantic.ExceptionHandle.CompileError;
import Semantic.ASTtype.NonArray.NonArrayType;
import Semantic.ASTtype.NonArray.NullType;

import java.util.HashMap;

public class ArrayType implements Type {
    private NonArrayType baseNonArrayType;
    private int dim;
    private Function sizeFunction;

    public ArrayType(NonArrayType baseNonArrayType, int dim, Function sizeFunction) {
        this.baseNonArrayType = baseNonArrayType;
        this.dim = dim;
        this.sizeFunction = sizeFunction;
//        Type returnType = new IntType();
//        ArrayList<VariableEntity> paras = new ArrayList<VariableEntity>();
//        BlockNode funcBody = null;
//        this.sizeFunction = new Function()
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstNull();
    }

    @Override
    public LLVMtype convert2LLVM(HashMap<Type, LLVMtype> typeMap) {
        LLVMtype llvMtype = baseNonArrayType.convert2LLVM(typeMap);
        for(int i= 0; i < dim; i++){
            llvMtype = new LLVMPointerType(llvMtype);
        }
        return llvMtype;
    }

    public int getDim(){
        return this.dim;
    }

    @Override
    public boolean equal(Type other) {
        if(other instanceof ArrayType){
            return ((ArrayType) other).baseNonArrayType.equal(this.baseNonArrayType) &&
                    ((ArrayType) other).getDim() == this.dim;
        }else{
            return false;
        }
    }

    @Override
    public boolean assignable(Type other) {
        if(other instanceof NullType)
            return true;
        return equal(other);
    }

    @Override
    public boolean hasVarMember(String memberName) {
        return false;
    }

    @Override
    public Type getMemberType(String memberName) {
        return null;
    }

    @Override
    public void addVarMember(String memberName, Type memberType) {
    }

    @Override
    public boolean hasMethod(String methodName) {
        return methodName.equals("size");
    }

    @Override
    public Function getMethod(String methodName) throws CompileError {
        if(!hasMethod(methodName))
            throw new CompileError(null, "ArrayType has only size method");
        return sizeFunction;
    }

    @Override
    public void addMethod(String methodName, Function method) throws CompileError {
    }

    @Override
    public Function getConstructor() throws CompileError {
        throw new CompileError(null, "ArrayType has no constructor");
    }

    @Override
    public void setConstructor(Function constructor) {

    }

    public NonArrayType getBaseNonArrayType() {
        return baseNonArrayType;
    }

    public void setBaseNonArrayType(NonArrayType baseNonArrayType) {
        this.baseNonArrayType = baseNonArrayType;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public Function getSizeFunction() {
        return sizeFunction;
    }

    public void setSizeFunction(Function sizeFunction) {
        this.sizeFunction = sizeFunction;
    }
}
