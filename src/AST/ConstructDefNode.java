package AST;

import AST.Location.Location;
import AST.NodeProperties.ASTNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

import java.util.ArrayList;

public class ConstructDefNode extends ASTNode {
    private String className;
    private ArrayList<FormalParaNode> paras;
    private BlockNode funcBody;

    public ConstructDefNode(String text, Location location, String className, ArrayList<FormalParaNode> paras,
                            BlockNode funcBody) {
        super(text, location);
        this.className = className;
        this.funcBody = funcBody;
        this.paras = paras;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public String getClassName(){
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<FormalParaNode> getParas() {
        return paras;
    }

    public void setParas(ArrayList<FormalParaNode> paras) {
        this.paras = paras;
    }

    public BlockNode getFuncBody() {
        return funcBody;
    }

    public void setFuncBody(BlockNode funcBody) {
        this.funcBody = funcBody;
    }
}
