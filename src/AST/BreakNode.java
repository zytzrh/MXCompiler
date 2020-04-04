package AST;

import AST.Location.Location;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

public class BreakNode extends StatementNode {

    public BreakNode(String text, Location location) {
        super(text, location);
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }
}
