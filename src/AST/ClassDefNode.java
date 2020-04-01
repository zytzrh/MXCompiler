package AST;

import AST.FuncDefNode;
import AST.NodeProperties.DefUnitNode;
import AST.VarDefOneNode;

import java.util.ArrayList;

public class ClassDefNode extends DefUnitNode {
    private String class_name;
    private ArrayList<VarDefOneNode> varMembers;
    private ArrayList<FuncDefNode> funcMembers;
    private ConstructDefNode constructor;

    public ClassDefNode(String text, Location location, String class_name, ArrayList<VarDefOneNode> varMembers,
                        ArrayList<FuncDefNode> funcMembers, ConstructDefNode constructor) {
        super(text, location);
        this.class_name = class_name;
        this.varMembers = varMembers;
        this.funcMembers = funcMembers;
        this.constructor = constructor;
    }
}
