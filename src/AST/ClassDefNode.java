package AST;

import AST.Location.Location;
import AST.NodeProperties.DefUnitNode;

import java.util.ArrayList;

public class ClassDefNode extends DefUnitNode {
    private String className;
    private ArrayList<VarDefOneNode> varMembers;
    private ArrayList<FuncDefNode> funcMembers;
    private ConstructDefNode constructor;

    public ClassDefNode(String text, Location location, String className, ArrayList<VarDefOneNode> varMembers,
                        ArrayList<FuncDefNode> funcMembers, ConstructDefNode constructor) {
        super(text, location);
        this.className = className;
        this.varMembers = varMembers;
        this.funcMembers = funcMembers;
        this.constructor = constructor;
    }

    public String getClassName() {
        return className;
    }

    public ArrayList<VarDefOneNode> getVarMembers() {
        return varMembers;
    }

    public ArrayList<FuncDefNode> getFuncMembers() {
        return funcMembers;
    }

    public ConstructDefNode getConstructor() {
        return constructor;
    }
}
