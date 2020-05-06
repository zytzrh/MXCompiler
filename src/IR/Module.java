package IR;

import IR.TypeSystem.*;
import Semantic.ASTtype.NonArray.*;
import Semantic.ASTtype.Type;
import Semantic.ASTtype.TypeTable;

import java.util.ArrayList;
import java.util.HashMap;

public class Module {
    private HashMap<String, LLVMfunction> functionMap;
    private HashMap<Type, LLVMtype> typeMap;    //map ASTtype to LLVMtype

    public void initTypeMap(TypeTable typeTable){
        for(Type astType : typeTable.getTypetable().values()){
            if(astType instanceof IntType)
                typeMap.put(astType, new LLVMIntType(LLVMIntType.BitWidth.int32));
            else if(astType instanceof BoolType)
                typeMap.put(astType, new LLVMIntType(LLVMIntType.BitWidth.int1));
            else if(astType instanceof VoidType)
                typeMap.put(astType, new LLVMVoidType());
            else if(astType instanceof NullType)
                typeMap.put(astType, new LLVMNullType());
            else if(astType instanceof StringType)
                typeMap.put(astType, new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)));
            else{
                assert astType instanceof ClassType;
                typeMap.put(astType, new LLVMStructType("class." + ((ClassType) astType).getName(),
                        new ArrayList<LLVMtype>()));
            }
        }
        // init member
        for(Type astType : typeTable.getTypetable().values()){
            if(astType instanceof ClassType){
                LLVMStructType llvmStructType = (LLVMStructType) typeMap.get(astType);
                for(Type memberType : ((ClassType) astType).getVarMembers().values()){
                    LLVMtype LLVMMemberType = typeMap.get(memberType);
                    llvmStructType.getMembers().add(LLVMMemberType);
                }
            }
        }
    }
}
