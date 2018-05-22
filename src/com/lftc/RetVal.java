package com.lftc;

public class RetVal {
    private SymType type;
    private boolean isLVal;
    private boolean isCtVal;
    private CtVal ctVal;

    public RetVal(SymType type, boolean isLVal, boolean isCtVal, CtVal ctVal) {
        this.type = type;
        this.isLVal = isLVal;
        this.isCtVal = isCtVal;
        this.ctVal = ctVal;
    }
}
