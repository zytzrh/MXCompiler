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
import Semantic.ASTtype.NonArray.*;
import Semantic.ASTtype.Type;
import Semantic.ASTtype.TypeTable;
import Semantic.ExceptionHandle.CompileError;
import Semantic.SemanticCheck;
import Utility.Pair;

import java.util.*;

public class IRBuilder extends ASTVisitor {
    private Module module;
    private LLVMfunction currentFunction;
    private Block currentBlock;
    private boolean globalScope;
    private String inClassName;
    private Stack<Block> loopBreakStack;    //only record the peak is enough
    private Stack<Block> loopContinueStack;


    public IRBuilder(SemanticCheck semanticCheck) throws CompileError {
        module = new Module();
        TypeTable astTypeTable = semanticCheck.getTypeTable();
        module.initTypeMap(astTypeTable);
        module.initTypeConstructor(astTypeTable);
        module.initTypeMethod(astTypeTable);
        //init normal funtion and built_in function
        HashMap<String, Function> globalFunctionTable = semanticCheck.getFunctionTable().getGlobalTable();
        for(String functionName : globalFunctionTable.keySet()){
            Function function = globalFunctionTable.get(functionName);
            if(function.getCategory() == Function.Category.Normal)
                module.initNormalFunction(functionName, function);
            else if(function.getCategory() == Function.Category.BuiltIn){

            }
        }
        Function globalInitializer = new Function(astTypeTable.get("void"), new ArrayList<>(), null,
                Function.Category.Normal);
        module.initNormalFunction("__init__", globalInitializer);
        module.initBuiltInFunction(astTypeTable);
        loopBreakStack = new Stack<Block>();
        loopContinueStack = new Stack<Block>();

    }

    @Override
    public void visit(ProgramNode node) throws CompileError {
        //globalInitializer
        globalScope= true;
        LLVMfunction globalInitializer = module.getFunctionMap().get("__init__");
        currentFunction = globalInitializer;
        currentBlock = currentFunction.getInitBlock();
        currentFunction.registerBlock(currentBlock.getName(), currentBlock);
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof VarDefNode){
                defUnitNode.accept(this);
            }
        }
        currentBlock.addInst(new BranchInst(currentBlock, null, currentFunction.getReturnBlock(), null));
        currentBlock = currentFunction.getReturnBlock();
        currentFunction.registerBlock(currentBlock.getName(), currentBlock);
        globalScope = false;


        for(LLVMInstruction llvmInstruction : module.getDefineGlobals()){
            //currentBlock.addInstruction(llvmInstruction);
        }

        inClassName = null;
        for(var defUnitNode : node.getDefUnits()){
            if(defUnitNode instanceof FuncDefNode){
                defUnitNode.accept(this);
            }
        }

        //main function add call
        currentFunction = module.getFunctionMap().get("main");
        currentBlock = currentFunction.getInitBlock();
        currentBlock.addInstFront(new CallInst(currentBlock, null, globalInitializer, new ArrayList<>()));

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
                        currentBlock.addInst(new StoreInst(currentBlock, initOperand, globalVar));
                        initOperand = astType.getDefaultValue();
                    }
                }else{
                    initOperand = astType.getDefaultValue();                //gugu changed the default value of ast can be combined to IR
                }
                DefineGlobal defineGlobal = new DefineGlobal(globalVar, initOperand);
                module.getDefineGlobals().add(defineGlobal);
                variableEntity.setAllocAddr(globalVar);
            }else{
                Register allocAddr = new Register(new LLVMPointerType(llvMtype), id + "$addr");
                currentFunction.registerVar(allocAddr.getName(), allocAddr);
                Block initBlock = currentFunction.getInitBlock();
                initBlock.addInstFront(new StoreInst(initBlock, astType.getDefaultValue(), allocAddr));   //gugu changed:why reverse
                initBlock.addInstFront(new AllocInst(initBlock, allocAddr, llvMtype));
                variableEntity.setAllocAddr(allocAddr);
                if(initNode != null){
                    initNode.accept(this);
                    Operand initOperand = initNode.getResult();
                    currentBlock.addInst(new StoreInst(currentBlock, initOperand, allocAddr));
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
            functionName = inClassName + "$" + functionName;
        LLVMfunction llvMfunction = module.getFunctionMap().get(functionName);
        currentFunction = llvMfunction;
        currentBlock = currentFunction.getInitBlock();
        currentFunction.registerBlock(currentBlock.getName(), currentBlock);

        node.getFuncBody().accept(this);

        currentBlock.addInst(new BranchInst(currentBlock,
                null, currentFunction.getReturnBlock(), null));     //maybe not successful
        currentBlock = currentFunction.getReturnBlock();
        currentFunction.registerBlock(currentBlock.getName(), currentBlock);          //gugu changed: why returnBlock must be the last one
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
        String functionName = inClassName + "$" + inClassName;
        LLVMfunction llvMfunction = module.getFunctionMap().get(functionName);
        currentFunction = llvMfunction;
        currentBlock = currentFunction.getInitBlock();
        currentFunction.registerBlock(currentBlock.getName(), currentBlock);

        node.getFuncBody().accept(this);

        currentBlock.addInst(new BranchInst(currentBlock,
                null, currentFunction.getReturnBlock(), null));
        currentBlock = currentFunction.getReturnBlock();
        currentFunction.registerBlock(currentBlock.getName(), currentBlock);        //gugu changed: why returnBlock must be the last one
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
    public void visit(IfNode node) throws CompileError {
        if(node.getElse_st() != null){
            Block thenBlock = new Block("ifThenBlock", currentFunction);
            Block elseBlock = new Block("ifElseBlock", currentFunction);
            Block mergeBlock = new Block("ifMergeBlock", currentFunction);
            currentFunction.registerBlock(thenBlock.getName(), thenBlock);
            currentFunction.registerBlock(elseBlock.getName(), elseBlock);
            currentFunction.registerBlock(mergeBlock.getName(), mergeBlock);
            node.getCond().accept(this);
            Operand condResult = node.getCond().getResult();
            //originalBlock
            currentBlock.addInst(new BranchInst(currentBlock, condResult, thenBlock, elseBlock));

            //thenBlock
            currentBlock = thenBlock;
            node.getThen_st().accept(this);
            currentBlock.addInst(new BranchInst(currentBlock, null, mergeBlock, null));

            //elseBlock
            currentBlock = elseBlock;
            node.getElse_st().accept(this);
            currentBlock.addInst(new BranchInst(currentBlock, null, mergeBlock, null));

            //mergeBlock
            currentBlock = mergeBlock;
        }else{
            Block thenBlock = new Block("ifThenBlock", currentFunction);
            Block mergeBlock = new Block("ifMergeBlock", currentFunction);
            currentFunction.registerBlock(thenBlock.getName(), thenBlock);
            currentFunction.registerBlock(mergeBlock.getName(), mergeBlock);
            node.getCond().accept(this);
            Operand condResult = node.getCond().getResult();
            //originalBlock
            currentBlock.addInst(new BranchInst(currentBlock, condResult, thenBlock, mergeBlock));

            //thenBlock
            currentBlock = thenBlock;
            node.getThen_st().accept(this);
            currentBlock.addInst(new BranchInst(currentBlock, null, mergeBlock, null));

            //mergeBlock
            currentBlock = mergeBlock;
        }

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
        currentBlock.addInst(new BranchInst(currentBlock, null, whileCond, null));
        currentBlock = whileCond;
        node.getCond().accept(this);
        Operand condResult = node.getCond().getResult();
        currentBlock.addInst(new BranchInst(currentBlock, condResult, whileBody, whileMerge));
        //while body
        loopContinueStack.push(whileCond);
        loopBreakStack.push(whileMerge);
        currentBlock = whileBody;
        node.getStatement().accept(this);
        currentBlock.addInst(new BranchInst(currentBlock, null, whileCond, null));
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
        //
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
            currentBlock.addInst(new BranchInst(currentBlock, null, forCond, null));
            currentBlock = forCond;
            node.getCond().accept(this);
            Operand condResult = node.getCond().getResult();
            currentBlock.addInst(new BranchInst(currentBlock, condResult, forBody, forMerge));
            //forBody Block
            if(node.getFor_update() != null){
                loopContinueStack.push(forUpdate);
                loopBreakStack.push(forMerge);
                currentBlock = forBody;
                node.getStatement().accept(this);
                currentBlock.addInst(new BranchInst(currentBlock, null, forUpdate, null));
                loopBreakStack.pop();
                loopContinueStack.pop();
                //forUpdate
                currentBlock = forUpdate;
                node.getFor_update().accept(this);
                currentBlock.addInst(new BranchInst(currentBlock, null, forCond, null));
            }else{
                loopContinueStack.push(forCond);
                loopBreakStack.push(forMerge);
                currentBlock = forBody;
                node.getStatement().accept(this);
                currentBlock.addInst(new BranchInst(currentBlock, null, forCond, null));
                loopBreakStack.pop();
                loopContinueStack.pop();
            }
        }else{
            //forBody
            currentBlock.addInst(new BranchInst(currentBlock, null, forBody, null));
            if(node.getFor_update() != null){
                loopContinueStack.push(forUpdate);
                loopBreakStack.push(forMerge);
                currentBlock = forBody;
                node.getStatement().accept(this);
                currentBlock.addInst(new BranchInst(currentBlock, null, forUpdate, null));
                loopBreakStack.pop();
                loopContinueStack.pop();
                //forUpdate
                currentBlock = forUpdate;
                node.getFor_update().accept(this);
                currentBlock.addInst(new BranchInst(currentBlock, null, forBody, null));
            }else{
                loopContinueStack.push(forCond);///////////////////////////////////////////////////
                loopBreakStack.push(forMerge);
                currentBlock = forBody;
                node.getStatement().accept(this);
                currentBlock.addInst(new BranchInst(currentBlock, null, forBody, null));
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
            currentBlock.addInst(new StoreInst(currentBlock, returnResult, currentFunction.getReturnAddr()));
        }
        currentBlock.addInst(new BranchInst(currentBlock, null, currentFunction.getReturnBlock(), null));
    }

    @Override
    public void visit(BreakNode node) {
        currentBlock.addInst(new BranchInst(currentBlock, null, loopBreakStack.peek(), null));
    }

    @Override
    public void visit(ContinueNode node) {
        currentBlock.addInst(new BranchInst(currentBlock, null, loopContinueStack.peek(), null));
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
        Register thisRegister = new Register(thisSelfType, "this");
        currentFunction.registerVar(thisRegister.getName(), thisRegister);
        currentBlock.addInst(new LoadInst(currentBlock, thisAddr,thisRegister));

        node.setResult(thisRegister);
        node.setAllocAddr(null);
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
                if(constant.equals("true"))
                    flag = true;
                else
                    flag = false;
                node.setResult(new ConstBool(flag));
                node.setAllocAddr(null);
                break;
            case'"':            //const string
                //preprocess string
                constant = constant.substring(1, constant.length()-1);
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
                currentFunction.registerVar(firstAddr.getName(), firstAddr);
                currentBlock.addInst(new GEPInst(currentBlock, globalVar, indexs, firstAddr));
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
            currentFunction.registerVar(result.getName(), result);
            currentBlock.addInst(new LoadInst(currentBlock, allocAddr, result));
            node.setResult(result);
            node.setAllocAddr(allocAddr);
        }else{
            Register thisAddr = currentFunction.getThisAddr();
            assert thisAddr.getLlvMtype() instanceof LLVMPointerType;
            //load 'this'
            Register thisRegister = new Register(((LLVMPointerType) thisAddr.getLlvMtype()).getBaseType(), "this");
            currentFunction.registerVar(thisRegister.getName(), thisRegister);
            currentBlock.addInst(new LoadInst(currentBlock, thisAddr, thisRegister));
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
                    "member" + "$" + node.getId() + "$addr");
            currentFunction.registerVar(memberAddr.getName(), memberAddr);
            currentBlock.addInst(new GEPInst(currentBlock, thisRegister, indexs, memberAddr));
            //load this.member
            Register member = new Register(memberLlvmType, "member" + "$" +node.getId());
            currentFunction.registerVar(member.getName(), member);
            currentBlock.addInst(new LoadInst(currentBlock, memberAddr, member));
            //final
            node.setResult(member);
            node.setAllocAddr(memberAddr);
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
        Register GepResult = new Register(new LLVMPointerType(memberType), "member$" + name + "$address");
        currentFunction.registerVar(GepResult.getName(), GepResult);
        currentBlock.addInst(new GEPInst(currentBlock, faValue, indexs, GepResult));
        //add load instruction
        Register loadResult = new Register(memberType, "member$" + name);
        currentFunction.registerVar(loadResult.getName(), loadResult);
        currentBlock.addInst(new LoadInst(currentBlock, GepResult, loadResult));
        node.setResult(loadResult);
        node.setAllocAddr(GepResult);
    }

    @Override
    public void visit(PostfixExprNode node) throws CompileError {
        node.getExpr().accept(this);
        String op = node.getOp();
        Operand exprResult = node.getExpr().getResult();
        Operand addr = node.getExpr().getAllocAddr();
        Register result;
        switch (node.getOp()){
            case "--":{
                result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "postDec");
                currentFunction.registerVar(result.getName(), result);
                currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.sub, exprResult,
                        new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32),1),
                        result));
                currentBlock.addInst(new StoreInst(currentBlock, result, addr));
                node.setAllocAddr(null);
                node.setResult(exprResult);
                break;
            }
            case "++":{
                result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "postInc");
                currentFunction.registerVar(result.getName(), result);
                currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.add, exprResult,
                        new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32),1),
                        result));
                currentBlock.addInst(new StoreInst(currentBlock, result, addr));
                node.setAllocAddr(null);
                node.setResult(exprResult);
                break;
            }
            default:assert false;
        }

    }

    @Override
    public void visit(NewExprNode_array node) throws CompileError {
        ArrayList<ExprNode> lenPerDim = node.getLenPerDim();
        Type astBaseType = node.getBaseType().getType();
        LLVMtype llvMtype = astBaseType.convert2LLVM(module.getTypeMap());
        for(int i = 0; i< node.getDim(); i++){
            llvMtype = new LLVMPointerType(llvMtype);
        }

        ArrayList<Operand> sizeList = new ArrayList<>();
        for(ExprNode len : lenPerDim){
            len.accept(this);
            sizeList.add(len.getResult());
        }
        //begin
        Operand result = newArrayMalloc(0,sizeList,llvMtype);
        node.setResult(result);
        node.setAllocAddr(null);

    }

    public Operand newArrayMalloc(int cur, ArrayList<Operand> sizeList, LLVMtype llvMtype){
        LLVMfunction function = module.getBuiltInFunctionMap().get("malloc");
        ArrayList<Operand>paras = new ArrayList<>();
        Register mallocResult = new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int8)),"malloc");
        currentFunction.registerVar(mallocResult.getName(), mallocResult);
        int baseSize = llvMtype.getByte();
        //calculate size
        Register bytesMul = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "bytesMul");
        Register bytes = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "bytes");
        currentFunction.registerVar(bytesMul.getName(), bytesMul);
        currentFunction.registerVar(bytes.getName(), bytes);
        currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.mul, sizeList.get(cur),
                new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), baseSize),
                bytesMul));
        currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.add, bytesMul,
                new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 4),
                bytes));
        paras.add(bytes);
        //call inst
        currentBlock.addInst(new CallInst(currentBlock, mallocResult, function, paras));
        //cast to int32
        Register mallocInt32 = new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int32)), "mallocInt32");
        currentFunction.registerVar(mallocInt32.getName(), mallocInt32);
        currentBlock.addInst(new BitCastInst(currentBlock, mallocResult, mallocInt32.getLlvMtype(), mallocInt32));
        //Store size
        currentBlock.addInst(new StoreInst(currentBlock, sizeList.get(cur), mallocInt32));
        //GetElemPtr to next
        Register arrayHeadInt32 = new Register(new LLVMPointerType(new LLVMIntType(LLVMIntType.BitWidth.int32)), "arrayHeadInt32");
        currentFunction.registerVar(arrayHeadInt32.getName(), arrayHeadInt32);
        ArrayList<Operand> indexs = new ArrayList<>();
        indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 1));
        currentBlock.addInst(new GEPInst(currentBlock, mallocInt32, indexs, arrayHeadInt32));
        //cast to object
        Register arrayHead = new Register(llvMtype, "arrayHead");
        currentFunction.registerVar(arrayHead.getName(), arrayHead);
        currentBlock.addInst(new BitCastInst(currentBlock, arrayHeadInt32, llvMtype, arrayHead));

        //for next dimension
        if(cur != sizeList.size()-1){
            Register arrayTail = new Register(llvMtype, "arrayTail");
            currentFunction.registerVar(arrayTail.getName(), arrayTail);
            indexs = new ArrayList<>();
            indexs.add(sizeList.get(cur));
            currentBlock.addInst(new GEPInst(currentBlock, arrayHead, indexs, arrayTail));
            //alloc temporary arrayPointer
            Register arrayPtrAddr = new Register(new LLVMPointerType(llvMtype), "arrayPtr$addr");
            currentFunction.registerVar(arrayPtrAddr.getName(), arrayHeadInt32);
            Block initBlock = currentFunction.getInitBlock();
            initBlock.addInstFront(new StoreInst(initBlock, llvMtype.DefaultValue(), arrayPtrAddr));
            initBlock.addInstFront(new AllocInst(initBlock, arrayPtrAddr, llvMtype));
            //arrayHead to arrayPtr
            currentBlock.addInst(new StoreInst(currentBlock, arrayHead, arrayPtrAddr));

            //begin loop
            //initialize block
            Block condBlock = new Block("newLoopCond", currentFunction);
            Block bodyBlock = new Block("newLoopBody", currentFunction);
            Block mergeBlock = new Block("newLoopMerge", currentFunction);
            currentFunction.registerBlock(condBlock.getName(), condBlock);
            currentFunction.registerBlock(bodyBlock.getName(), bodyBlock);
            currentFunction.registerBlock(mergeBlock.getName(), mergeBlock);

            //condBlock
            currentBlock.addInst(new BranchInst(currentBlock, null, condBlock, null));
            currentBlock = condBlock;
            //check arrayPointer != arrayTail
            Register arrayPointer = new Register(llvMtype, "arrayPointer");
            Register cmpResult = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "ptrCmpResult");
            currentFunction.registerVar(arrayPointer.getName(), arrayPointer);
            currentFunction.registerVar(cmpResult.getName(), cmpResult);
            currentBlock.addInst(new LoadInst(currentBlock, arrayPtrAddr, arrayPointer));
            currentBlock.addInst(new IcmpInst(currentBlock, IcmpInst.IcmpName.slt, llvMtype,
                    arrayPointer, arrayTail, cmpResult));
            //true->bodyBlock   ||   false->mergeBlock
            condBlock.addInst(new BranchInst(currentBlock, cmpResult, bodyBlock, mergeBlock));

            //bodyBlock
            currentBlock = bodyBlock;
            assert llvMtype instanceof LLVMPointerType;
            Operand arrayHeadNextDim = newArrayMalloc(cur + 1, sizeList, ((LLVMPointerType) llvMtype).getBaseType());
            currentBlock.addInst(new StoreInst(currentBlock, arrayHeadNextDim, arrayPointer));
            //GEP to next arrayPointer
            Register nextArrayPtr = new Register(llvMtype, "nextArrayPtr");
            currentFunction.registerVar(nextArrayPtr.getName(), nextArrayPtr);
            indexs = new ArrayList<>();
            indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 1));
            currentBlock.addInst(new GEPInst(currentBlock, arrayPointer, indexs, nextArrayPtr));
            currentBlock.addInst(new StoreInst(currentBlock, nextArrayPtr, arrayPtrAddr));
            currentBlock.addInst(new BranchInst(currentBlock, null, condBlock, null));

            //MergeBlock
            currentBlock = mergeBlock;
        }
        return arrayHead;

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
        currentFunction.registerVar(mallocResult.getName(), mallocResult);
        currentBlock.addInst(new CallInst(currentBlock, mallocResult, mallocFunction, paras));
        //add bitcast instruction
        Register castResult = new Register(llvMtype, "castResult");
        currentFunction.registerVar(castResult.getName(),castResult);
        currentBlock.addInst(new BitCastInst(currentBlock, mallocResult, castResult.getLlvMtype(), castResult));
        node.setResult(castResult);
        node.setAllocAddr(null);
        if(astType.getConstructor().getCategory() != Function.Category.defaultConstructor){
            String constructorName = ((ClassType) astType).getName() + "$" + ((ClassType) astType).getName();
            LLVMfunction constructor = module.getFunctionMap().get(constructorName);
            assert constructor != null;
            ArrayList<Operand> constructorParas = new ArrayList<Operand>();
            constructorParas.add(castResult);                       //gugu changed: why?
            currentBlock.addInst(new CallInst(currentBlock, null, constructor, constructorParas));
        }
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
                    currentFunction.registerVar(pointer.getName(), pointer);
                    currentBlock.addInst(new BitCastInst(currentBlock, faResult, pointer.getLlvMtype(), pointer));
                }else{
                    pointer = (Register) faResult;
                }
                //add GEP instruction
                ArrayList<Operand> indexs = new ArrayList<>();
                indexs.add(new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), -1));
                Register GEPresult = new Register(pointer.getLlvMtype(), "GEP$result");
                currentFunction.registerVar(GEPresult.getName(), GEPresult);
                currentBlock.addInst(new GEPInst(currentBlock, pointer, indexs, GEPresult));
                //add load instruction
                Register sizeResult = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "size$result");
                currentFunction.registerVar(sizeResult.getName(),sizeResult);
                currentBlock.addInst(new LoadInst(currentBlock, GEPresult, sizeResult));
                node.setResult(sizeResult);
                node.setAllocAddr(null);
            }else{
                if(faType instanceof StringType){
                    function = module.getBuiltInFunctionMap().get("__string_" + name);
                }else{
                    assert faType instanceof ClassType;
                    function = module.getFunctionMap().get(((ClassType) faType).getName() + "$" + name);
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
                    currentBlock.addInst(new CallInst(currentBlock, null, function, paras));
                    node.setResult(null);
                    node.setAllocAddr(null);
                } else{
                    Register result = new Register(returnType, "call$result");
                    currentFunction.registerVar(result.getName(), result);
                    currentBlock.addInst(new CallInst(currentBlock, result, function, paras));
                    node.setResult(result);
                    node.setAllocAddr(null);
                }

            }
        }else{
            assert funcSelf instanceof IdExprNode;
            String name = ((IdExprNode) funcSelf).getId();
            if(!module.getFunctionMap().containsKey(name) && !module.getBuiltInFunctionMap().containsKey(name)){
                //method
                function = module.getFunctionMap().get(inClassName + "$" + name);
                assert function != null;
                Register thisAddr = currentFunction.getThisAddr();
                assert thisAddr.getLlvMtype() instanceof LLVMPointerType;
                Register thisValue = new Register(((LLVMPointerType) thisAddr.getLlvMtype()).getBaseType(), "this");
                currentFunction.registerVar(thisValue.getName(), thisValue);
                currentBlock.addInst(new LoadInst(currentBlock, thisAddr, thisValue));
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
                    currentBlock.addInst(new CallInst(currentBlock, null, function, paras));
                    node.setResult(null);
                    node.setAllocAddr(null);
                } else{
                    Register result = new Register(returnType, "call$result");
                    currentFunction.registerVar(result.getName(), result);
                    currentBlock.addInst(new CallInst(currentBlock, result, function, paras));
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
                    currentBlock.addInst(new CallInst(currentBlock, null, function, paras));
                    node.setResult(null);
                    node.setAllocAddr(null);
                } else{
                    Register result = new Register(returnType, "call$result");
                    currentFunction.registerVar(result.getName(), result);
                    currentBlock.addInst(new CallInst(currentBlock, result, function, paras));
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
        currentFunction.registerVar(GepResult.getName(), GepResult);
        currentBlock.addInst(new GEPInst(currentBlock, arrayValue, indexs, GepResult));
        //add load instructions
        assert GepResult.getLlvMtype() instanceof LLVMPointerType;
        Register loadResult = new Register(((LLVMPointerType) GepResult.getLlvMtype()).getBaseType(), "load$result");
        currentFunction.registerVar(loadResult.getName(), loadResult);
        currentBlock.addInst(new LoadInst(currentBlock, GepResult, loadResult));
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
                currentFunction.registerVar(result.getName(), result);
                currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.add,
                        exprResult, new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 1), result));
                currentBlock.addInst(new StoreInst(currentBlock, result, exprAddr));
                node.setResult(result);
                node.setAllocAddr(exprAddr);
                break;
            }
            case"--":{
                Operand exprAddr = node.getExpr().getAllocAddr();
                Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "preDecrease");
                currentFunction.registerVar(result.getName(), result);
                currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.sub,
                        exprResult, new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 1), result));
                currentBlock.addInst(new StoreInst(currentBlock, result, exprAddr));
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
                    currentFunction.registerVar(result.getName(), result);
                    currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.sub,
                            new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), 0), exprResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                }
                break;
            }
            case"!":{
                Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "notValue");
                currentFunction.registerVar(result.getName(),result);
                currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.xor,
                        new ConstBool(true), exprResult, result));
                node.setResult(result);
                node.setAllocAddr(null);
                break;
            }
            case"~":{
                Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "bitwiseValue");
                currentFunction.registerVar(result.getName(),result);
                currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.xor,
                        new ConstInt(new LLVMIntType(LLVMIntType.BitWidth.int32), -1), exprResult, result));
                node.setResult(result);
                node.setAllocAddr(null);
                break;
            }
        }
    }

    @Override
    public void visit(BinaryExprNode node) throws CompileError {    //gugu changed: the order can be reorganized e.g. string specific area
        String op = node.getOp();
        if(!op.equals("&&") && !op.equals("||")){
            //for int
            ExprNode lhs = node.getLhs();
            ExprNode rhs = node.getRhs();
            lhs.accept(this);
            rhs.accept(this);
            Operand lhsResult = lhs.getResult();
            Operand rhsResult = rhs.getResult();
            switch (op){
                case"*": case "/": case "%": case "-": case "<<": case ">>": case "&": case "^" : case "|":{
                    String registerId = null;
                    BinaryOpInst.BinaryOpName binaryOpName = null;
                    if(op.equals("*")){registerId = "mulValue"; binaryOpName = BinaryOpInst.BinaryOpName.mul;}
                    else if(op.equals("/")){registerId = "divValue"; binaryOpName = BinaryOpInst.BinaryOpName.sdiv;}
                    else if(op.equals("%")){registerId = "modValue"; binaryOpName = BinaryOpInst.BinaryOpName.srem;}
                    else if(op.equals("-")){registerId = "subValue"; binaryOpName = BinaryOpInst.BinaryOpName.sub;}
                    else if(op.equals("<<")){registerId = "shiftLeftValue"; binaryOpName = BinaryOpInst.BinaryOpName.shl;}
                    else if(op.equals(">>")){registerId = "shiftRightValue"; binaryOpName = BinaryOpInst.BinaryOpName.ashr;}
                    else if(op.equals("&")){registerId = "bitwiseAndValue"; binaryOpName = BinaryOpInst.BinaryOpName.and;}
                    else if(op.equals("^")){registerId = "bitwiseXorValue"; binaryOpName = BinaryOpInst.BinaryOpName.xor;}
                    else if(op.equals("|")){registerId = "bitwiseOrValue"; binaryOpName = BinaryOpInst.BinaryOpName.or;}
                    if (registerId == null) throw new AssertionError();
                    if (binaryOpName == null) throw new AssertionError();
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), registerId);
                    currentFunction.registerVar(result.getName(),result);
                    currentBlock.addInst(new BinaryOpInst(currentBlock, binaryOpName,
                            lhsResult, rhsResult, result));
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case "+":{
                    if(rhs.getExprType() instanceof IntType){
                        assert lhs.getExprType() instanceof IntType;
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int32), "add");
                        currentFunction.registerVar(result.getName(),result);
                        currentBlock.addInst(new BinaryOpInst(currentBlock, BinaryOpInst.BinaryOpName.add,
                                lhsResult, rhsResult, result));
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else{
                        assert lhs.getExprType() instanceof StringType;
                        assert rhs.getExprType() instanceof StringType;
                        LLVMfunction function = module.getBuiltInFunctionMap().get("__string_concatenate");
                        //init para
                        ArrayList<Operand> paras = new ArrayList<Operand>();
                        paras.add(lhsResult);
                        paras.add(rhsResult);
                        //call inst
                        Register result = new Register(new LLVMPointerType(
                                new LLVMIntType(LLVMIntType.BitWidth.int8)), "stringAddResult");
                        currentFunction.registerVar(result.getName(), result);
                        currentBlock.addInst(new CallInst(currentBlock, result, function, paras));
                        //modify node
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }
                }
                case "<": case ">" : case "<=" : case ">=":{
                    if(node.getRhs().getExprType() instanceof IntType){
                        String registeredId = null;
                        IcmpInst.IcmpName icmpName = null;
                        if(op.equals("<")){registeredId = "lessThen"; icmpName = IcmpInst.IcmpName.slt;}
                        else if(op.equals(">")){registeredId = "greaterThen"; icmpName = IcmpInst.IcmpName.sgt;}
                        else if(op.equals("<=")){registeredId = "lessEqual"; icmpName = IcmpInst.IcmpName.sle;}
                        else if(op.equals(">=")){registeredId = "greaterEqual"; icmpName = IcmpInst.IcmpName.sge;}
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), registeredId);
                        currentFunction.registerVar(result.getName(),result);
                        currentBlock.addInst(new IcmpInst(currentBlock, icmpName,
                                new LLVMIntType(LLVMIntType.BitWidth.int32),
                                lhsResult, rhsResult, result));
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else{
                        assert lhs.getExprType() instanceof StringType;
                        assert rhs.getExprType() instanceof StringType;
                        String registeredId = null;
                        String funtionName = null;
                        if(op.equals("<")){registeredId = "stringLessThanResult"; funtionName = "__string_lessThan";}
                        else if(op.equals(">")){registeredId = "stringGreaterThanResult"; funtionName = "__string_greaterThan";}
                        else if(op.equals("<=")){registeredId = "stringLessEqualResult"; funtionName = "__string_lessEqual";}
                        else if(op.equals(">=")){registeredId = "stringGreaterEqual"; funtionName = "__string_greaterEqual";}
                        LLVMfunction function = module.getBuiltInFunctionMap().get(funtionName);
                        //init para
                        ArrayList<Operand> paras = new ArrayList<Operand>();
                        paras.add(lhsResult);
                        paras.add(rhsResult);
                        //call inst
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), registeredId);
                        currentFunction.registerVar(result.getName(), result);
                        currentBlock.addInst(new CallInst(currentBlock, result, function, paras));
                        //modify node
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }
                }
                case "==":{
                    Type ltype = lhs.getExprType();
                    Type rtype = rhs.getExprType();
                    if(ltype instanceof IntType && rtype instanceof IntType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "equal");
                        currentFunction.registerVar(result.getName(), result);
                        currentBlock.addInst(new IcmpInst(currentBlock, IcmpInst.IcmpName.eq,
                                new LLVMIntType(LLVMIntType.BitWidth.int32), lhsResult, rhsResult, result));

                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else if(ltype instanceof BoolType && rtype instanceof BoolType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "equal");
                        currentFunction.registerVar(result.getName(), result);
                        currentBlock.addInst(new IcmpInst(currentBlock, IcmpInst.IcmpName.eq,
                                new LLVMIntType(LLVMIntType.BitWidth.int1), lhsResult, rhsResult, result));

                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else if(ltype instanceof StringType && rtype instanceof StringType){
                        LLVMfunction mfunction = module.getBuiltInFunctionMap().get("__string_equal");
                        //init paras
                        ArrayList<Operand> paras = new ArrayList<Operand>();
                        paras.add(lhsResult);
                        paras.add(rhsResult);
                        //call inst
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "stringEqualResult");
                        currentFunction.registerVar(result.getName(), result);
                        currentBlock.addInst(new CallInst(currentBlock, result, mfunction, paras));
                        //modify node
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else if((ltype instanceof ArrayType && rtype instanceof NullType)
                            || (ltype instanceof NullType && rtype instanceof ArrayType)
                            || (ltype instanceof ClassType && rtype instanceof NullType)
                            || (ltype instanceof NullType && rtype instanceof ClassType)){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "equalResult");
                        currentFunction.registerVar(result.getName(), result);
                        LLVMtype compareType = rtype instanceof NullType ? lhsResult.getLlvMtype() : rhsResult.getLlvMtype();
                        currentBlock.addInst(new IcmpInst(currentBlock, IcmpInst.IcmpName.eq, compareType,
                                lhsResult, rhsResult, result));

                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else if(ltype instanceof NullType && rtype instanceof  NullType){
                        node.setResult(new ConstBool(true));
                        node.setLvalue(null);
                        break;
                    }
                }
                case "!=":{
                    Type ltype = lhs.getExprType();
                    Type rtype = rhs.getExprType();
                    if(ltype instanceof IntType && rtype instanceof IntType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "notEqual");
                        currentFunction.registerVar(result.getName(), result);
                        currentBlock.addInst(new IcmpInst(currentBlock, IcmpInst.IcmpName.ne,
                                new LLVMIntType(LLVMIntType.BitWidth.int32), lhsResult, rhsResult, result));

                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else if(ltype instanceof BoolType && rtype instanceof BoolType){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "notEqual");
                        currentFunction.registerVar(result.getName(), result);
                        currentBlock.addInst(new IcmpInst(currentBlock, IcmpInst.IcmpName.ne,
                                new LLVMIntType(LLVMIntType.BitWidth.int1), lhsResult, rhsResult, result));

                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else if(ltype instanceof StringType && rtype instanceof StringType){
                        LLVMfunction mfunction = module.getBuiltInFunctionMap().get("__string_notEqual");
                        //init paras
                        ArrayList<Operand> paras = new ArrayList<Operand>();
                        paras.add(lhsResult);
                        paras.add(rhsResult);
                        //call inst
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "stringNotEqualResult");
                        currentFunction.registerVar(result.getName(), result);
                        currentBlock.addInst(new CallInst(currentBlock, result, mfunction, paras));
                        //modify node
                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else if((ltype instanceof ArrayType && rtype instanceof NullType)
                            || (ltype instanceof NullType && rtype instanceof ArrayType)
                            || (ltype instanceof ClassType && rtype instanceof NullType)
                            || (ltype instanceof NullType && rtype instanceof ClassType)){
                        Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "notEqualResult");
                        currentFunction.registerVar(result.getName(), result);
                        LLVMtype compareType = rtype instanceof NullType ? lhsResult.getLlvMtype() : rhsResult.getLlvMtype();
                        currentBlock.addInst(new IcmpInst(currentBlock, IcmpInst.IcmpName.ne, compareType,
                                lhsResult, rhsResult, result));

                        node.setResult(result);
                        node.setAllocAddr(null);
                        break;
                    }else if(ltype instanceof NullType && rtype instanceof  NullType){
                        node.setResult(new ConstBool(false));
                        node.setLvalue(null);
                        break;
                    }
                }
                default:assert false;
            }

        }else{
            switch (op){
                case "&&":{
                    ExprNode lhs = node.getLhs();
                    ExprNode rhs = node.getRhs();
                    Block branchBlock = new Block("logicalAndBranch", currentFunction);
                    Block mergeBlock = new Block("logicalAndMerge", currentFunction);
                    currentFunction.registerBlock(branchBlock.getName(), branchBlock);
                    currentFunction.registerBlock(mergeBlock.getName(), mergeBlock);

                    //original block
                    lhs.accept(this);
                    Operand lhsResult = lhs.getResult();
                    currentBlock.addInst(new BranchInst(currentBlock, lhsResult, branchBlock, mergeBlock));

                    Block originalBlock = currentBlock;

                    //branchBlock
                    currentBlock = branchBlock;
                    rhs.accept(this);
                    Operand rhsResult = rhs.getResult();
                    currentBlock.addInst(new BranchInst(currentBlock, null, mergeBlock, null));

                    //mergeBlock
                    currentBlock = mergeBlock;
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "logicAnd");
                    currentFunction.registerVar(result.getName(), result);
                    Set<Pair<Operand, Block>>branches = new LinkedHashSet<>();  //gugu changde: container can be modified
                    branches.add(new Pair<>(new ConstBool(false), originalBlock));
                    branches.add(new Pair<>(rhsResult, branchBlock));
                    currentBlock.addInst(new PhiInst(currentBlock, branches, result));

                    //modify Node
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                case "||":{
                    ExprNode lhs = node.getLhs();
                    ExprNode rhs = node.getRhs();
                    Block branchBlock = new Block("logicalOrBranch", currentFunction);
                    Block mergeBlock = new Block("logicalOrMerge", currentFunction);
                    currentFunction.registerBlock(branchBlock.getName(), branchBlock);
                    currentFunction.registerBlock(mergeBlock.getName(), mergeBlock);

                    //original block
                    lhs.accept(this);
                    Operand lhsResult = lhs.getResult();
                    currentBlock.addInst(new BranchInst(currentBlock, lhsResult, mergeBlock, branchBlock));

                    Block originalBlock = currentBlock;

                    //branchBlock
                    currentBlock = branchBlock;
                    rhs.accept(this);
                    Operand rhsResult = rhs.getResult();
                    currentBlock.addInst(new BranchInst(currentBlock, null, mergeBlock, null));

                    //mergeBlock
                    currentBlock = mergeBlock;
                    Register result = new Register(new LLVMIntType(LLVMIntType.BitWidth.int1), "logicOr");
                    currentFunction.registerVar(result.getName(), result);
                    Set<Pair<Operand, Block>>branches = new LinkedHashSet<>();  //gugu changde: container can be modified
                    branches.add(new Pair<>(new ConstBool(true), originalBlock));
                    branches.add(new Pair<>(rhsResult, branchBlock));
                    currentBlock.addInst(new PhiInst(currentBlock, branches, result));

                    //modify Node
                    node.setResult(result);
                    node.setAllocAddr(null);
                    break;
                }
                default:assert false;
            }
        }
    }

    @Override
    public void visit(AssignExprNode node) throws CompileError {
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        Operand rhsResult = node.getRhs().getResult();
        Operand lhsAddr = node.getLhs().getAllocAddr();
        currentBlock.addInst(new StoreInst(currentBlock, rhsResult, lhsAddr));
        node.setResult(rhsResult);
        node.setAllocAddr(null);//gugu changed ????
    }

    public Module getModule() {
        return module;
    }
}
