package AST;

import AST.NodeProperties.*;
import org.w3c.dom.Text;


public class VarDefOneNode extends ASTNode {
    private String id;
    private TypeNode type;
    private ExprNode initValue;

    public VarDefOneNode(String text, Location location, String id, ExprNode initValue){    //without initial type
        super(text, location);
        this.id = id;
        this.initValue = initValue;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }
}
