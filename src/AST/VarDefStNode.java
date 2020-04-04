package AST;

import AST.Location.Location;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class VarDefStNode extends StatementNode {
    private VarDefNode varDef;

    public VarDefStNode(String text, Location location, VarDefNode vafDef) {
        super(text, location);
        this.varDef = vafDef;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public VarDefNode getVarDef() {
        return varDef;
    }

    public void setVarDef(VarDefNode varDef) {
        this.varDef = varDef;
    }
}
