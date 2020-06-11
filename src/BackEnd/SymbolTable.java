package BackEnd;

import BackEnd.Operand.ASMRegister.VirtualASMRegister;

import java.util.*;

public class SymbolTable {
    private Map<String, ArrayList<Object>> symbolTable;

    public SymbolTable() {
        symbolTable = new HashMap<>();
    }


    public void putASM(String name, Object object) {
        assert object instanceof VirtualASMRegister || object instanceof ASMBlock;
        assert !symbolTable.containsKey(name);

        ArrayList<Object> array = new ArrayList<>();
        array.add(object);
        symbolTable.put(name, array);
    }

    public void putASMRename(String name, Object object) {
        // Maybe this method will only be called when loading/storing global variables.
        assert object instanceof VirtualASMRegister;

        int id = 0;
        while (symbolTable.containsKey(name + "." + id))
            id++;
        ((VirtualASMRegister) object).setName(name + "." + id);

        ArrayList<Object> array = new ArrayList<>();
        array.add(object);
        symbolTable.put(name + "." + id, array);
    }

    public boolean contains(String name) {
        return symbolTable.containsKey(name);
    }

    public Object get(String name) {
        ArrayList<Object> arrayList = symbolTable.get(name);
        return arrayList.get(0);
    }

    public VirtualASMRegister getVR(String name) {
        assert symbolTable.containsKey(name);
        return ((VirtualASMRegister) symbolTable.get(name).get(0));
    }

    public Set<VirtualASMRegister> getAllVRs() {
        Set<VirtualASMRegister> VRs = new HashSet<>();
        for (ArrayList<Object> array : symbolTable.values()) {
            assert array.size() == 1;
            assert array.get(0) instanceof VirtualASMRegister;
            if (!((VirtualASMRegister) array.get(0)).getDef().isEmpty())
                VRs.add(((VirtualASMRegister) array.get(0)));
        }
        return VRs;
    }

    public void removeVR(VirtualASMRegister vr) {
        assert symbolTable.containsKey(vr.getName());
        assert symbolTable.get(vr.getName()).get(0) == vr;
        symbolTable.remove(vr.getName());
    }
}