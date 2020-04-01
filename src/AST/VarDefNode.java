package AST;

import AST.NodeProperties.DefUnitNode;

import java.util.ArrayList;

public class VarDefNode extends DefUnitNode {
    private ArrayList<VarDefOneNode> varDefs;

    public VarDefNode(String text, Location location, ArrayList<VarDefOneNode> varDefs) {
        super(text, location);
        this.varDefs = varDefs;
    }
}
