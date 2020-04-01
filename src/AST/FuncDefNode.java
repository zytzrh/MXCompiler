package AST;

import AST.NodeProperties.DefUnitNode;
import AST.NodeProperties.StatementNode;
import AST.NodeProperties.TypeNode;

import java.util.List;


public class FuncDefNode extends DefUnitNode {
    private TypeNode returnType;    //returnType is null when void occur
    private List<FormalParaNode> paras;
    private String func_name;
    private BlockNode func_body;


    public FuncDefNode(String text, Location location, TypeNode returnType, List<FormalParaNode> paras,
                       String func_name, BlockNode func_body) {
        super(text, location);
        this.returnType = returnType;
        this.paras = paras;
        this.func_name = func_name;
        this.func_body = func_body;
    }
}
