package Semantic.ASTtype.NonArray;

import AST.Function.Function;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMtype;
import Semantic.ASTtype.Type;

import java.util.HashMap;

public class VoidType extends NonArrayType {

    public VoidType() {
        super("void", new HashMap<String, Type>(), new HashMap<String, Function>());
    }

    @Override
    public LLVMtype convert2LLVM(HashMap<Type, LLVMtype> typeMap) {
        assert false;
        return null;
    }

    @Override
    public Operand getDefaultValue() {
        assert false;
        return null;
    }
}
