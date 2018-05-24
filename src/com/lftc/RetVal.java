package com.lftc;

public class RetVal {
    private SymType type;
    private boolean isLVal;
    private boolean isCtVal;
    private CtVal ctVal;

    public RetVal(SymType type, CtVal ctVal, boolean isLVal, boolean isCtVal) {
        this.type = type;
        this.isLVal = isLVal;
        this.isCtVal = isCtVal;
        this.ctVal = ctVal;
    }

    public RetVal(SymType type, boolean isLVal, boolean isCtVal) {
        this.type = type;
        this.isLVal = isLVal;
        this.isCtVal = isCtVal;
        this.ctVal = null;
    }

    public boolean isCtVal() {
        return this.isCtVal;
    }

    public SymType getType() {
        return this.type;
    }

    public CtVal getCtVal() {
        return this.ctVal;
    }

    public boolean isLVal() {
        return this.isLVal;
    }

    public void setLVal(boolean b) {
        this.isLVal = b;
    }

    public void setCtVal(boolean b) {
        this.isCtVal = b;
    }

    public void setType(SymType type) {
        this.type = type;
    }
}
