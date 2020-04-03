package AST;

import AST.Location.Location;
import AST.NodeProperties.ASTNode;
import AST.NodeProperties.DefUnitNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

import java.util.ArrayList;

public class ProgramNode extends ASTNode {
    private ArrayList<DefUnitNode> defUnits;

    public ProgramNode(String text, Location location, ArrayList<DefUnitNode> defUnits) {
        super(text, location);
        this.defUnits = defUnits;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public ArrayList<DefUnitNode> getDefUnits() {
        return defUnits;
    }
}
