package Semantic.ASTtype.NonArray;

import AST.Function.Function;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMVoidType;
import IR.TypeSystem.LLVMtype;
import Semantic.ASTtype.Type;

import java.util.HashMap;

public class NullType extends NonArrayType {

    public NullType() {
        super("null", new HashMap<String, Type>(), new HashMap<String, Function>());
    }

    @Override
    public LLVMtype convert2LLVM(HashMap<Type, LLVMtype> typeMap) {
        return new LLVMPointerType(new LLVMVoidType());
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstNull();
    }
}
