package AST;

import AST.NodeProperties.ASTNode;
import AST.NodeProperties.TypeNode;

public class FormalParaNode extends ASTNode {
    private TypeNode paraType;
    private String id;


    public FormalParaNode(String text, Location location, TypeNode paraType, String id) {
        super(text, location);
        this.paraType = paraType;
        this.id = id;
    }
}
