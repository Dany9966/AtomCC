package com.lftc;

import java.util.ArrayList;
import java.util.Arrays;

public class SymType {

    public static ArrayList<String> typebaseList = new ArrayList<String>(Arrays.asList(
            "TB_INT", "TB_DOUBLE", "TB_CHAR", "TB_STRUCT", "TB_VOID"
    ));

    private String typebase;
    private Symbol s;
    private int nElements;

    public SymType(String tb){
        if(typebaseList.contains(tb)) {
            this.typebase = tb;
        } else {
            System.out.println("No such typebase: " + tb);
            System.exit(1);
        }
        this.s = null;
        this.nElements = 0;
    }

    public SymType(String tb, int nElements){
        if(typebaseList.contains(tb)) {
            this.typebase = tb;
        } else {
            System.out.println("No such typebase: " + tb);
            System.exit(1);
        }
        this.s = null;
        this.nElements = nElements;
    }

    public SymType(String tb, Symbol s){
        if(typebaseList.contains(tb)) {
            this.typebase = tb;
        } else {
            System.out.println("No such typebase: " + tb);
            System.exit(1);
        }
        this.s = s;
        this.nElements = 0;
    }

    public void setNElements(int i) {
        this.nElements = i;
    }

    public void setTypeBase(String tb) {
        if(typebaseList.contains(tb)){
            this.typebase = tb;
        }
        else {
            System.out.println("No such typebase: " + tb);
            System.exit(1);
        }
    }

    public void castTo(SymType type){
        if(this.nElements > -1){
            if(type.nElements > -1){
                if(!(this.typebase.equals(type.typebase))){
                    System.out.println("an array cannot be converted to an array of another type");
                    System.exit(1);
                } else {
                    System.out.println("an array cannot be converted to a non-array");
                    System.exit(1);
                }
            } else {
                if(type.nElements > -1){
                    System.out.println("a non-array cannot be converted to an array");
                    System.exit(1);
                }
            }

            switch(this.typebase){
                case "TB_CHAR":
                case "TB_INT":
                case "TB_DOUBLE":
                    switch(type.typebase){
                        case "TB_CHAR":
                        case "TB_INT":
                        case "TB_DOUBLE":
                            return;

                    }
                case "TB_STRUCT":
                    if(type.typebase.equals("TB_STRUCT")){
                        if(this.s != type.s){
                            System.out.println("a structure cannot be converted to another one");
                            System.exit(1);
                        }

                        return;
                    }
            }
        }
        System.out.println("incompatible types");
        System.exit(1);
    }
}
