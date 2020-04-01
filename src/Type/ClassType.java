package Type;

import java.util.HashMap;

public class ClassType extends NonArrayType {

    public ClassType(String name, HashMap<String, Type> varMember) {
        super(name, varMember);
    }
}
