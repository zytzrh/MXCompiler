package Semantic.ASTtype.NonArray;

import AST.Function.Function;
import Semantic.ASTtype.Type;

import java.util.HashMap;

public class VoidType extends NonArrayType {

    public VoidType() {
        super("void", new HashMap<String, Type>(), new HashMap<String, Function>());
    }
}
