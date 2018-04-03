package com.lftc;

import java.util.ArrayList;
import java.util.Arrays;

public class Token {
    private int code;   //codul atomului
    private String content;     //continutul atomului
    private int line;
    private int format;
    public static ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(
            "ID", "END", "BREAK", "CHAR", "DOUBLE", "ELSE", "FOR", "IF", "INT", "RETURN", "STRUCT", "VOID", "WHILE",
            "CT_INT", "CT_REAL", "CT_CHAR", "CT_STRING", "COMMA", "SEMICOLON", "LPAR", "RPAR", "LBRACKET",
            "RBRACKET", "LACC", "RACC", "ADD", "SUB", "MUL", "DIV", "DOT", "AND", "OR", "NOT", "ASSIGN",
            "EQUAL", "NOTEQ", "LESS", "LESSEQ", "GREATER", "GREATEREQ"
        )
    );

    public static ArrayList<String> formats = new ArrayList<String>(Arrays.asList(
            "STRING", "DECIMAL", "OCTAL", "HEXADECIMAL", "FLOATPOINT", "EXPONENTIAL"
    ));

    public static int codeOf(String s){
        return Token.tokens.indexOf(s);
    }

    public Token(int code, int line){
        this.code = code;
        this.content = "";
        this.line = line;
        this.format = 0;
    }

    public Token(int code, String content, int line) {
        //super();
        this.code = code;
        this.content = content;
        this.line = line;
        this.format = 0;
    }

    public Token(int code, String content, int line, int format){
        this.code = code;
        this.content = content;
        this.line = line;
        this.format = format;
    }
    public int getLine(){
        return this.line;
    }
    @Override
    public String toString() {
        String str = "";
        str += tokens.get(code);
        if(content != "") {
            if(format == 1 || format == 2 || format == 3) {
                int intContent = convertInt(format, content);
                str = str + ":" + intContent;
            }
            else{
                if(format == 5 || format == 6) {
                    float floatContent = convertFloat(format, content);
                    str = str + ":" + floatContent;
                }
                else
                    str = str + ":" + content;
            }
        }

        str += " ";
        return str;
    }

    private int convertInt(int format, String s){
        if(format == 1){
            return Integer.parseInt(s);
        }

        if(format == 2){
            return Integer.parseInt(s, 8);
        }

        else
            return Integer.parseInt(s.substring(2), 16);
    }

    private float convertFloat(int format, String s){
        if(format == 4){
            return Float.parseFloat(s);
        }
        else
            return Float.parseFloat(s);
    }

    public int getCode(){
        return code;
    }
}
