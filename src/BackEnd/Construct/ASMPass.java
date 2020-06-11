package BackEnd.Construct;

import BackEnd.RISCVModule;

abstract public class ASMPass {
    protected RISCVModule RISCVModule;

    public ASMPass(BackEnd.RISCVModule RISCVModule) {
        this.RISCVModule = RISCVModule;
    }

    abstract public void run();
}
