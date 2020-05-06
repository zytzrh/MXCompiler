package Semantic.ASTtype.NonArray;

import AST.Function.Function;
import Semantic.ASTtype.Type;

import java.util.HashMap;

public class ClassType extends NonArrayType {

    public ClassType(String name) {
        super(name, new HashMap<String, Type>(), new HashMap<String, Function>());
    }

    public ClassType(String name, HashMap<String, Type> varMember) {
        super(name, varMember, new HashMap<String, Function>());
    }

}
