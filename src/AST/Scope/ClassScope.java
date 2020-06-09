package AST.Scope;

import Semantic.ASTtype.NonArray.ClassType;

public class ClassScope extends Scope {
    ClassType classType;

    public ClassScope(ClassType classType) {
        this.classType = classType;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }
}
