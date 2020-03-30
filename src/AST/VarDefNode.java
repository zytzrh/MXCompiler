package AST;

import AST.NodeProperties.*;
import org.w3c.dom.Text;


public class VarDefNode extends DefUnitNode {
    private String id;
    private TypeNode type;
    private ExprNode initValue;

    public VarDefNode(String text, Location location, String id, ExprNode initValue){    //without initial type
        super(text, location);
        this.id = id;
        this.initValue = initValue;
    }
}
