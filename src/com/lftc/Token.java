package com.lftc;

import java.util.ArrayList;
import java.util.Arrays;

public class Token {
    private int code;
    private String content;
    private int line;
    public static ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(
            "ID", "END", "BREAK", "CHAR", "DOUBLE", "ELSE", "FOR", "IF", "INT", "RETURN", "STRUCT", "VOID", "WHILE",
            "CT_INT", "CT_REAL", "CT_CHAR", "CT_STRING", "COMMA", "SEMICOLON", "LPAR", "RPAR", "LBRACKET",
            "RBRACKET", "LACC", "RACC", "ADD", "SUB", "MUL", "DIV", "DOT", "AND", "OR", "NOT", "ASSIGN",
            "EQUAL", "NOTEQ", "LESS", "LESSEQ", "GREATER", "GREATEREQ"
        )
    );

    public Token(int code, int line){
        this.code = code;
        this.content = "";
        this.line = line;
    }

    public Token(int code, String content, int line) {
        //super();
        this.code = code;
        this.content = content;
        this.line = line;
    }
    public int getLine(){
        return this.line;
    }
    @Override
    public String toString() {
        String str = "";
        str += tokens.get(code);
        if(content != "") {
            str = str + ":" + content;
        }
        str += " ";
        return str;
    }
}
