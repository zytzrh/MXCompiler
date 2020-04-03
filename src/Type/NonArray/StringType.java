package Type.NonArray;

import AST.Function.Function;
import AST.VariableEntity.VariableEntity;
import Type.Type;

import java.util.ArrayList;
import java.util.HashMap;

public class StringType extends NonArrayType {

    public StringType() {
        super("string", new HashMap<String, Type>(), new HashMap<String, Function>());
        VariableEntity para = new VariableEntity("InitValue", this);
        ArrayList<VariableEntity> paras = new ArrayList<VariableEntity>();
        paras.add(para);
        Function constructor = new Function(this, paras, null);
        super.setConstructor(constructor);
    }
}
