package AST.Function;

import AST.BlockNode;
import AST.VariableEntity.VariableEntity;
import Semantic.ASTtype.Type;

import java.util.ArrayList;

public class Function {
    private Type returnType;
    private ArrayList<VariableEntity> paras;
    private BlockNode funcBody;    //null when the function is in_built | default constructor | arraySize function |
    public Function(Type returnType, ArrayList<VariableEntity> paras, BlockNode funcBody){
        this.returnType = returnType;
        this.paras = paras;
        this.funcBody = funcBody;
    }

    public void addPara(VariableEntity variableEntity){
        paras.add(variableEntity);
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public ArrayList<VariableEntity> getParas() {
        return paras;
    }

    public void setParas(ArrayList<VariableEntity> paras) {
        this.paras = paras;
    }

    public BlockNode getFuncBody() {
        return funcBody;
    }

    public void setFuncBody(BlockNode funcBody) {
        this.funcBody = funcBody;
    }
}
