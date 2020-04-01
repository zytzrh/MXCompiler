package AST;

import AST.NodeProperties.ASTNode;
import AST.NodeProperties.TypeNode;

import java.util.List;

public class ConstructDefNode extends ASTNode {
    private String classTypeId;
    private List<FormalParaNode> paras;
    private BlockNode func_body;

    public ConstructDefNode(String text, Location location, String classTypeId, List<FormalParaNode> paras,
                            BlockNode func_body) {
        super(text, location);
        this.classTypeId = classTypeId;
        this.func_body = func_body;
        this.paras = paras;
    }

    public String getClassTypeId(){
        return this.classTypeId;
    }
}
