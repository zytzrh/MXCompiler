package Semantic.ASTtype.NonArray;

import AST.Function.Function;
import Semantic.ASTtype.Type;

import java.util.HashMap;

public class NullType extends NonArrayType {

    public NullType() {
        super("null", new HashMap<String, Type>(), new HashMap<String, Function>());
    }
}
