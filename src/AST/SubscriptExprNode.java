package AST;

import AST.Location.Location;
import AST.NodeProperties.ExprNode;
import AST.Visit.ASTVisitor;
import Semantic.ExceptionHandle.CompileError;

public class SubscriptExprNode extends ExprNode {
    private ExprNode arrayName;
    private ExprNode index;

    public SubscriptExprNode(String text, Location location, ExprNode arrayName, ExprNode index) {
        super(text, location);
        this.arrayName = arrayName;
        this.index = index;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }

    public ExprNode getArrayName() {
        return arrayName;
    }

    public void setArrayName(ExprNode arrayName) {
        this.arrayName = arrayName;
    }

    public ExprNode getIndex() {
        return index;
    }

    public void setIndex(ExprNode index) {
        this.index = index;
    }
}
