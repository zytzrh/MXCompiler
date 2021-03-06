package AST;

import AST.Location.Location;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class ContinueNode extends StatementNode {

    public ContinueNode(String text, Location location) {
        super(text, location);
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }
}
