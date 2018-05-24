package com.lftc;

public class CtVal {
    private long i;
    private double d;
    private String str;

    public CtVal(long i){
        this.i = i;
        this.d = 0;
        this.str = null;
    }

    public CtVal(double d){
        this.i = 0;
        this.d = d;
        this.str = null;
    }

    public CtVal(String str){
        this.i = 0;
        this.d = 0;
        this.str = str;
    }

    public long getI() {
        return this.i;
    }
}
