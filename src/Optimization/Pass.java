package Optimization;

import IR.Module;

abstract public class Pass {
    protected Module module;
    protected boolean changed;

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

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
