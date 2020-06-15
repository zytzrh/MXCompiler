package Optimization.ConstOptim;

import IR.LLVMoperand.Operand;

public class OpStatus {
    public enum Status {
        undefined, constant, multiDefined
    }

    private Status status;
    private Operand operand;

    public OpStatus(Status status, Operand operand) {
        this.status = status;
        this.operand = operand;
    }

    public Status getStatus() {
        return status;
    }

    public Operand getOperand() {
        return operand;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setOperand(Operand operand) {
        this.operand = operand;
    }

    @Override
    public String toString() {
        if (status == Status.constant)
            return "constant " + operand.toString();
        else
            return status.name();
    }
}
