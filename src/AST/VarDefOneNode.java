package AST;

import AST.Location.Location;
import AST.NodeProperties.*;
import AST.Visit.ASTVisitor;
import ExceptionHandle.CompileError;


public class VarDefOneNode extends ASTNode {
    private String id;
    private TypeNode typeNode;
    private ExprNode initValue;

    public VarDefOneNode(String text, Location location, String id, ExprNode initValue){    //without initial type
        super(text, location);
        this.id = id;
        this.initValue = initValue;
    }

    public void setTypeNode(TypeNode typeNode) {
        this.typeNode = typeNode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TypeNode getTypeNode() {
        return typeNode;
    }

    public ExprNode getInitValue() {
        return initValue;
    }

    public void setInitValue(ExprNode initValue) {
        this.initValue = initValue;
    }

    @Override
    public void accept(ASTVisitor visitor) throws CompileError {
        visitor.visit(this);
    }
}
