package Semantic.ExceptionHandle;

import AST.Location.Location;

public class CompileError extends Exception {
    private Location location;
    private String msg;

    public CompileError(Location location, String msg){
        this.location = location;
        this.msg = msg;
    }

    public CompileError(){}

    public void setLocation(Location location){
        if(this.location == null)
            this.location = location;
    }

    public Location getLocation(){
        return this.location;
    }

    public String getMsg(){
        return this.msg;
    }
}
