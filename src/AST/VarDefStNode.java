package AST;

import AST.Location.Location;
import AST.NodeProperties.StatementNode;

public class VarDefStNode extends StatementNode {
    private VarDefNode varDef;

    public VarDefStNode(String text, Location location, VarDefNode vafDef) {
        super(text, location);
        this.varDef = vafDef;
    }
}
