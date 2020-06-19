package BackEnd;

import BackEnd.Operand.ASMRegister.VirtualASMRegister;
import BackEnd.Operand.Address.StackLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StackFrame {
    private ASMFunction ASMFunction;

    int size;

    private Map<VirtualASMRegister, StackLocation> spillLocations;


    private ArrayList<StackLocation> formalParaLocs;


    private Map<ASMFunction, ArrayList<StackLocation>> parameterLocation;       //gugu changed:??

    public StackFrame(ASMFunction ASMFunction) {
        this.ASMFunction = ASMFunction;
        size = 0;

        spillLocations = new LinkedHashMap<>();
        formalParaLocs = new ArrayList<>();
        parameterLocation = new HashMap<>();
    }

    public int getSize() {
        return size;
    }

    public Map<VirtualASMRegister, StackLocation> getSpillLocations() {
        return spillLocations;
    }

    public void addFormalParameterLocation(StackLocation stackLocation) {
        formalParaLocs.add(stackLocation);
    }

    public Map<ASMFunction, ArrayList<StackLocation>> getParameterLocation() {
        return parameterLocation;
    }

    public void computeFrameSize() {
        int maxSpilledActualParameter = 0;
        int spilledVRCnt = spillLocations.size();
        for (ArrayList<StackLocation> parameters : parameterLocation.values())
            maxSpilledActualParameter = Integer.max(maxSpilledActualParameter, parameters.size());

        size = maxSpilledActualParameter + spilledVRCnt;

        for (int i = 0; i < formalParaLocs.size(); i++) {
            StackLocation stackLocation = formalParaLocs.get(i);
            stackLocation.setOffset((size + i) * 4);
        }
        int j = 0;
        for (StackLocation stackLocation : spillLocations.values()) {
            stackLocation.setOffset((j + maxSpilledActualParameter) * 4);
            j++;
        }
        for (ArrayList<StackLocation> parameters : parameterLocation.values()) {
            for (int k = 0; k < parameters.size(); k++) {
                StackLocation stackLocation = parameters.get(k);
                stackLocation.setOffset(k * 4);
            }
        }
    }
}

