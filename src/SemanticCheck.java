import AST.*;
import AST.Function.Function;
import AST.Function.FunctionTable;
import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.TypeNode;
import AST.Scope.GlobalScope;
import AST.Scope.Scope;
import AST.VariableEntity.VariableEntity;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;
import ExceptionHandle.ExceptionListener;
import Type.*;
import Type.NonArray.*;

import java.util.ArrayList;
import java.util.Stack;

public class SemanticCheck extends ASTVisitor {
    private ExceptionListener exceptionListener;
    private TypeTable typeTable;    //<string(typename), type>
    private FunctionTable functionTable;    //<string, function>
    private Stack<Scope> scopeStack;    //save variable entity table <string(variable name), type>

    public SemanticCheck(ExceptionListener exceptionListener){
        this.exceptionListener = exceptionListener;
        typeTable = new TypeTable();
        functionTable = new FunctionTable();
        scopeStack = new Stack<Scope>();
        tableInit();
    }

    private void tableInit(){
        try{
            NonArrayType intType = new IntType();
            NonArrayType boolType = new BoolType();
            NonArrayType stringType = new StringType();
            NonArrayType voidType = new VoidType();
            NonArrayType nullType = new NullType();
            typeTable.put("int", intType);
            typeTable.put("bool", boolType);
            typeTable.put("string", stringType);
            typeTable.put("void", voidType);
            typeTable.put("null", nullType);
            //print
            VariableEntity printPara = new VariableEntity("str", stringType);
            ArrayList<VariableEntity> printParas = new ArrayList<VariableEntity>();
            printParas.add(printPara);
            Function print = new Function(voidType, printParas, null);
            functionTable.putFunc("print", print);
            //println
            VariableEntity printlnPara = new VariableEntity("str", stringType);
            ArrayList<VariableEntity> printlnParas = new ArrayList<VariableEntity>();
            printlnParas.add(printlnPara);
            Function println = new Function(voidType, printlnParas, null);
            functionTable.putFunc("println", println);
            //printInt
            VariableEntity printIntPara = new VariableEntity("n", intType);
            ArrayList<VariableEntity> printIntParas = new ArrayList<VariableEntity>();
            printIntParas.add(printIntPara);
            Function printInt = new Function(voidType, printIntParas, null);
            functionTable.putFunc("printInt", printInt);
            //printlnInt
            VariableEntity printlnIntPara = new VariableEntity("n", intType);
            ArrayList<VariableEntity> printlnIntParas = new ArrayList<VariableEntity>();
            printlnIntParas.add(printlnIntPara);
            Function printlnInt = new Function(voidType, printlnIntParas, null);
            functionTable.putFunc("printlnInt", printlnInt);
            //getString (no paras)
            ArrayList<VariableEntity> getStringParas = new ArrayList<VariableEntity>();
            Function getString = new Function(stringType, getStringParas, null);
            functionTable.putFunc("getString", getString);
            //getInt (no paras)
            ArrayList<VariableEntity> getIntParas = new ArrayList<VariableEntity>();
            Function getInt = new Function(intType, getIntParas, null);
            functionTable.putFunc("getInt", getInt);
            //toString
            VariableEntity toStringPara = new VariableEntity("i", intType);
            ArrayList<VariableEntity> toStringParas = new ArrayList<VariableEntity>();
            toStringParas.add(toStringPara);
            Function toString = new Function(stringType, toStringParas, null);
            //
        }catch(CompileError e){
            e.setLocation(Location.unknownLocation());
            exceptionListener.errorOut(e);
        }
    }

    private void registerClassType(ClassDefNode node){
        try{
            String class_name = node.getClassName();
            NonArrayType class_type = new ClassType("class_name");
            typeTable.put(class_name, class_type);
        }catch (CompileError compileError){
            compileError.setLocation(node.getLocation());
            exceptionListener.errorOut(compileError);
        }
    }

    private Function convertFuntion(FuncDefNode node) throws CompileError {
        //paras
        ArrayList<VariableEntity> convertedParas = new ArrayList<VariableEntity>();
        ArrayList<FormalParaNode> Paras = node.getParas();
        for(FormalParaNode para : Paras){
            TypeNode paraTypeNode = para.getParaType();
            paraTypeNode.accept(this);
            Type paraType = paraTypeNode.getType();
            String paraName = para.getParaName();
            convertedParas.add(new VariableEntity(paraName, paraType));
        }
        //func_body
        BlockNode funcBody = node.getFuncBody();
        //return_type
        TypeNode returnTypeNode = node.getReturnType();
        returnTypeNode.accept(this);
        Type returnType = returnTypeNode.getType();
        //all
        return new Function(returnType, convertedParas, funcBody);
    }

    private Function convertConstructor(ConstructDefNode node) throws CompileError {
        //paras
        ArrayList<VariableEntity> convertedParas = new ArrayList<VariableEntity>();
        ArrayList<FormalParaNode> Paras = node.getParas();
        for(FormalParaNode para : Paras){
            TypeNode paraTypeNode = para.getParaType();
            paraTypeNode.accept(this);
            Type paraType = paraTypeNode.getType();
            String paraName = para.getParaName();
            convertedParas.add(new VariableEntity(paraName, paraType));
        }
        //func_body
        BlockNode funcBody = node.getFuncBody();
        //return_type
        Type returnType = typeTable.get(node.getClassTypeId());
        //all
        return new Function(returnType, convertedParas, funcBody);
    }

    private void registerFunction(FuncDefNode node){
        try{
            String funcName = node.getFuncName();
            Function function = convertFuntion(node);
            functionTable.putFunc(funcName, function);
        }catch (CompileError compileError){
            compileError.setLocation(node.getLocation());
            exceptionListener.errorOut(compileError);
        }
    }

    private void registerMember(ClassDefNode node){
        try{
            String className = node.getClassName();
            //register variable member
            ArrayList<VarDefOneNode> varMembers = node.getVarMembers();
            for(VarDefOneNode varDefOneNode : varMembers){
                TypeNode typeNode = varDefOneNode.getTypeNode();
                typeNode.accept(this);
                Type type = typeNode.getType();
                String id = varDefOneNode.getId();
                typeTable.get(className).addVarMember(id, type);
            }
            //register method
            ArrayList<FuncDefNode> funcDefNodes= node.getFuncMembers();
            for(FuncDefNode funcDefNode : funcDefNodes){
                String methodName = funcDefNode.getFuncName();
                Function method = convertFuntion(funcDefNode);
                typeTable.get(className).addMethod(methodName, method);
            }
            //registor constructor
            if(node.getConstructor() != null){
                Function constructor = convertConstructor(node.getConstructor());
                typeTable.get(className).setConstructor(constructor);
            }
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            exceptionListener.errorOut(compileError);
        }
    }


    @Override
    public void visit(ProgramNode node) throws CompileError {
        //register class type
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof ClassDefNode){
                registerClassType((ClassDefNode) defUnitNode);
            }
        }
        //register funtion
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof FuncDefNode){
                registerFunction((FuncDefNode) defUnitNode);
            }
        }
        //register variable member and method
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof ClassDefNode){
                registerMember((ClassDefNode) defUnitNode);
            }
        }
        //register global variable
        Scope globalscope = new GlobalScope();
        scopeStack.push(globalscope);
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof VarDefNode){
                defUnitNode.accept(this);
            }
        }
    }

    @Override
    public void visit(ArrayTypeNode node) throws CompileError {
        try{
            NonArrayTypeNode baseTypeNode = node.getBaseType();
            baseTypeNode.accept(this);
            NonArrayType basetype = typeTable.get(baseTypeNode.getTypeName());
            int dim = node.getDim();
            Function sizeFunction = new Function(typeTable.get("int"), new ArrayList<VariableEntity>(), null);
            ArrayType arrayType = new ArrayType(basetype, dim, sizeFunction);
            node.setType(arrayType);
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }

    }

    @Override
    public void visit(NonArrayTypeNode node) throws CompileError {
        try{
            String typeName = node.getTypeName();
            Type type = typeTable.get(typeName);
            node.setType(type);
        }catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(VarDefNode node) throws CompileError {
        ArrayList<VarDefOneNode> varDefs = node.getVarDefs();
        for(VarDefOneNode varDef : varDefs){
            varDef.accept(this);
        }
    }

    @Override
    public void visit(VarDefOneNode node) {
        try{
            //for id check
            String id = node.getId();
            if(scopeStack.peek().hasVar(id))
                throw new CompileError(null, "Duplicate variable name");
            //fetch type
            TypeNode typeNode = node.getTypeNode();
            typeNode.accept(this);
            Type type = typeNode.getType();
            //check initValue
            ExprNode initValue = node.getInitValue();
            initValue.accept(this);
            Type initType = initValue.getExprType();
            if(!type.equal(initType))
                throw new CompileError(null, "Variable type not match");
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            exceptionListener.errorOut(compileError);
        }
    }



}
