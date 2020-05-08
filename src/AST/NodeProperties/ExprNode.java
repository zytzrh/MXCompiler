package AST.NodeProperties;

import AST.Location.Location;
import AST.VariableEntity.VariableEntity;
import IR.LLVMoperand.Operand;
import Semantic.ASTtype.Type;

abstract public class ExprNode extends ASTNode{
    private Boolean lvalue;
    private Type exprType;
    private VariableEntity variableEntity;  //only used by this and id node

    //IR
    private Operand result;
    private Operand allocAddr;
    public ExprNode(String text, Location location) {
        super(text, location);
    }

    public Boolean getLvalue() {
        return lvalue;
    }

    public void setLvalue(Boolean lvalue) {
        this.lvalue = lvalue;
    }

    public Type getExprType() {
        return exprType;
    }

    public void setExprType(Type exprType) {
        this.exprType = exprType;
    }

    public VariableEntity getVariableEntity() {
        return variableEntity;
    }

    public void setVariableEntity(VariableEntity variableEntity) {
        this.variableEntity = variableEntity;
    }

    public Operand getResult() {
        return result;
    }

    public void setResult(Operand result) {
        this.result = result;
    }

    public Operand getAllocAddr() {
        return allocAddr;
    }

    public void setAllocAddr(Operand allocAddr) {
        this.allocAddr = allocAddr;
    }
}
