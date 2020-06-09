package Optimization;

import IR.Module;

abstract public class Pass {
    protected Module module;
    protected boolean changded;

    public Pass(Module module) {        //gugu changed: the para of constructor
        this.module = module;
    }

    abstract public boolean run();

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean isChangded() {
        return changded;
    }

    public void setChangded(boolean changded) {
        this.changded = changded;
    }
}
