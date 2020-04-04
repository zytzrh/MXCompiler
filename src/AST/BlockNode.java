package AST;


import AST.Location.Location;
import AST.NodeProperties.StatementNode;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

import java.util.ArrayList;

public class BlockNode extends StatementNode {
    private ArrayList<StatementNode> statements;

    public BlockNode(String text, Location location, ArrayList<StatementNode>statements) {
        super(text, location);
        this.statements = statements;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public ArrayList<StatementNode> getStatements() {
        return statements;
    }

    public void setStatements(ArrayList<StatementNode> statements) {
        this.statements = statements;
    }
}
