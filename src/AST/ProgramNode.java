package AST;

import AST.NodeProperties.ASTNode;
import AST.NodeProperties.DefUnitNode;

import java.util.ArrayList;

public class ProgramNode extends ASTNode {
    ArrayList<DefUnitNode> defUnits;

    public ProgramNode(String text, Location location, ArrayList<DefUnitNode> defUnits) {
        super(text, location);
        this.defUnits = defUnits;
    }
}
