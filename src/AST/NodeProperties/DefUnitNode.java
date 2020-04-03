package AST.NodeProperties;

import AST.Location.Location;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;

abstract public class DefUnitNode extends ASTNode{
    public DefUnitNode(String text, Location location) {
        super(text, location);
    }

}
//classDefNode funcDefNode varDefNode