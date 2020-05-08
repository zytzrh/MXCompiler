package Semantic.ASTtype.NonArray;

import AST.Function.Function;
import AST.VariableEntity.VariableEntity;
import IR.LLVMoperand.ConstInt;
import IR.LLVMoperand.Operand;
import IR.TypeSystem.LLVMIntType;
import IR.TypeSystem.LLVMtype;
import Semantic.ASTtype.Type;

import java.util.ArrayList;
import java.util.HashMap;

public class IntType extends NonArrayType {

    public IntType() {
        super("int", new HashMap<String, Type>(), new HashMap<String, Function>());
        VariableEntity para = new VariableEntity("InitValue", this);
        ArrayList<VariableEntity> paras = new ArrayList<VariableEntity>();
        paras.add(para);
        Function constructor = new Function(this, paras, null, Function.Category.defaultConstructor);
        super.setConstructor(constructor);
    }

    @Override
    public LLVMtype convert2LLVM(HashMap<Type, LLVMtype> typeMap) {
        return typeMap.get(this);
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 0);
    }

}
