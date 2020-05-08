package IR;

import AST.*;
import AST.Function.Function;
import AST.NodeProperties.ExprNode;
import AST.NodeProperties.StatementNode;
import AST.VariableEntity.VariableEntity;
import AST.Visit.ASTVisitor;
import IR.Instruction.*;
import IR.LLVMoperand.*;
import IR.TypeSystem.*;
import Semantic.ASTtype.ArrayType;
import Semantic.ASTtype.NonArray.BoolType;
import Semantic.ASTtype.NonArray.ClassType;
import Semantic.ASTtype.NonArray.IntType;
import Semantic.ASTtype.NonArray.StringType;
import Semantic.ASTtype.Type;
import Semantic.ExceptionHandle.CompileError;
import Semantic.SemanticCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class IRBuilder extends ASTVisitor {
    private Module module;
    private LLVMfunction currentFunction;
    private Block currentBlock;
    private boolean globalScope;
    private String inClassName;
    private Stack<Block> loopBreakStack;
    private Stack<Block> loopContinueStack;


    public IRBuilder(SemanticCheck semanticCheck){
        module = new Module();
        module.initTypeMap(semanticCheck.getTypeTable());
        module.initTypeConstructor(semanticCheck.getTypeTable());
        module.initTypeMethod(semanticCheck.getTypeTable());
        //init normal funtion and built_in function
        HashMap<String, Function> globalFunctionTable = semanticCheck.getFunctionTable().getGlobalTable();
        for(String functionName : globalFunctionTable.keySet()){
            Function function = globalFunctionTable.get(functionName);
            if(function.getCategory() == Function.Category.Normal)
                module.initNormalFunction(functionName, function);
            else if(function.getCategory() == Function.Category.BuiltIn){

            }
        }
        module.initBuiltInFunction(semanticCheck.getTypeTable());
        loopBreakStack = new Stack<Block>();
        loopContinueStack = new Stack<Block>();




    }

    @Override
    public void visit(ProgramNode node) throws CompileError {
        globalScope = true;
        currentFunction = module.getFunctionMap().get("main");
        currentBlock = currentFunction.getInitBlock();
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof VarDefNode){
                defUnitNode.accept(this);
            }
        }
        for(LLVMInstruction llvmInstruction : module.getDefineGlobals()){
            //currentBlock.addInstruction(llvmInstruction);
        }
//        currentBlock.addInstruction(new BranchInst(currentBlock, null, currentFunction.getMainBlock(), null));
//        currentBlock = currentFunction.getMainBlock();
        globalScope = false;

        inClassName = null;
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof FuncDefNode){
                defUnitNode.accept(this);
            }
        }
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof ClassDefNode){
                defUnitNode.accept(this);
            }
        }

    }

    @Override
    public void visit(ArrayTypeNode node) throws CompileError {
        assert false;
    }

    @Override
    public void visit(NonArrayTypeNode node) throws CompileError {
        assert false;
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
            Type astType = node.getTypeNode().getType();
            LLVMtype llvMtype = astType.convert2LLVM(module.getTypeMap());
            String id = node.getId();
            VariableEntity variableEntity = node.getVariableEntity();
            ExprNode initNode = node.getInitValue();
            if(globalScope){
                GlobalVar globalVar = new GlobalVar(new LLVMPointerType(llvMtype), id);
                Operand initOperand;
                if(initNode != null){
                    initNode.accept(this);
                    initOperand = initNode.getResult();
                    if(!initOperand.isConst()){
                        assert false;
                        System.out.println("Need to be modified");//////////////
                    }
                }else{
                    initOperand = astType.getDefaultValue();
                }
                DefineGlobal defineGlobal = new DefineGlobal(globalVar, initOperand);
                module.getDefineGlobals().add(defineGlobal);
                variableEntity.setAllocAddr(globalVar);
            }else{
                Register allocAddr = new Register(new LLVMPointerType(llvMtype), id + "$addr");
                currentFunction.registerVar(allocAddr.getRegisterId(), allocAddr);
                currentBlock.addInstruction(new AllocInst(currentBlock, allocAddr, llvMtype));
                currentBlock.addInstruction(new StoreInst(currentBlock, astType.getDefaultValue(), allocAddr));
                variableEntity.setAllocAddr(allocAddr);
                if(initNode != null){
                    initNode.accept(this);
                    Operand initOperand = initNode.getResult();
                    currentBlock.addInstruction(new StoreInst(currentBlock, initOperand, allocAddr));
                }

            }
        } catch (CompileError compileError) {
            System.out.println("Unpossieble Error in IR build");
        }
    }

    @Override
    public void visit(FuncDefNode node) throws CompileError {
        String functionName = node.getFuncName();
        if(inClassName != null)
            functionName = inClassName + "." + functionName;
        LLVMfunction llvMfunction = module.getFunctionMap().get(functionName);
        currentFunction = llvMfunction;
        currentBlock = currentFunction.getInitBlock();

        node.getFuncBody().accept(this);

        currentBlock.addInstruction(new BranchInst(currentBlock,
                null, currentFunction.getReturnBlock(), null));
        currentFunction.getReturnBlock().setDirectPredecessor(currentBlock);
    }

    @Override
    public void visit(ClassDefNode node) throws CompileError {
        this.inClassName = node.getClassName();
        if(node.getConstructor() != null)
            node.getConstructor().accept(this);
        for(var method : node.getFuncMembers()){
            method.accept(this);
        }
    }

    @Override
    public void visit(ConstructDefNode node) throws CompileError {
        String functionName = inClassName + "." + inClassName;
        LLVMfunction llvMfunction = module.getFunctionMap().get(functionName);
        currentFunction = llvMfunction;
        currentBlock = currentFunction.getInitBlock();

        node.getFuncBody().accept(this);

        currentBlock.addInstruction(new BranchInst(currentBlock,
                null, currentFunction.getReturnBlock(), null));
        currentFunction.getReturnBlock().setDirectPredecessor(currentBlock);
    }

    @Override
    public void visit(BlockNode node) throws CompileError {
        for(StatementNode statementNode : node.getStatements()){
            statementNode.accept(this);
        }
    }

    @Override
    public void visit(VarDefStNode node) throws CompileError {
        node.getVarDef().accept(this);
    }

    @Override
    public void visit(IfNode node) {

    }

    @Override
    public void visit(WhileNode node) throws CompileError {
        Block whileCond = new Block("while$cond", currentFunction);
        currentFunction.registerBlock(whileCond.getName(),whileCond);
        Block whileBody = new Block("while$body", currentFunction);
        currentFunction.registerBlock(whileBody.getName(), whileBody);
        Block whileMerge = new Block("while$merge", currentFunction);
        currentFunction.registerBlock(whileMerge.getName(), whileMerge);
        //while cond
        currentBlock.addInstruction(new BranchInst(currentBlock, null, whileCond, null));
        currentBlock = whileCond;
        node.getCond().accept(this);
        Operand condResult = node.getCond().getResult();
        currentBlock.addInstruction(new BranchInst(currentBlock, condResult, whileBody, whileMerge));
        //while body
        loopContinueStack.push(whileCond);
        loopBreakStack.push(whileMerge);
        currentBlock = whileBody;
        node.getStatement().accept(this);
        currentBlock.addInstruction(new BranchInst(currentBlock, null, whileCond, null));
        loopContinueStack.pop();
        loopBreakStack.pop();
        //while merge
        currentBlock = whileMerge;

    }

    @Override
    public void visit(ForNode node) throws CompileError {
        //forInit
        if(node.getFor_init() != null)
            node.getFor_init().accept(this);
        Block forCond, forUpdate, forBody, forMerge;
        if(node.getCond() != null){
            forCond = new Block("forCond", currentFunction);
            currentFunction.registerBlock(forCond.getName(), forCond);
        }else{
            forCond = null;
        }
        if(node.getFor_update() != null){
            forUpdate = new Block("forUpdate", currentFunction);
            currentFunction.registerBlock(forUpdate.getName(), forUpdate);
        }else{
            forUpdate = null;
        }
        forBody = new Block("forBody", currentFunction);
        currentFunction.registerBlock(forBody.getName(), forBody);
        forMerge = new Block("forMerge", currentFunction);
        currentFunction.registerBlock(forMerge.getName(), forMerge);

        if(node.getCond() != null){
            //forCond Block
            currentBlock.addInstruction(new BranchInst(currentBlock, null, forCond, null));
            currentBlock = forCond;
            node.getCond().accept(this);
            Operand condResult = node.getCond().getResult();
            currentBlock.addInstruction(new BranchInst(currentBlock, condResult, forBody, forMerge));
            //forBody Block
            if(node.getFor_update() != null){
                loopContinueStack.push(forUpdate);
                loopBreakStack.push(forMerge);
                node.getStatement().accept(this);
                currentBlock.addInstruction(new BranchInst(currentBlock, null, forUpdate, null));
                loopBreakStack.pop();
                loopContinueStack.pop();
                //forUpdate
                currentBlock = forUpdate;
                node.getFor_update().accept(this);
                currentBlock.addInstruction(new BranchInst(currentBlock, null, forCond, null));
            }else{
                loopContinueStack.push(forCond);
                loopBreakStack.push(forMerge);
                node.getStatement().accept(this);
                currentBlock.addInstruction(new BranchInst(currentBlock, null, forCond, null));
                loopBreakStack.pop();
                loopContinueStack.pop();
            }
        }else{
            //forBody
            currentBlock.addInstruction(new BranchInst(currentBlock, null, forBody, null));
            if(node.getFor_update() != null){
                loopContinueStack.push(forUpdate);
                loopBreakStack.push(forMerge);
                currentBlock = forBody;
                node.getStatement().accept(this);
                currentBlock.addInstruction(new BranchInst(currentBlock, null, forUpdate, null));
                loopBreakStack.pop();
                loopContinueStack.pop();
                //forUpdate
                currentBlock = forUpdate;
                node.getFor_update().accept(this);
                currentBlock.addInstruction(new BranchInst(currentBlock, null, forBody, null));
            }else{
                loopContinueStack.push(forCond);///////////////////////////////////////////////////
                loopBreakStack.push(forMerge);
                currentBlock = forBody;
                node.getStatement().accept(this);
                currentBlock.addInstruction(new BranchInst(currentBlock, null, forBody, null));
                loopBreakStack.pop();
                loopContinueStack.pop();
            }
        }
        //forMerge
        currentBlock = forMerge;
    }

    @Override
    public void visit(ReturnNode node) throws CompileError {
        if(node.getReturnExpr() != null){
            node.getReturnExpr().accept(this);
            Operand returnResult = node.getReturnExpr().getResult();
            currentBlock.addInstruction(new StoreInst(currentBlock, returnResult, currentFunction.getReturnAddr()));
        }
        currentBlock.addInstruction(new BranchInst(currentBlock, null, currentFunction.getReturnBlock(), null));
    }

    @Override
    public void visit(BreakNode node) {
        currentBlock.addInstruction(new BranchInst(currentBlock, null, loopBreakStack.peek(), null));
    }

    @Override
    public void visit(ContinueNode node) {
        currentBlock.addInstruction(new BranchInst(currentBlock, null, loopContinueStack.peek(), null));
    }

    @Override
    public void visit(EmptyNode node) {
    }

    @Override
    public void visit(ExprStNode node) throws CompileError {
        node.getExpr().accept(this);
    }

    @Override
    public void visit(ThisExprNode node) throws CompileError {
        Register thisAddr = currentFunction.getThisAddr();
        assert thisAddr.getLlvMtype() instanceof LLVMPointerType;
        LLVMtype thisSelfType = ((LLVMPointerType) thisAddr.getLlvMtype()).getBaseType();
        Register thisValue = new Register(thisSelfType, "this");
        currentFunction.registerVar(thisAddr.getRegisterId(),thisAddr);
        currentBlock.addInstruction(new LoadInst(currentBlock, thisAddr,thisValue));
    }

    @Override
    public void visit(ConstExprNode node) throws CompileError {
        String constant = node.getConstant();
        switch (constant.charAt(0)){
            case'n':            //const null
                node.setResult(new ConstNull());
                node.setAllocAddr(null);
                break;
            case't': case 'f':
                boolean flag;   //const bool
                if(constant == "true")
                    flag = true;
                else
                    flag = false;
                node.setResult(new ConstBool(flag));
                node.setAllocAddr(null);
                break;
            case'"':            //const string
                //preprocess string
                constant = constant.replace("\\\\", "\\");
                constant = constant.replace("\\n", "\n");
                constant = constant.replace("\\\"", "\"");
                constant = constant + "\0";
                //fetch globalVar
                GlobalVar globalVar;
                if(module.getConstStringMap().containsKey(constant))
                    globalVar = module.getConstStringMap().get(constant);
                else{
                    int id = module.getConstStringMap().size();
                    globalVar = new GlobalVar(
                            new LLVMPointerType(new LLVMArrayType(constant.length(), new LLVMIntType(LLVMIntType.BitWidth.int8))),
                            ".str." + id
                    );
                    module.getConstStringMap().put(constant, globalVar);
                    Operand init = new ConstString(new LLVMArrayType(constant.length(), new LLVMIntType(LLVMIntType.BitWidth.int8)),
                            constant);
                    DefineGlobal defineGlobal = new DefineGlobal(globalVar, init);
                    module.getDefineGlobals().add(defineGlobal);
                }
                ArrayList<Operand> indexs = new ArrayList<>();
                indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 0));
                indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 0));
                Register firstAddr = new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),
                        "stringFirstAddr");
                currentFunction.registerVar(firstAddr.getRegisterId(), firstAddr);
                currentBlock.addInstruction(new GEPInst(currentBlock, globalVar, indexs, firstAddr));
                //final
                node.setResult(firstAddr);
                node.setAllocAddr(null);
                break;
            default:                        //for int
                int value = Integer.parseInt(constant);
                node.setResult(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), value));
                node.setAllocAddr(null);
        }

    }

    @Override
    public void visit(IdExprNode node) throws CompileError {
        Operand allocAddr = node.getVariableEntity().getAllocAddr();
        if(allocAddr != null){
            //normal variable                                   funtion name????
            assert allocAddr.getLlvMtype() instanceof LLVMPointerType;
            LLVMtype loadType = ((LLVMPointerType) allocAddr.getLlvMtype()).getBaseType();
            Register result = new Register(loadType, node.getId());
            currentFunction.registerVar(result.getRegisterId(), result);
            currentBlock.addInstruction(new LoadInst(currentBlock, allocAddr, result));
            node.setResult(result);
            node.setAllocAddr(allocAddr);
        }else{
            Register thisAddr = currentFunction.getThisAddr();
            assert thisAddr.getLlvMtype() instanceof LLVMPointerType;
            //load 'this'
            Register thisRegister = new Register(((LLVMPointerType) thisAddr.getLlvMtype()).getBaseType(), "this");
            currentFunction.registerVar(thisRegister.getRegisterId(), thisRegister);
            currentBlock.addInstruction(new LoadInst(currentBlock, thisAddr, thisRegister));
            //find pos
            ArrayList<Operand> indexs = new ArrayList<Operand>();
            assert thisRegister.getLlvMtype() instanceof LLVMPointerType;
            assert ((LLVMPointerType) thisRegister.getLlvMtype()).getBaseType() instanceof LLVMStructType;
            LLVMStructType llvmStructType = (LLVMStructType) ((LLVMPointerType) thisRegister.getLlvMtype()).getBaseType();
            int pos = llvmStructType.getMemberIndexMap().get(node.getId());
            indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 0));
            indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), pos));
            //load addr of this.member
            Type memberAstType = node.getExprType();
            LLVMtype memberLlvmType = memberAstType.convert2LLVM(module.getTypeMap());
            Register memberAddr = new Register(new LLVMPointerType(memberLlvmType),
                    "member" + "." + node.getId() + "$addr");
            currentFunction.registerVar(memberAddr.getRegisterId(), memberAddr);
            currentBlock.addInstruction(new GEPInst(currentBlock, thisRegister, indexs, memberAddr));
            //load this.member
            Register member = new Register(memberLlvmType, "member" + "." +node.getId());
            currentFunction.registerVar(member.getRegisterId(), member);
            currentBlock.addInstruction(new LoadInst(currentBlock, memberAddr, member));
            //final
            node.setResult(member);
            node.setAllocAddr(memberAddr);
        }
    }

    @Override
    public void visit(PostfixExprNode node) throws CompileError {
    }

    @Override
    public void visit(NewExprNode_array node) throws CompileError {
        /******/
    }

    @Override
    public void visit(NewExprNode_nonArray node) throws CompileError {
        Type astType = node.getExprType();
        assert astType instanceof ClassType;
        //external funtion
        LLVMfunction mallocFunction = module.getBuiltInFunctionMap().get("malloc");
        //compute size, init paras
        LLVMtype llvMtype = astType.convert2LLVM(module.getTypeMap());
        assert llvMtype instanceof LLVMPointerType;
        int size = ((LLVMPointerType) llvMtype).getBaseType().getByte();
        ArrayList<Operand> paras = new ArrayList<Operand>();
        paras.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), size));
        //add malloc call instruction
        Register mallocResult = new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),
                "malloc$result");
        currentFunction.registerVar(mallocResult.getRegisterId(), mallocResult);
        currentBlock.addInstruction(new CallInst(currentBlock, mallocResult, mallocFunction, paras));
        //add bitcast instruction
        Register castResult = new Register(llvMtype, "castResult");
        currentFunction.registerVar(castResult.getRegisterId(),castResult);
        currentBlock.addInstruction(new BitCastInst(currentBlock, mallocResult, castResult.getLlvMtype(), castResult));
        node.setResult(castResult);
        node.setAllocAddr(null);
        if(astType.getConstructor().getCategory() != Function.Category.defaultConstructor){
            String constructorName = ((ClassType) astType).getName() + "." + ((ClassType) astType).getName();
            LLVMfunction constructor = module.getFunctionMap().get(constructorName);
            assert constructor != null;
            ArrayList<Operand> constructorParas = new ArrayList<Operand>();
            constructorParas.add(castResult);
            currentBlock.addInstruction(new CallInst(currentBlock, null, constructor, constructorParas));
        }
    }

    @Override
    public void visit(MemberExprNode node) throws CompileError {
        node.getExpr().accept(this);
        String name = node.getId();
        Operand faValue = node.getExpr().getResult();
        assert  faValue.getLlvMtype() instanceof LLVMPointerType;
        LLVMtype faSelfType = ((LLVMPointerType) faValue.getLlvMtype()).getBaseType();
        assert  faSelfType instanceof LLVMStructType;
        //init indexs
        int pos = ((LLVMStructType) faSelfType).getMemberIndexMap().get(name);
        ArrayList<Operand> indexs = new ArrayList<Operand>();
        indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 0));
        indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), pos));
        //add GEP Instruction
        LLVMtype memberType = node.getExprType().convert2LLVM(module.getTypeMap());
        Register GepResult = new Register(new LLVMPointerType(memberType), "member." + name + "$address");
        currentFunction.registerVar(GepResult.getRegisterId(), GepResult);
        currentBlock.addInstruction(new GEPInst(currentBlock, faValue, indexs, GepResult));
        //add load instruction
        Register loadResult = new Register(memberType, "member." + name);
        currentFunction.registerVar(loadResult.getRegisterId(), loadResult);
        currentBlock.addInstruction(new LoadInst(currentBlock, GepResult, loadResult));
        node.setResult(loadResult);
        node.setAllocAddr(GepResult);
    }

    @Override
    public void visit(FuncExprNode node) throws CompileError {
        ExprNode funcSelf = node.getFuncSelf();
        LLVMfunction function;
        if(funcSelf instanceof NewExprNode_nonArray){
            funcSelf.accept(this);
            node.setAllocAddr(funcSelf.getAllocAddr());
            node.setResult(funcSelf.getResult());
        }else if(funcSelf instanceof MemberExprNode){
            ExprNode faNode = ((MemberExprNode) funcSelf).getExpr();
            faNode.accept(this);
            String name = ((MemberExprNode) funcSelf).getId();
            Type faType = faNode.getExprType();
            Operand faResult = faNode.getResult();
            if(faType instanceof ArrayType){
                /*for array.size ***************************************** maybe can deleted*/
                Register pointer;
                if(!faResult.getLlvMtype().equals(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int32)))){
                    pointer = new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int32)), "cast$result");
                    currentFunction.registerVar(pointer.getRegisterId(), pointer);
                    currentBlock.addInstruction(new BitCastInst(currentBlock, faResult, pointer.getLlvMtype(), pointer));
                }else{
                    pointer = (Register) faResult;
                }
                //add GEP instruction
                ArrayList<Operand> indexs = new ArrayList<>();
                indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), -1));
                Register GEPresult = new Register(pointer.getLlvMtype(), "GEP$result");
                currentFunction.registerVar(GEPresult.getRegisterId(), GEPresult);
                currentBlock.addInstruction(new GEPInst(currentBlock, pointer, indexs, GEPresult));
                //add load instruction
                Register sizeResult = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "size$result");
                currentFunction.registerVar(sizeResult.getRegisterId(),sizeResult);
                currentBlock.addInstruction(new LoadInst(currentBlock, GEPresult, sizeResult));
                node.setResult(sizeResult);
                node.setAllocAddr(null);
            }else{
                if(faType instanceof StringType){
                    function = module.getBuiltInFunctionMap().get("__string_" + name);
                }else{
                    function = module.getFunctionMap().get(faType.toString() + name);
                }
                assert function != null;
                //init para
                ArrayList<Operand> paras = new ArrayList<Operand>();
                paras.add(faResult);
                for(ExprNode paraNode : node.getParas()){
                    paraNode.accept(this);
                    paras.add(paraNode.getResult());
                }
                //void or not || add instrcution
                LLVMtype returnType = function.getResultType();
                if(returnType instanceof LLVMVoidType){
                    currentBlock.addInstruction(new CallInst(currentBlock, null, function, paras));
                    node.setResult(null);
                    node.setAllocAddr(null);
                } else{
                    Register result = new Register(returnType, "call$result");
                    currentFunction.registerVar(result.getRegisterId(), result);
                    currentBlock.addInstruction(new CallInst(currentBlock, result, function, paras));
                    node.setResult(result);
                    node.setAllocAddr(null);
                }

            }
        }else{
            assert funcSelf instanceof IdExprNode;
            String name = ((IdExprNode) funcSelf).getId();
            if(!module.getFunctionMap().containsKey(name) && !module.getBuiltInFunctionMap().containsKey(name)){
                //method
                function = module.getFunctionMap().get(inClassName + "." + name);
                assert function != null;
                Register thisAddr = currentFunction.getThisAddr();
                assert thisAddr.getLlvMtype() instanceof LLVMPointerType;
                Register thisValue = new Register(((LLVMPointerType) thisAddr.getLlvMtype()).getBaseType(), "this");
                currentFunction.registerVar(thisValue.getRegisterId(), thisValue);
                currentBlock.addInstruction(new LoadInst(currentBlock, thisAddr, thisValue));
                //init paras
                ArrayList<Operand>paras = new ArrayList<Operand>();
                paras.add(thisValue);
                for(ExprNode paraNode : node.getParas()){
                    paraNode.accept(this);
                    paras.add(paraNode.getResult());
                }
                //void or not || add instrcution
                LLVMtype returnType = function.getResultType();
                if(returnType instanceof LLVMVoidType){
                    currentBlock.addInstruction(new CallInst(currentBlock, null, function, paras));
                    node.setResult(null);
                    node.setAllocAddr(null);
                } else{
                    Register result = new Register(returnType, "call$result");
                    currentFunction.registerVar(result.getRegisterId(), result);
                    currentBlock.addInstruction(new CallInst(currentBlock, result, function, paras));
                    node.setResult(result);
                    node.setAllocAddr(null);
                }
            }else{
                //Normal Function
                if(module.getBuiltInFunctionMap().containsKey(name))
                    function = module.getBuiltInFunctionMap().get(name);
                else{
                    assert module.getFunctionMap().containsKey(name);
                    function = module.getFunctionMap().get(name);
                }
                //init paras
                ArrayList<Operand> paras = new ArrayList<Operand>();
                for(ExprNode paraNode : node.getParas()){
                    paraNode.accept(this);
                    paras.add(paraNode.getResult());
                }
                //void or not || add instrcution
                LLVMtype returnType = function.getResultType();
                if(returnType instanceof LLVMVoidType){
                    currentBlock.addInstruction(new CallInst(currentBlock, null, function, paras));
                    node.setResult(null);
                    node.setAllocAddr(null);
                } else{
                    Register result = new Register(returnType, "call$result");
                    currentFunction.registerVar(result.getRegisterId(), result);
                    currentBlock.addInstruction(new CallInst(currentBlock, result, function, paras));
                    node.setResult(result);
                    node.setAllocAddr(null);
                }
            }
        }
    }

    @Override
    public void visit(SubscriptExprNode node) throws CompileError {
        node.getArrayName().accept(this);
        node.getIndex().accept(this);
        //init indexs
        ArrayList<Operand> indexs = new ArrayList<Operand>();
        indexs.add(node.getIndex().getResult());
        //add GEP instructions
        Operand arrayValue = node.getArrayName().getResult();
        Register GepResult = new Register(arrayValue.getLlvMtype(), "GEP$result");
        currentFunction.registerVar(GepResult.getRegisterId(), GepResult);
        currentBlock.addInstruction(new GEPInst(currentBlock, arrayValue, indexs, GepResult));
        //add load instructions
        assert GepResult.getLlvMtype() instanceof LLVMPointerType;
        Register loadResult = new Register(((LLVMPointerType) GepResult.getLlvMtype()).getBaseType(), "load$result");
        currentFunction.registerVar(loadResult.getRegisterId(), loadResult);
        currentBlock.addInstruction(new LoadInst(currentBlock, GepResult, loadResult));
        node.setResult(loadResult);
        node.setAllocAddr(GepResult);
    }

    @Override
    public void visit(PrefixExprNode node) throws CompileError {
        node.getExpr().accept(this);
        String op = node.getOp();
        Operand exprResult = node.getExpr().getResult();
        switch (op){
            case "++": {
                Operand exprAddr = node.getExpr().getAllocAddr();
                Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "preIncrease");
                currentFunction.registerVar(result.getRegisterId(), result);
                currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.add,
                        exprResult, new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 1), result));
                currentBlock.addInstruction(new StoreInst(currentBlock, result, exprAddr));
                node.setResult(result);
                node.setAllocAddr(exprAddr);
                break;
            }
            case"--":{
                Operand exprAddr = node.getExpr().getAllocAddr();
                Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "preDecrease");
                currentFunction.registerVar(result.getRegisterId(), result);
                currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.sub,
                        exprResult, new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 1), result));
                currentBlock.addInstruction(new StoreInst(currentBlock, result, exprAddr));
                node.setResult(result);
                node.setAllocAddr(exprAddr);
                break;
            }
            case"+":{
                node.setResult(exprResult);
                node.setAllocAddr(null);
                break;
            }
            case"-":{
                if(exprResult.isConst()){
                    assert exprResult instanceof ConstInt;
                    node.setResult(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32),
                            -((ConstInt) exprResult).getValue()));
                    node.setAllocAddr(null);
                }else{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "negValue");
                    currentFunction.registerVar(result.getRegisterId(), result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.sub,
                            new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 0), exprResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                }
                break;
            }
            case"!":{
                Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "notValue");
                currentFunction.registerVar(result.getRegisterId(),result);
                currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.xor,
                        new ConstBool(true), exprResult, result));
                node.setResult(result);
                node.setAllocAddr(null);
                break;
            }
            case"~":{
                Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "bitwiseValue");
                currentFunction.registerVar(result.getRegisterId(),result);
                currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.xor,
                        new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), -1), exprResult, result));
                node.setResult(result);
                node.setAllocAddr(null);
                break;
            }
        }
    }

    @Override
    public void visit(BinaryExprNode node) throws CompileError {
        String op = node.getOp();
        if(op != "&&" && op != "||"){
            //for int
            node.getLhs().accept(this);
            node.getRhs().accept(this);
            Operand lhsResult = node.getLhs().getResult();
            Operand rhsResult = node.getRhs().getResult();
            switch (op){
                case"*":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "mulValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.mul,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case"/":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "divValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.sdiv,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case"%":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "modValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.srem,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case"-":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "subValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.sub,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case "<<":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "shiftLeftValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.shl,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case ">>":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "shiftRightValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.ashr,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case "&":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "bitwiseAndValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.and,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case "^":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "bitwiseXorValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.xor,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case "|":{
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "bitwiseOrValue");
                    currentFunction.registerVar(result.getRegisterId(),result);
                    currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.or,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                //
                case "+":{
                    if(node.getRhs().getExprType() instanceof IntType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "add");
                        currentFunction.registerVar(result.getRegisterId(),result);
                        currentBlock.addInstruction(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.and,
                                lhsResult, rhsResult, result));
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else{
                        /**string*/
                    }
                }
                case "<":{
                    if(node.getRhs().getExprType() instanceof IntType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "lessThen");
                        currentFunction.registerVar(result.getRegisterId(),result);
                        currentBlock.addInstruction(new IcmpInst(currentBlock, IcmpInst.IcmpName.slt,
                                new LLVMIntType(LLVMIntType.BitWidth.int32),
                                lhsResult, rhsResult, result));
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else{
                        /**string*/
                    }
                }
                case ">":{
                    if(node.getRhs().getExprType() instanceof IntType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "greaterThen");
                        currentFunction.registerVar(result.getRegisterId(),result);
                        currentBlock.addInstruction(new IcmpInst(currentBlock, IcmpInst.IcmpName.sgt,
                                new LLVMIntType(LLVMIntType.BitWidth.int32),
                                lhsResult, rhsResult, result));
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else{

                    }
                }
                case "<=":{
                    if(node.getRhs().getExprType() instanceof IntType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "lessEqual");
                        currentFunction.registerVar(result.getRegisterId(),result);
                        currentBlock.addInstruction(new IcmpInst(currentBlock, IcmpInst.IcmpName.sle,
                                new LLVMIntType(LLVMIntType.BitWidth.int32),
                                lhsResult, rhsResult, result));
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else{

                    }
                }
                case ">=":{
                    if(node.getRhs().getExprType() instanceof IntType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "greaterEqual");
                        currentFunction.registerVar(result.getRegisterId(),result);
                        currentBlock.addInstruction(new IcmpInst(currentBlock, IcmpInst.IcmpName.sge,
                                new LLVMIntType(LLVMIntType.BitWidth.int32),
                                lhsResult, rhsResult, result));
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else{

                    }
                }
                case "==":{
                    Type ltype = node.getLhs().getExprType();
                    Type rtype = node.getRhs().getExprType();
                    if(ltype instanceof IntType && rtype instanceof IntType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "equal");
                        currentFunction.registerVar(result.getRegisterId(), result);
                        currentBlock.addInstruction(new IcmpInst(currentBlock, IcmpInst.IcmpName.eq,
                                new LLVMIntType(LLVMIntType.BitWidth.int32), lhsResult, rhsResult, result));

                        node.setResult(result);
                        node.setAllocAddr(null);
                    }else if(ltype instanceof BoolType && rtype instanceof BoolType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "equal");
                        currentFunction.registerVar(result.getRegisterId(), result);
                        currentBlock.addInstruction(new IcmpInst(currentBlock, IcmpInst.IcmpName.eq,
                                new LLVMIntType(LLVMIntType.BitWidth.int1), lhsResult, rhsResult, result));

                        node.setResult(result);
                        node.setAllocAddr(null);
                    }else{

                    }
                }
            }

        }
    }

    @Override
    public void visit(AssignExprNode node) throws CompileError {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        Operand rhsResult = node.getRhs().getResult();
        Operand lhsAddr = node.getLhs().getAllocAddr();
        currentBlock.addInstruction(new StoreInst(currentBlock, rhsResult, lhsAddr));
        node.setResult(rhsResult);
        node.setAllocAddr(null);//***************************
    }

    public Module getModule() {
        return module;
    }
}
