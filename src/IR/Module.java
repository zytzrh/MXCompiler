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
                    initMethod(((ClassType) astType).getName() + "$" + method.getKey(),
                            method.getValue(), (ClassType) astType, false);
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
                        initMethod(((ClassType) astType).getName() + "$" + ((ClassType) astType).getName(),
                            constructor, (ClassType) astType, true);
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
            else if(astType instanceof VoidType){
                typeMap.put(astType, new LLVMVoidType());
            }
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
                HashMap<String, Type> varMembers = ((ClassType) astType).getVarMembers();
                for(String memberName : varMembers.keySet()){
                    Type memberType = varMembers.get(memberName);
                    LLVMtype LLVMMemberType = memberType.convert2LLVM(typeMap);
                    llvmStructType.getMembers().add(LLVMMemberType);

                    llvmStructType.getMemberIndexMap().put(memberName, index);
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
//        llvMfunction.registerBlock(initBlock.getName(), initBlock);
//        llvMfunction.registerBlock(returnBlock.getName(), returnBlock);
        if(!(llvmReturnType instanceof LLVMVoidType)){
            Register returnAddr = new Register(new LLVMPointerType(llvmReturnType), "return$address");
            llvMfunction.setReturnAddr(returnAddr);
            llvMfunction.registerVar(returnAddr.getName(), returnAddr);
            initBlock.addInst(new AllocInst(initBlock, returnAddr, llvmReturnType));
            initBlock.addInst(new StoreInst(initBlock, function.getReturnType().getDefaultValue(), returnAddr));

            Register returnLoad = new Register(llvmReturnType, "return");
            llvMfunction.registerVar(returnLoad.getName(), returnLoad);
            returnBlock.addInst(new LoadInst(returnBlock, returnAddr, returnLoad));
            returnBlock.addInst(new ReturnInst(returnBlock, llvmReturnType, returnLoad));
        }else{
            returnBlock.addInst(new ReturnInst(returnBlock, new LLVMVoidType(), null));
        }
        for(int i = 0; i < paras.size(); i++){
            Register para = paras.get(i);
            para.setParameter(true);            //gugu changed:
            LLVMtype paraAddrType = para.getLlvMtype();
            Register allocAddr = new Register(new LLVMPointerType(paraAddrType),
                    para.getName() + "$address");
            llvMfunction.registerVar(allocAddr.getName(), allocAddr);
            initBlock.addInst(new AllocInst(initBlock, allocAddr, paraAddrType));
            initBlock.addInst(new StoreInst(initBlock, para, allocAddr));
            function.getParas().get(i).setAllocAddr(allocAddr);         //
        }
    }

    //can used for both method and constructor
    public void initMethod(String functionName, Function function, ClassType classType, boolean inConstructor){
        ArrayList<Register> paras = new ArrayList<Register>();
        Register thisRegiser = new Register(classType.convert2LLVM(typeMap), "this");
        thisRegiser.setParameter(true);             //gugu changed
        paras.add(thisRegiser);                             //differ from normal function
        for(VariableEntity para : function.getParas()){
            LLVMtype llvmParaType = para.getType().convert2LLVM(typeMap);
            paras.add(new Register(llvmParaType, para.getId()));
        }
        LLVMtype llvmReturnType;
        if(inConstructor)
            llvmReturnType = new LLVMVoidType();
        else
            llvmReturnType = function.getReturnType().convert2LLVM(typeMap);
        LLVMfunction llvMfunction = new LLVMfunction(functionName, paras, llvmReturnType);
        llvMfunction.registerVar(thisRegiser.getName(), thisRegiser);
        functionMap.put(functionName, llvMfunction);
        //initBlock
        Block initBlock = new Block("initBlock", llvMfunction);
        Block returnBlock = new Block("returnBlock", llvMfunction);
        llvMfunction.setInitBlock(initBlock);
        llvMfunction.setReturnBlock(returnBlock);
//        llvMfunction.registerBlock(initBlock.getName(), initBlock);
//        llvMfunction.registerBlock(returnBlock.getName(), returnBlock);
        if(!(llvmReturnType instanceof LLVMVoidType)){
            Register returnAddr = new Register(new LLVMPointerType(llvmReturnType), "return$address");
            llvMfunction.setReturnAddr(returnAddr);
            llvMfunction.registerVar(returnAddr.getName(), returnAddr);
            initBlock.addInst(new AllocInst(initBlock, returnAddr, llvmReturnType));
            initBlock.addInst(new StoreInst(initBlock, function.getReturnType().getDefaultValue(), returnAddr));

            Register returnLoad = new Register(llvmReturnType, "return");
            llvMfunction.registerVar(returnLoad.getName(), returnLoad);
            returnBlock.addInst(new LoadInst(returnBlock, returnAddr, returnLoad));
            returnBlock.addInst(new ReturnInst(returnBlock, llvmReturnType, returnLoad));
        }else{
            returnBlock.addInst(new ReturnInst(returnBlock, new LLVMVoidType(), null));
        }
        //differ from normal function
        Register thisAddr = new Register(new LLVMPointerType(thisRegiser.getLlvMtype()), "this$address");
        llvMfunction.setThisAddr(thisAddr);
        llvMfunction.registerVar(thisAddr.getName(), thisAddr);
        initBlock.addInst(new AllocInst(initBlock, thisAddr, thisRegiser.getLlvMtype()));
        initBlock.addInst(new StoreInst(initBlock, thisRegiser, thisAddr));
        for(int i = 1; i < paras.size(); i++){
            Register para = paras.get(i);                                 //differ from normal funtion
            para.setParameter(true);                //gugu changed
            LLVMtype paraAddrType = para.getLlvMtype();
            Register allocAddr = new Register(new LLVMPointerType(paraAddrType),
                    para.getName() + "$address");
            llvMfunction.registerVar(allocAddr.getName(), allocAddr);
            initBlock.addInst(new AllocInst(initBlock, allocAddr, paraAddrType));
            initBlock.addInst(new StoreInst(initBlock, para, allocAddr));
            function.getParas().get(i-1).setAllocAddr(allocAddr);           //
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
        function.setSideEffect(true);
        function.setBuiltIn(true);


        // void println(string str);
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)), "str"));
        function = new LLVMfunction("println", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(true);
        function.setBuiltIn(true);

        // void printInt(int n);
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "n"));
        function = new LLVMfunction("printInt", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(true);
        function.setBuiltIn(true);

        // void printlnInt(int n);
        returnType = new LLVMVoidType();
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "n"));
        function = new LLVMfunction("printlnInt", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(true);
        function.setBuiltIn(true);

        // string getString();
        returnType = new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8));
        parameters = new ArrayList<Register>();
        function = new LLVMfunction("getString", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(true);
        function.setBuiltIn(true);

        // int getInt();
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int32);
        parameters = new ArrayList<Register>();
        function = new LLVMfunction("getInt", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(true);
        function.setBuiltIn(true);

        // string toString(int i);
        returnType = new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8));
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "i"));
        function = new LLVMfunction("toString", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // byte* malloc(int size);
        returnType = new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8));
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "size"));
        function = new LLVMfunction("malloc", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // string string.concatenate(string str1, string str2);
        returnType = new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8));
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str1"));
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str2"));
        function = new LLVMfunction("__string_concatenate", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // bool string.equal(string str1, string str2);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int1);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str1"));
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str2"));
        function = new LLVMfunction("__string_equal", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // bool string.notEqual(string str1, string str2);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int1);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str1"));
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str2"));
        function = new LLVMfunction("__string_notEqual", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // bool string.lessThan(string str1, string str2);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int1);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str1"));
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str2"));
        function = new LLVMfunction("__string_lessThan", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // bool string.greaterThan(string str1, string str2);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int1);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str1"));
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str2"));
        function = new LLVMfunction("__string_greaterThan", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // bool string.lessEqual(string str1, string str2);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int1);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str1"));
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str2"));
        function = new LLVMfunction("__string_lessEqual", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // bool string.greaterEqual(string str1, string str2);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int1);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str1"));
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str2"));
        function = new LLVMfunction("__string_greaterEqual", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);


        // int string.length(string str);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int32);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str"));
        function = new LLVMfunction("__string_length", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // string string.substring(string str, int left, int right);
        returnType = new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8));
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str"));
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "left"));
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "right"));
        function = new LLVMfunction("__string_substring", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // int string.parseInt(string str);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int32);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str"));
        function = new LLVMfunction("__string_parseInt", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // int ord(string str, int pos);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int32);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"str"));
        parameters.add(new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "pos"));
        function = new LLVMfunction("__string_ord", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);

        // int array.size(array arr);
        returnType = new LLVMIntType(LLVMIntType.BitWidth.int32);
        parameters = new ArrayList<Register>();
        parameters.add(new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)), "arr"));
        function = new LLVMfunction("__array_size", parameters, returnType);
        this.builtInFunctionMap.put(function.getFunctionName(), function);
        function.setSideEffect(false);
        function.setBuiltIn(true);


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

    public boolean checkNormalFunctional(){
        for (LLVMfunction function : functionMap.values()) {
            if (!function.isFunctional())
                return false;
        }
        return true;
    }

    public boolean checkTrivalCall(){
        int trivalCount = 0;
        for (LLVMfunction function : functionMap.values()){
            for (Block block : function.getBlocks()) {
                LLVMInstruction currentInst = block.getInstHead();
                while (currentInst != null) {
                    if (currentInst instanceof CallInst) {
                        CallInst callInst = (CallInst) currentInst;
                        LLVMfunction callee = callInst.getLlvMfunction();
                        if(callee == builtInFunctionMap.get("toString")){
                            trivalCount++;
                            if(trivalCount > 300)
                                return false;
                        }
                    }
                    currentInst = currentInst.getPostInst();
                }
            }

        }
        return true;
    }
}
