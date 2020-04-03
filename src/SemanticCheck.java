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
            }else{
                Function constructor = new Function(typeTable.get(className), new ArrayList<VariableEntity>(), null);
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



    /*visit TypeNode***************************************************************/
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

    /*visit DefNode*************************************************************/

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
            //register
            scopeStack.peek().put(id, type);
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            exceptionListener.errorOut(compileError);
        }
    }



    /*for ExprNode visit********************************************************/

    @Override
    public void visit(ThisExprNode node) throws CompileError {
        try{
            Type type = null;
            for(Scope scope : scopeStack){
                if(scope.hasVar("this"))
                    type = scope.getVarType("this");
            }
            if(type == null){
                throw new CompileError(null, "Access 'this' not in method");
            }
            node.setExprType(type);
            node.setLvalue(true);
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(ConstExprNode node) throws CompileError {
        try{
            String constant = node.getConstant();
            if(constant.charAt(0) == 'n'){
                node.setExprType(typeTable.get("null"));
                node.setLvalue(false);  //rvalue
            }else if('0' <= constant.charAt(0) && constant.charAt(0) <= 9){
                node.setExprType(typeTable.get("int"));
                node.setLvalue(false);
            }else if(constant.charAt(0) == 't' || constant.charAt(0) == 'f'){
                node.setExprType(typeTable.get("bool"));
                node.setLvalue(false);
            }else if(constant.charAt(0) == '"'){
                node.setExprType(typeTable.get("string"));
                node.setLvalue(false);
            }else{
                throw new CompileError(null, "No possioble error");
            }
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(IdExprNode node) throws CompileError {
        try{
            String id = node.getId();
            Type type = null;
            for(Scope scope : scopeStack){
                if(scope.hasVar(id))
                    type = scope.getVarType(id);
            }
            if(type != null){
                node.setExprType(type);
                node.setLvalue(true);
            }else if(functionTable.hasFunc(id)){
                node.setExprType(new FunctionType());
                /*the lvalue is decided when processing function*/
            }else{
                throw new CompileError(null, "Cannot find variable or function");
            }
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(PostfixExprNode node) throws CompileError {
        try{
            ExprNode exprNode = node.getExpr();
            exprNode.accept(this);
            if(exprNode.getLvalue() == false)
                throw new CompileError(null, "rvalue not assignable");
            if(exprNode.getExprType() != typeTable.get("int"))
                throw new CompileError(null, "Postfix '++' or '--' can only apply to 'int'");
            node.setExprType(typeTable.get("int"));
            node.setLvalue(false);
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(NewExprNode_array node) throws CompileError {
        try{
            NonArrayTypeNode baseTypeNode = node.getBaseType();
            baseTypeNode.accept(this);
            NonArrayType baseNonArrayType = (NonArrayType) baseTypeNode.getType();
            int dim = node.getDim();
            Function sizeFunction = new Function(typeTable.get("int"), new ArrayList<VariableEntity>(), null);
            node.setExprType(new ArrayType(baseNonArrayType, dim, sizeFunction));
            node.setLvalue(false);
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(NewExprNode_nonArray node) throws CompileError {
        try{
            NonArrayTypeNode typeNode = node.getType();
            typeNode.accept(this);
            NonArrayType type = (NonArrayType) typeNode.getType();
            node.setExprType(type);
            node.setLvalue(false);
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(MemberExprNode node) throws CompileError {
        try{
            ExprNode faNode = node.getExpr();
            faNode.accept(this);
            Type faType = faNode.getExprType();
            String id = node.getId();
            if(faType.hasVarMember(id)){
                node.setExprType(faType.getMemberType(id));
                node.setLvalue(faNode.getLvalue());
            }else if(faType.hasMethod(id)){
                node.setExprType(new FunctionType());
                /*the lvalue is decided when processing function*/
            }else{
                throw new CompileError(null, "Member not exist");
            }
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(FuncExprNode node) throws CompileError {
        try{
            //check function itself
            ExprNode funcSelf = node.getFuncSelf();
            funcSelf.accept(this);
            Function function;
            if(funcSelf instanceof IdExprNode){
                if(!new FunctionType().equal(funcSelf.getExprType()))
                    throw new CompileError(null, "Function not exist");
                String funcName = ((IdExprNode) funcSelf).getId();
                function = functionTable.getFunc(funcName);
            }else if(funcSelf instanceof MemberExprNode){
                if(!new FunctionType().equal(funcSelf.getExprType()))
                    throw new CompileError(null, "Function not exist");
                Type faType = ((MemberExprNode) funcSelf).getExpr().getExprType();
                String methodName = ((MemberExprNode) funcSelf).getId();
                function = faType.getMethod(methodName);
            }else if(funcSelf instanceof NewExprNode_nonArray){
                Type constructType = funcSelf.getExprType();
                function = constructType.getConstructor();
            }else{
                throw new CompileError(null, "Function not exist");
            }
            //check parameter match formal parameter
            ArrayList<VariableEntity> paras = function.getParas();
            ArrayList<ExprNode> argus = node.getParas();
            if(paras.size() != argus.size())
                throw new CompileError(null, "The argument do not match formal parameter");
            for(int i = 0; i < paras.size(); i++){
                ExprNode arguement = argus.get(i);
                arguement.accept(this);
                VariableEntity para = paras.get(i);
                if(!arguement.getExprType().equal(para.getType()))
                    throw new CompileError(null, "The argument do not match formal parameter");
            }
            //modify node
            Type returnType = function.getReturnType();
            node.setExprType(returnType);
            if(returnType.equal(typeTable.get("int")) || returnType.equal(typeTable.get("bool")))
                node.setLvalue(false);
            else
                node.setLvalue(true);
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(SubscriptExprNode node) throws CompileError {
        try{
            //for arryName
            ExprNode arrayName = node.getArrayName();
            arrayName.accept(this);
            Type arrayType = arrayName.getExprType();
            if(!(arrayType instanceof ArrayType))
                throw new CompileError(null, "Illegal index access");
            //for index
            ExprNode index = node.getIndex();
            index.accept(this);
            Type indexType = index.getExprType();
            if(!(indexType instanceof IntType))//**************************************
                throw new CompileError(null, "Illegal Index");
            //overall
            int nowDim = ((ArrayType) arrayType).getDim();
            if(nowDim == 1){
                node.setExprType(((ArrayType) arrayType).getBaseNonArrayType());
                node.setLvalue(arrayName.getLvalue());
            }else{
                Function sizeFunction = new Function(typeTable.get("int"),
                        new ArrayList<VariableEntity>(), null);
                node.setExprType(new ArrayType(((ArrayType) arrayType).getBaseNonArrayType(),
                        nowDim-1, sizeFunction));
                node.setLvalue(arrayName.getLvalue());
            }
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(PrefixExprNode node) throws CompileError {
        try{
            ExprNode exprNode = node.getExpr();
            exprNode.accept(this);
            Type type = exprNode.getExprType();
            if(node.getOp().equals("!")){
                if(!(type instanceof BoolType))
                    throw new CompileError(null, "'!' can only be applied to bool type");
            }else if(node.getOp().equals("++") || node.getOp().equals("--") || node.getOp().equals("+") ||
                    node.getOp().equals("-") || node.getOp().equals("~")){
                if(!(type instanceof IntType)){
                    throw new CompileError(null,
                            "Prefix symbol " + node.getOp() + " can only be applied to int type");
                }
            }else{
                throw new CompileError(null, "No possible error");
            }
            if(!exprNode.getLvalue())
                throw new CompileError(null,
                        "Prefix symbol " + node.getOp() + " can only be applied to lvalue");
            node.setExprType(type);
            node.setLvalue(true);
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(BinaryExprNode node) throws CompileError {
        try{
            ExprNode lhsNode = node.getLhs();
            ExprNode rhsNode = node.getRhs();
            lhsNode.accept(this);
            rhsNode.accept(this);
            String op = node.getOp();
            switch (op){
                case "+":
                    if(lhsNode.getExprType() != rhsNode.getExprType()){
                        throw new CompileError(null, "Type not match for " + op);
                    }
                    if(!(lhsNode.getExprType() instanceof IntType || lhsNode.getExprType() instanceof StringType))
                        throw new CompileError(null, "Type not allowed for " + op);
                    node.setExprType(lhsNode.getExprType());
                    node.setLvalue(false);
                    break;
                case "<": case ">": case "<=": case ">=":
                    if(lhsNode.getExprType() != rhsNode.getExprType()){
                        throw new CompileError(null, "Type not match for " + op);
                    }
                    if(!(lhsNode.getExprType() instanceof IntType || lhsNode.getExprType() instanceof StringType))
                        throw new CompileError(null, "Type not allowed for " + op);
                    node.setExprType(typeTable.get("bool"));
                    node.setLvalue(false);
                    break;
                case "-": case "*": case "/": case "%": case "<<": case ">>": case "&": case "^": case "|":
                    if(lhsNode.getExprType() != rhsNode.getExprType()){
                        throw new CompileError(null, "Type not match for " + op);
                    }
                    if(!(lhsNode.getExprType() instanceof IntType))
                        throw new CompileError(null, "Type not allowed for " + op);
                    node.setExprType(typeTable.get("int"));
                    node.setLvalue(false);
                    break;
                case "&&": case "||":
                    if(lhsNode.getExprType() != rhsNode.getExprType()){
                        throw new CompileError(null, "Type not match for " + op);
                    }
                    if(!(lhsNode.getExprType() instanceof BoolType))
                        throw new CompileError(null, "Type not allowed for " + op);
                    node.setExprType(typeTable.get("bool"));
                    node.setLvalue(false);
                    break;
                case "==": case "!=":
                    if((lhsNode.getExprType() instanceof IntType && lhsNode.getExprType() instanceof IntType) ||
                            (lhsNode.getExprType() instanceof BoolType && rhsNode.getExprType() instanceof BoolType) ||
                            (lhsNode.getExprType() instanceof StringType && rhsNode.getExprType() instanceof StringType) ||
                            (lhsNode.getExprType() instanceof ArrayType && rhsNode.getExprType() instanceof NullType) ||
                            (lhsNode.getExprType() instanceof NullType && rhsNode.getExprType() instanceof ArrayType) ||
                            (lhsNode.getExprType() instanceof ClassType && rhsNode.getExprType() instanceof NullType) ||
                            (lhsNode.getExprType() instanceof NullType && rhsNode.getExprType() instanceof ClassType) ||
                            (lhsNode.getExprType() instanceof NullType && rhsNode.getExprType() instanceof NullType)){
                        node.setExprType(typeTable.get("bool"));
                        node.setLvalue(false);
                    }else{
                        throw new CompileError(null, "Type not allowed for " + op);
                    }
            }
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }

    @Override
    public void visit(AssignExprNode node) throws CompileError {
        try{
            ExprNode lhsNode = node.getLhs();
            ExprNode rhsNode = node.getRhs();
            lhsNode.accept(this);
            rhsNode.accept(this);
            if(rhsNode.getExprType() instanceof NullType){
                if((lhsNode.getExprType() instanceof ClassType) || (lhsNode.getExprType() instanceof ArrayType)){
                    node.setExprType(lhsNode.getExprType());
                    node.setLvalue(true);/*???????????????????????????????????????????????????????*/
                }else{
                    throw new CompileError(null, "Null can not assign to this type");
                }
            }else{
                if(lhsNode.getExprType().equal(rhsNode.getExprType())){
                    node.setExprType(lhsNode.getExprType());
                    node.setLvalue(true);
                }else{
                    throw new CompileError(null, "Type do not match when assign");
                }
            }
        } catch (CompileError compileError) {
            compileError.setLocation(node.getLocation());
            throw compileError;
        }
    }
}
