package AST;

import AST.Location.Location;
import AST.NodeProperties.DefUnitNode;
import AST.NodeProperties.TypeNode;
import AST.VariableEntity.VariableEntity;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

import java.util.ArrayList;


public class FuncDefNode extends DefUnitNode {
    private TypeNode returnType;    //returnType is null when void occur
    private ArrayList<FormalParaNode> paras;
    private String funcName;
    private BlockNode funcBody;

    private ArrayList<VariableEntity> varMembersEntity;       //if in class, contain class variableEntity


    public FuncDefNode(String text, Location location, TypeNode returnType, ArrayList<FormalParaNode> paras,
                       String funcName, BlockNode funcBody) {
        super(text, location);
        this.returnType = returnType;
        this.paras = paras;
        this.funcName = funcName;
        this.funcBody = funcBody;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public ArrayList<FormalParaNode> getParas() {
        return paras;
    }

    public String getFuncName() {
        return funcName;
    }

    public BlockNode getFuncBody() {
        return funcBody;
    }

    public ArrayList<VariableEntity> getVarMembersEntity() {
        return varMembersEntity;
    }

    public void setVarMembersEntity(ArrayList<VariableEntity> varMembersEntity) {
        this.varMembersEntity = varMembersEntity;
    }
}
