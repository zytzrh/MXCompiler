package Type.NonArray;

import AST.Function.Function;
import Type.Type;

import java.util.HashMap;

public class VoidType extends NonArrayType {

    public VoidType() {
        super("void", new HashMap<String, Type>(), new HashMap<String, Function>());
    }
}
