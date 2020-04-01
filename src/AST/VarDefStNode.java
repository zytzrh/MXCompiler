package AST;

import AST.NodeProperties.StatementNode;

import java.util.ArrayList;

public class VarDefStNode extends StatementNode {
    private VarDefNode varDef;

    public VarDefStNode(String text, Location location, VarDefNode vafDef) {
        super(text, location);
        this.varDef = vafDef;
    }
}
