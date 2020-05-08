package IR;

import AST.Function.Function;
import AST.VariableEntity.VariableEntity;
import IR.Instruction.*;
import IR.LLVMoperand.GlobalVar;
import IR.LLVMoperand.Register;
import IR.TypeSystem.*;
import Semantic.ASTtype.NonArray.*;
import Semantic.ASTtype.Type;
import Semantic.ASTtype.TypeTable;
import Semantic.ExceptionHandle.CompileError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Module {
    private HashMap<String, LLVMfunction> functionMap;
    private HashMap<String, LLVMfunction> builtInFunctionMap;
    private HashMap<Type, LLVMtype> typeMap;    //map ASTtype to LLVMtype
    private HashMap<String, GlobalVar> constStringMap;
    private ArrayList<DefineGlobal> DefineGlobals;

    public Module() {
        functionMap = new HashMap<String, LLVMfunction>();
        builtInFunctionMap = new HashMap<String, LLVMfunction>();
        typeMap = new HashMap<Type, LLVMtype>();
        constStringMap = new HashMap<String, GlobalVar>();
        DefineGlobals = new ArrayList<DefineGlobal>();
    }

    public void initTypeMethod(TypeTable typeTable){
        for(Type astType : typeTable.getTypetable().values()){
            if(astType instanceof ClassType){
                HashMap<String, Function> methods = ((ClassType) astType).getMethods();
                for(HashMap.Entry<String, Function> method : methods.entrySet()){
                    initMethod(((ClassType) astType).getName() + "." + method.getKey(),
                            method.getValue(), (ClassType) astType);
                }
            }
        }
    }

    public void initTypeConstructor(TypeTable typeTable) {
        try{
            for(Type astType : typeTable.getTypetable().values()){
                if(astType instanceof ClassType){
                    Function constructor = astType.getConstructor();
                    if(constructor.getCategory() != Function.Category.defaultConstructor)
                        initMethod(((ClassType) astType).getName() + "." + ((ClassType) astType).getName(),
                            constructor, (ClassType) astType);
                }
            }
        } catch (CompileError compileError) {
            System.out.println("Unpossible Error in IR build");
        }
    }

    public void initTypeMap(TypeTable typeTable){
        for(Type astType : typeTable.getTypetable().values()){
            if(astType instanceof IntType)
                typeMap.put(astType, new LLVMIntType(LLVMIntType.BitWidth.int32));
            else if(astType instanceof BoolType)
                typeMap.put(astType, new LLVMIntType(LLVMIntType.BitWidth.int1));
            else if(astType instanceof VoidType)
                typeMap.put(astType, new LLVMVoidType());
            else if(astType instanceof NullType)        //maybe deleted
                typeMap.put(astType, new LLVMNullType());
            else if(astType instanceof StringType)
                typeMap.put(astType, new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)));
            else{
                assert astType instanceof ClassType;
                typeMap.put(astType, new LLVMStructType(((ClassType) astType).getName(),
                        new ArrayList<LLVMtype>(), new HashMap<String, Integer>()));
            }
        }
        // init member
        for(Type astType : typeTable.getTypetable().values()){
            if(astType instanceof ClassType){
                LLVMStructType llvmStructType = (LLVMStructType) typeMap.get(astType);
                int index = 0;
                for(Type memberType : ((ClassType) astType).getVarMembers().values()){
                    LLVMtype LLVMMemberType = memberType.convert2LLVM(typeMap);
                    llvmStructType.getMembers().add(LLVMMemberType);
                    llvmStructType.getMemberIndexMap().put(memberType.toString(), index);
                    index++;
                }
            }
        }


    }

    public void initNormalFunction(String functionName, Function function){
        ArrayList<Register> paras = new ArrayList<Register>();
        for(VariableEntity para : function.getParas()){
            LLVMtype llvmParaType = para.getType().convert2LLVM(typeMap);
            paras.add(new Register(llvmParaType, para.getId()));
        }
        LLVMtype llvmReturnType = function.getReturnType().convert2LLVM(typeMap);
        LLVMfunction llvMfunction = new LLVMfunction(functionName, paras, llvmReturnType);
        functionMap.put(functionName, llvMfunction);
        //initBlock
        Block initBlock = new Block("initBlock", llvMfunction);
        Block returnBlock = new Block("returnBlock", llvMfunction);
        llvMfunction.setInitBlock(initBlock);
        llvMfunction.setReturnBlock(returnBlock);
        llvMfunction.registerBlock(initBlock.getName(), initBlock);
        llvMfunction.registerBlock(returnBlock.getName(), returnBlock);
        if(!(llvmReturnType instanceof LLVMVoidType)){
            Register returnAddr = new Register(new LLVMPointerType(llvmReturnType), "return$addr");
            llvMfunction.setReturnAddr(returnAddr);
            llvMfunction.registerVar(returnAddr.getRegisterId(), returnAddr);
            initBlock.addInstruction(new AllocInst(initBlock, returnAddr, llvmReturnType));
            initBlock.addInstruction(new StoreInst(initBlock, function.getReturnType().getDefaultValue(), returnAddr));

            Register returnLoad = new Register(llvmReturnType, "return");
            llvMfunction.registerVar(returnLoad.getRegisterId(), returnLoad);
            returnBlock.addInstruction(new LoadInst(returnBlock, returnAddr, returnLoad));
            returnBlock.addInstruction(new ReturnInst(returnBlock, llvmReturnType, returnLoad));
        }else{
            returnBlock.addInstruction(new ReturnInst(returnBlock, new LLVMVoidType(), null));
        }
        for(int i = 0; i < paras.size(); i++){
            Register para = paras.get(i);
            LLVMtype paraAddrType = para.getLlvMtype();
            Register allocAddr = new Register(new LLVMPointerType(paraAddrType),
                    para.getRegisterId() + "$address");
            llvMfunction.registerVar(allocAddr.getRegisterId(), allocAddr);
            initBlock.addInstruction(new AllocInst(initBlock, allocAddr, paraAddrType));
            initBlock.addInstruction(new StoreInst(initBlock, para, allocAddr));
            function.getParas().get(i).setAllocAddr(allocAddr);
        }
    }

    //can used for both method and constructor
    public void initMethod(String functionName, Function function, ClassType classType){
        ArrayList<Register> paras = new ArrayList<Register>();
        Register thisRegiser = new Register(new LLVMPointerType(classType.convert2LLVM(typeMap)), "this");
        paras.add(thisRegiser);                             //differ from normal function
        for(VariableEntity para : function.getParas()){
            LLVMtype llvmParaType = para.getType().convert2LLVM(typeMap);
            paras.add(new Register(llvmParaType, para.getId()));
        }
        LLVMtype llvmReturnType = function.getReturnType().convert2LLVM(typeMap);
        LLVMfunction llvMfunction = new LLVMfunction(functionName, paras, llvmReturnType);
        functionMap.put(functionName, llvMfunction);
        //initBlock
        Block initBlock = new Block("initBlock", llvMfunction);
        Block returnBlock = new Block("returnBlock", llvMfunction);
        llvMfunction.setInitBlock(initBlock);
        llvMfunction.setReturnBlock(returnBlock);
        llvMfunction.registerBlock(initBlock.getName(), initBlock);
        llvMfunction.registerBlock(returnBlock.getName(), returnBlock);
        if(!(llvmReturnType instanceof LLVMVoidType)){
            Register returnAddr = new Register(new LLVMPointerType(llvmReturnType), "return$addr");
            llvMfunction.setReturnAddr(returnAddr);
            llvMfunction.registerVar(returnAddr.getRegisterId(), returnAddr);
            initBlock.addInstruction(new AllocInst(initBlock, returnAddr, llvmReturnType));
            initBlock.addInstruction(new StoreInst(initBlock, function.getReturnType().getDefaultValue(), returnAddr));

            Register returnLoad = new Register(llvmReturnType, "return");
            llvMfunction.registerVar(returnLoad.getRegisterId(), returnLoad);
            returnBlock.addInstruction(new LoadInst(returnBlock, returnAddr, returnLoad));
            returnBlock.addInstruction(new ReturnInst(returnBlock, llvmReturnType, returnLoad));
        }else{
            returnBlock.addInstruction(new ReturnInst(returnBlock, new LLVMVoidType(), null));
        }
        //differ from normal function
        Register thisAddr = new Register(new LLVMPointerType(thisRegiser.getLlvMtype()), "this$address");
        llvMfunction.setThisAddr(thisAddr);
        llvMfunction.registerVar(thisAddr.getRegisterId(), thisAddr);
        initBlock.addInstruction(new AllocInst(initBlock, thisAddr, thisRegiser.getLlvMtype()));
        initBlock.addInstruction(new StoreInst(initBlock, thisRegiser, thisAddr));
        for(int i = 0; i < paras.size(); i++){
            Register para = paras.get(i+1);                                 //differ from normal funtion
            LLVMtype paraAddrType = para.getLlvMtype();
            Register allocAddr = new Register(new LLVMPointerType(paraAddrType),
                    para.getRegisterId() + "$address");
            llvMfunction.registerVar(allocAddr.getRegisterId(), allocAddr);
            initBlock.addInstruction(new AllocInst(initBlock, allocAddr, paraAddrType));
            initBlock.addInstruction(new StoreInst(initBlock, para, allocAddr));
            function.getParas().get(i).setAllocAddr(allocAddr);
        }
    }

    public void initBuiltInFunction(TypeTable typeTable){
        // Add external functions.
        LLVMtype returnType;
        ArrayList<Register> parameters;
        LLVMfunction function;

        // void print(string str);
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)), "str"));
        function = new LLVMfunction("print", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);

        // void println(string str);
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)), "str"));
        function = new LLVMfunction("println", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);

        // void printInt(int n);
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "n"));
        function = new LLVMfunction("printInt", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);

        // void printlnInt(int n);
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "n"));
        function = new LLVMfunction("printlnInt", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);

        // string getString();
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        function = new LLVMfunction("getString", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);

        // int getInt();
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        function = new LLVMfunction("getInt", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);

        // string toString(int i);
        returnType = new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8));
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "i"));
        function = new LLVMfunction("toString", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);

        // byte* malloc(int size);
        returnType = new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8));
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "size"));
        function = new LLVMfunction("malloc", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);

        // string string.concatenate(string str1, string str2);

        // bool string.equal(string str1, string str2);


        // bool string.notEqual(string str1, string str2);


        // bool string.lessThan(string str1, string str2);

        // bool string.greaterThan(string str1, string str2);

        // bool string.lessEqual(string str1, string str2);

        // bool string.greaterEqual(string str1, string str2);


        // int string.length(string str);


        // string string.substring(string str, int left, int right);


        // int string.parseInt(string str);


        // int ord(string str, int pos);

        // int array.size(array arr);

    }

    public HashMap<Type, LLVMtype> getTypeMap() {
        return typeMap;
    }

    public HashMap<String, GlobalVar> getConstStringMap() {
        return constStringMap;
    }

    public ArrayList<DefineGlobal> getDefineGlobals() {
        return DefineGlobals;
    }

    public HashMap<String, LLVMfunction> getFunctionMap() {
        return functionMap;
    }

    public HashMap<String, LLVMfunction> getBuiltInFunctionMap() {
        return builtInFunctionMap;
    }

    public void setBuiltInFunctionMap(HashMap<String, LLVMfunction> builtInFunctionMap) {
        this.builtInFunctionMap = builtInFunctionMap;
    }

    public void accept(IRVisitor visitor) throws IOException {
        visitor.visit(this);
    }
}
