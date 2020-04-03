package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

import java.util.ArrayList;

public class FuncExprNode extends ExprNode {
    private ArrayList<ExprNode> paras;
    private ExprNode funcSelf;

    public FuncExprNode(String text, Location location, ExprNode funcSelf, ArrayList<ExprNode> paras) {
        super(text, location);
        this.funcSelf = funcSelf;
        this.paras = paras;
    }

    public ArrayList<ExprNode> getParas(){
        return this.paras;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public void setParas(ArrayList<ExprNode> paras) {
        this.paras = paras;
    }

    public ExprNode getFuncSelf() {
        return funcSelf;
    }

    public void setFuncSelf(ExprNode funcSelf) {
        this.funcSelf = funcSelf;
    }
}
