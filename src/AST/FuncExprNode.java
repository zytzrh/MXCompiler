package AST;

import AST.NodeProperties.ExprNode;

import java.util.ArrayList;

public class FuncExprNode extends ExprNode {
    private ArrayList<ExprNode> paras;
    private ExprNode func_name;

    public FuncExprNode(String text, Location location, ExprNode func_name, ArrayList<ExprNode> paras) {
        super(text, location);
        this.func_name = func_name;
        this.paras = paras;
    }

    public ArrayList<ExprNode> getParas(){
        return this.paras;
    }
}
