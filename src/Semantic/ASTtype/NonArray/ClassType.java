package Semantic.ASTtype.NonArray;

import AST.Function.Function;
import IR.LLVMoperand.ConstNull;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMPointerType;
import IR.TypeSystem.LLVMtype;
import Semantic.ASTtype.Type;

import java.util.HashMap;

public class ClassType extends NonArrayType {

    public ClassType(String name) {
        super(name, new HashMap<String, Type>(), new HashMap<String, Function>());
    }

    public ClassType(String name, HashMap<String, Type> varMember) {
        super(name, varMember, new HashMap<String, Function>());
    }

    @Override
    public LLVMtype convert2LLVM(HashMap<Type, LLVMtype> typeMap) {
        return new LLVMPointerType(typeMap.get(this));
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstNull();
    }


}
