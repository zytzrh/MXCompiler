package AST;

import AST.Location.Location;
import AST.NodeProperties.ASTNode;

import java.util.ArrayList;

public class ConstructDefNode extends ASTNode {
    private String classTypeId;
    private ArrayList<FormalParaNode> paras;
    private BlockNode funcBody;

    public ConstructDefNode(String text, Location location, String classTypeId, ArrayList<FormalParaNode> paras,
                            BlockNode funcBody) {
        super(text, location);
        this.classTypeId = classTypeId;
        this.funcBody = funcBody;
        this.paras = paras;
    }

    public String getClassTypeId(){
        return this.classTypeId;
    }

    public void setClassTypeId(String classTypeId) {
        this.classTypeId = classTypeId;
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
