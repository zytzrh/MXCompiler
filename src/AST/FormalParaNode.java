package AST;

import AST.Location.Location;
import AST.NodeProperties.ASTNode;
import AST.NodeProperties.TypeNode;

public class FormalParaNode extends ASTNode {   //not accessible
    private TypeNode paraType;
    private String paraName;


    public FormalParaNode(String text, Location location, TypeNode paraType, String paraName) {
        super(text, location);
        this.paraType = paraType;
        this.paraName = paraName;
    }

    public TypeNode getParaType() {
        return paraType;
    }

    public String getParaName() {
        return paraName;
    }
}
