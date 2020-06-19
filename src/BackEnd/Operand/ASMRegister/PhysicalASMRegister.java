package BackEnd.Operand.ASMRegister;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PhysicalASMRegister extends ASMRegister {          //gugu changed: too beautiful
    static public String[] prNames = {
            "zero", "ra", "sp", "gp", "tp",
            "t0", "t1", "t2", "s0", "s1",
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
            "t3", "t4", "t5", "t6"};
    static public String[] callerSavePRNames = {
            "ra", "t0", "t1", "t2",
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "t3", "t4", "t5", "t6"
    };
    static public String[] calleeSavePRNames = {
            "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11"        //s0 is also the fp
    };
    static public String[] allocatablePRNames = {
            // Except zero, sp, gp and tp.
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
            "t0", "t1", "t2", "t3", "t4", "t5", "t6",
            "ra"
    };

    static public Map<String, PhysicalASMRegister> allPRs;
    static public Map<String, PhysicalASMRegister> callerSavePRs;
    static public Map<String, PhysicalASMRegister> calleeSavePRs;
    static public Map<String, PhysicalASMRegister> allocatablePRs;

    static public Map<String, VirtualASMRegister> vrs;
    static public VirtualASMRegister zeroVR;
    static public VirtualASMRegister returnAddressVR;
    static public VirtualASMRegister returnValueVR;
    static public ArrayList<VirtualASMRegister> argVR;
    static public ArrayList<VirtualASMRegister> calleeSaveVRs;

    static {
        //init PR set
        allPRs = new LinkedHashMap<>();
        callerSavePRs = new LinkedHashMap<>();
        calleeSavePRs = new LinkedHashMap<>();
        allocatablePRs = new LinkedHashMap<>();
        for (String name : prNames)
            allPRs.put(name, new PhysicalASMRegister(name));

        for (String name : callerSavePRNames)
            callerSavePRs.put(name, allPRs.get(name));
        for (String name : calleeSavePRNames)
            calleeSavePRs.put(name, allPRs.get(name));
        for (String name : allocatablePRNames)
            allocatablePRs.put(name, allPRs.get(name));

        //init VR set
        vrs = new LinkedHashMap<>();
        for (String name : prNames) {
            VirtualASMRegister vr = new VirtualASMRegister("." + name);
            vr.fixColor(allPRs.get(name));
            vrs.put(name, vr);
        }

        zeroVR = vrs.get("zero");
        returnAddressVR = vrs.get("ra");
        returnValueVR = vrs.get("a0");
        argVR = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            argVR.add(vrs.get("a" + i));
        calleeSaveVRs = new ArrayList<>();
        for (String name : calleeSavePRNames)
            calleeSaveVRs.add(vrs.get(name));
    }


    private final String name;

    public PhysicalASMRegister(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String emitCode() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
