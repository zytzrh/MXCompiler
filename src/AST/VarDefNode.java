package AST;

import AST.Location.Location;
import AST.NodeProperties.DefUnitNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

import java.util.ArrayList;

public class VarDefNode extends DefUnitNode {
    private ArrayList<VarDefOneNode> varDefs;

    public VarDefNode(String text, Location location, ArrayList<VarDefOneNode> varDefs) {
        super(text, location);
        this.varDefs = varDefs;
    }

    public ArrayList<VarDefOneNode> getVarDefs() {
        return this.varDefs;
    }

    public void setVarDefs(ArrayList<VarDefOneNode> varDefs) {
        this.varDefs = varDefs;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }
}
