package com.lftc;

import java.util.ArrayList;
import java.util.Arrays;

public class Symbol {
    public static ArrayList<String> clsList = new ArrayList<String>(Arrays.asList(
            "CLS_VAR", "CLS_FUNC", "CLS_EXTFUNC", "CLS_STRUCT"
    ));

    public static ArrayList<String> memList = new ArrayList<String>(Arrays.asList(
            "MEM_GLOBAL", "MEM_ARG", "MEM_LOCAL"
    ));

    private String name;
    private String cls;
    private String mem;
    private SymType type;
    private int depth;
    private ArrayList<Symbol> args;
    private ArrayList<Symbol> members;

    public Symbol(String name, String cls, int depth){
        this.name = name;
        if(clsList.contains(cls)){
            this.cls = cls;
        } else {
            System.out.println("Invalid cls: " + cls);
            System.exit(1);
        }
        this.depth = depth;
        this.mem = null; this.type = null; this.args = null; this.members = null;
    }

    public Symbol(String name, String cls, int depth, SymType t){
        this.name = name;
        if(clsList.contains(cls)){
            this.cls = cls;
        } else {
            System.out.println("Invalid cls: " + cls);
            System.exit(1);
        }
        this.depth = depth;
        this.mem = null; this.type = t; this.args = null; this.members = null;
    }

    public Symbol(String name, String cls, int depth, SymType t, String mem){
        this.name = name;
        if(clsList.contains(cls)){
            this.cls = cls;
        } else {
            System.out.println("Invalid cls: " + cls);
            System.exit(1);
        }
        this.depth = depth;

        if(memList.contains(mem)) {
            this.mem = mem;
        }
        else {
            System.out.println("Invalid mem string: " + mem);
            System.exit(1);
        }

        this.type = t; this.args = null; this.members = null;
    }

    public String getName() {
        return name;
    }

    public void setMembers(ArrayList<Symbol> members) {
        this.members = members;
    }

    public ArrayList<Symbol> getMembers() {
        return members;
    }

    public void setType(SymType type) {
        this.type = type;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setMem(String mem_local) {
        if(memList.contains(mem_local)) {
            this.mem = mem_local;
        }
        else {
            System.out.println("Invalid mem string: " + mem_local);
            System.exit(1);
        }
    }

    public void setCls(String cls) {
        if(clsList.contains(cls)){
            this.cls = cls;
        } else {
            System.out.println("Invalid cls: " + cls);
            System.exit(1);
        }
    }

    public String getCls() {
        return cls;
    }

    public void setArgs(ArrayList<Symbol> symlist) {
        this.args = symlist;
    }

    public ArrayList<Symbol> getArgs() {
        return args;
    }
}
