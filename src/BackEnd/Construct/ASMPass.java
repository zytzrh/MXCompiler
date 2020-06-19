package BackEnd.Construct;

import BackEnd.ASMModule;

abstract public class ASMPass {
    protected ASMModule ASMModule;

    public ASMPass(ASMModule ASMModule) {
        this.ASMModule = ASMModule;
    }

    abstract public void run();
}
