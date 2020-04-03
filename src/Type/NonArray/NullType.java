package Type.NonArray;

import AST.Function.Function;
import Type.Type;

import java.util.HashMap;

public class NullType extends NonArrayType {

    public NullType() {
        super("null", new HashMap<String, Type>(), new HashMap<String, Function>());
    }
}
