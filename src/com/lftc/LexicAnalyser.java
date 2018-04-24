package com.lftc;

import java.util.ArrayList;

public class LexicAnalyser {

        private ArrayList<Token> tokenList;
        private int state;
        private int line;

    public LexicAnalyser() {
        this.tokenList = new ArrayList<>();
        this.state = 0;
        this.line = 1;
    }

    public ArrayList<Token> analiseFile(byte[] bFile){
            String text = new String(bFile);
            //text = text.concat(Character.toString('\0'));
            text += '\0';
            //System.out.println(text);
            String word = "";
            int format = 0;
            int err = 0;
            if(err == 0){
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    //System.out.println("character: " + c);
                    //System.out.println("state: " + state);
                    //System.out.println("word: " + word);
                    //int ignore = 0;
                    switch (state) {
                        case 0:
                            if (Character.isLetter(c) || c == '_') {
                                //System.out.println("letter");
                                state = 1;
                                //word = word.concat(Character.toString(c));
                                word += c;
                                break;
                            }
                            if (c == '-') {
                                // System.out.println("minus");

                                state = 30;
                                break;
                            }
                            if (c == '+') {
                                // System.out.println("plus");

                                state = 29;
                                break;
                            }
                            if (Character.isDigit(c)) {
                                //System.out.println("digit");
                                word += c;
                                if (c == '0')
                                    state = 4;
                                else
                                    state = 3;
                                break;
                            }
                            if (c == '[') {
                                // System.out.println("lbracket");

                                state = 25;
                                break;
                            }
                            if (c == ']') {
                                // System.out.println("rbracket");

                                state = 26;
                                break;
                            }
                            if (c == '{') {
                                //System.out.println("lacc");

                                state = 27;
                                break;
                            }
                            if (c == '}') {
                                //System.out.println("racc");

                                state = 28;
                                break;
                            }
                            if (c == '\'') {
                                //System.out.println("apostrof");

                                state = 14;
                                break;
                            }
                            if (c == '.') {
                                // System.out.println("dot");

                                state = 33;
                                break;
                            }
                            if (c == '(') {
                                // System.out.println("lpar");

                                state = 23;
                                break;
                            }
                            if (c == ')') {
                                //  System.out.println("rpar");

                                state = 24;
                                break;
                            }
                            if (c == '\"') {
                                // System.out.println("double quotes");

                                state = 18;
                                break;
                            }
                            if (c == '*') {
                                // System.out.println("star");

                                state = 31;
                                break;
                            }
                            if (c == '\n') {
                                line++;
                                break;
                            }
                            if (c == ' ' || c == '\t') {
                                break;
                            }

                            if (c == '\0') {
                                tokenList.add(new Token(Token.tokens.indexOf("END"), line));
                                break;
                            }
                            if (c == ';') {
                                state = 22;
                                break;
                            }
                            if (c == ',') {
                                state = 21;
                                break;
                            }

                                /*if(c == '\\'){
                                    state = 56;
                                    break;
                                }*/
                            if (c == '&') {
                                state = 34;
                                break;
                            }
                            if (c == '|') {
                                state = 36;
                                break;
                            }
                            if (c == '!') {
                                state = 38;
                                break;
                            }
                            if (c == '/') {
                                state = 51;
                                break;
                            }
                            if (c == '<') {
                                state = 44;
                                break;
                            }
                            if (c == '=') {
                                state = 39;
                                break;
                            }
                            if (c == '>') {
                                state = 47;
                                break;
                            }

                        case 1:
                            if (Character.isLetterOrDigit(c) || c == '_') {
                                // System.out.println("letter or digit");
                                //word = word.concat(Character.toString(c));
                                word += c;
                                break;
                            } else {

                                i--; //nu se consuma caracterul
                                state = 2;
                                break;

                            }
                        case 2:
                            //System.out.println("Word: " + Token.tokens.subList(2, 13));
                            if (Token.tokens.subList(2, 13).contains(word.toUpperCase())) {
                                // System.out.println("keyword");
                                int auxCode = Token.tokens.indexOf(word.toUpperCase());
                                tokenList.add(new Token(auxCode, line));
                            } else {
                                tokenList.add(new Token(Token.tokens.indexOf("ID"), word, line));
                            }

                            word = "";
                            state = 0;
                            i--;//nu se consuma caracterul
                            break;

                        case 3:
                            format = Token.formats.indexOf("DECIMAL");
                            if (Character.isDigit(c)) {
                                //word = word.concat(Character.toString(c));
                                word += c;
                                break;
                            }

                            if (c == 'e' || c == 'E') {
                                //word = word.concat(Character.toString(c));
                                format = Token.formats.indexOf("EXPONENTIAL");
                                word += c;
                                state = 10;
                                break;
                            }
                            if (c == '.') {
                                format = Token.formats.indexOf("FLOATPOINT");
                                word += c;
                                state = 8;

                                break;
                            } else {
                                i--;
                                state = 7;
                                break;
                            }

                        case 4:
                            if (c == '.') {
                                format = Token.formats.indexOf("FLOATPOINT");
                                word += c;
                                state = 8;
                                break;
                            }
                            if (c == 'x') {
                                format = Token.formats.indexOf("HEXADECIMAL");
                                word += c;
                                state = 5;
                                break;
                            } else {
                                i--;
                                state = 401;
                                break;
                            }

                        case 401:
                            if (Character.isDigit(c)) {
                                word += c;
                                if (Character.getNumericValue(c) == 8 || Character.getNumericValue(c) == 9)
                                    state = 402;
                                break;
                            }
                            if (c == 'e' || c == 'E') {
                                format = Token.formats.indexOf("EXPONENTIAL");
                                word += c;
                                state = 10;
                                break;
                            }
                            if (c == '.') {
                                format = Token.formats.indexOf("FLOATPOINT");
                                word += c;
                                state = 8;
                                break;
                            } else {
                                format = Token.formats.indexOf("OCTAL");
                                i--;
                                state = 7;
                                break;
                            }

                        case 402:
                            if (Character.isDigit(c)) {
                                word += c;
                                break;
                            }
                            if (c == 'e' || c == 'E') {
                                format = Token.formats.indexOf("EXPONENTIAL");
                                word += c;
                                state = 10;
                                break;
                            }
                            if (c == '.') {
                                format = Token.formats.indexOf("FLOATPOINT");
                                word += c;
                                state = 8;
                                break;
                            } else {
                                word += c;
                                System.out.println("Expected number, e/E or floating point: " + word);
                                System.out.println("Line: " + line);
                                err = 1;
                                i = text.length();
                                break;
                            }

                        case 5:
                            if (Character.isLetterOrDigit(c)) {
                                word += c;
                                state = 6;
                                break;

                            } else {
                                //TODO check if alpha is a-f A-F
                                word += c;
                                System.out.println("Expected alphanum: " + word);
                                System.out.println("Line: " + line);
                                i = text.length();
                                err = 1;
                                break;
                            }

                        case 6:
                            if (Character.isLetterOrDigit(c)) {
                                //TODO check if alpha is a-f A-F
                                word += c;
                                break;
                            } else {
                                i--;
                                state = 7;
                                break;
                            }

                        case 7:

                            tokenList.add(new Token(Token.tokens.indexOf("CT_INT"), word, line, format));
                            format = 0;
                            i--;
                            word = "";
                            state = 0;
                            break;

                        case 8:
                            if (Character.isDigit(c)) {
                                word += c;
                                state = 9;
                                break;
                            } else {
                                word += c;
                                System.out.println("Expected number: " + word);
                                System.out.println("Line: " + line);
                                err = 1;
                                i = text.length();
                                break;
                            }

                        case 9:
                            if (Character.isDigit(c)) {
                                word += c;
                                break;

                            }
                            if (c == 'e' || c == 'E') {
                                format = Token.formats.indexOf("EXPONENTIAL");
                                word += c;
                                state = 10;
                                break;
                            } else {
                                i--;
                                state = 13;
                                break;
                            }

                        case 10:
                            if (c == '+' || c == '-') {
                                word += c;
                                state = 11;
                                break;
                            }
                            if (Character.isDigit(c)) {
                                word += c;
                                state = 12;
                                break;
                            } else {
                                word += c;
                                System.out.println("Expected digit or +/-: " + word);
                                System.out.println("Line: " + line);
                                err = 1;
                                i = text.length();
                                break;
                            }

                        case 11:
                            if (Character.isDigit(c)) {
                                word += c;
                                state = 12;
                                break;
                            } else {
                                word += c;
                                System.out.println("Expected digit: " + word);
                                System.out.println("Line: " + line);
                                err = 1;
                                i = text.length();
                                break;
                            }

                        case 12:
                            if (Character.isDigit(c)) {
                                word += c;
                                break;
                            } else {
                                i--;
                                state = 13;
                                break;
                            }

                        case 13:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("CT_REAL"), word, line, format));
                            format = 0;
                            state = 0;
                            word = "";
                            break;

                        case 14:
                            if (c == '\\') {
                                state = 17;
                                break;
                            } else {
                                word += c;
                                state = 15;
                                break;
                            }

                        case 15:
                            if (c == '\'') {
                                state = 16;
                                break;
                            } else {
                                word += c;
                                System.out.println("Invalid char: " + word);
                                System.out.println("Line: " + line);
                                err = 1;
                                i = text.length();
                                break;
                            }

                        case 16:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("CT_CHAR"), word, line));
                            state = 0;
                            word = "";
                            break;

                        case 17:
                            switch (c) {
                                case 'a':
                                    word += '\007';
                                    state = 15;
                                    break;
                                case 'b':
                                    word += '\b';
                                    state = 15;
                                    break;
                                case 'f':
                                    word += '\f';
                                    state = 15;
                                    break;
                                case 'n':
                                    word += '\n';
                                    state = 15;
                                    break;
                                case 't':
                                    word += '\t';
                                    state = 15;
                                    break;
                                //case 'v': word += "\v"; state = 15; break;
                                case '\\':
                                    word += '\\';
                                    state = 15;
                                    break;
                                case '\'':
                                    word += '\'';
                                    state = 15;
                                    break;
                                case '\"':
                                    word += '\"';
                                    state = 15;
                                    break;
                                case '?':
                                    word += '?';
                                    state = 15;
                                    break;
                                case '\0':
                                    word += '\0';
                                    state = 15;
                                    break;
                                default:
                                    System.out.println("Illegal escape character: \\" + c);
                                    System.out.println("Line: " + line);
                                    err = 1;
                                    i = text.length();
                                    break;
                            }

                            break;

                        case 18:
                            if (c == '\"') {
                                state = 19;
                                break;
                            }
                            if (c == '\\') {
                                state = 20;
                                break;
                            } else {
                                word += c;
                                break;
                            }

                        case 19:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("CT_STRING"), word, line));
                            state = 0;
                            word = "";
                            break;

                        case 20:
                            switch (c) {
                                case 'a':
                                    word += '\007';
                                    state = 18;
                                    break;
                                case 'b':
                                    word += '\b';
                                    state = 18;
                                    break;
                                case 'f':
                                    word += '\f';
                                    state = 18;
                                    break;
                                case 'n':
                                    word += '\n';
                                    state = 18;
                                    break;
                                case 't':
                                    word += '\t';
                                    state = 18;
                                    break;
                                //case 'v': word += "\v"; state = 15; break;
                                case '\\':
                                    word += '\\';
                                    state = 18;
                                    break;
                                case '\'':
                                    word += '\'';
                                    state = 18;
                                    break;
                                case '\"':
                                    word += '\"';
                                    state = 18;
                                    break;
                                case '?':
                                    word += '?';
                                    state = 18;
                                    break;
                                case '\0':
                                    word += '\0';
                                    state = 18;
                                    break;
                                default:
                                    System.out.println("Illegal escape character: \\" + c);
                                    System.out.println("Line: " + line);
                                    err = 1;
                                    i = text.length();
                                    break;
                            }
                            break;

                        case 21:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("COMMA"), line));
                            state = 0;
                            word = "";
                            break;

                        case 22:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("SEMICOLON"), line));
                            state = 0;
                            word = "";
                            break;

                        case 23:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("LPAR"), line));
                            state = 0;
                            word = "";
                            break;

                        case 24:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("RPAR"), line));
                            state = 0;
                            word = "";
                            break;

                        case 25:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("LBRACKET"), line));
                            state = 0;
                            word = "";
                            break;

                        case 26:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("RBRACKET"), line));
                            state = 0;
                            word = "";
                            break;

                        case 27:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("LACC"), line));
                            state = 0;
                            word = "";
                            break;

                        case 28:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("RACC"), line));
                            state = 0;
                            word = "";
                            break;

                        case 29:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("ADD"), line));
                            state = 0;
                            word = "";
                            break;

                        case 30:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("SUB"), line));
                            state = 0;
                            word = "";
                            break;

                        case 31:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("MUL"), line));
                            state = 0;
                            word = "";
                            break;

                        case 32:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("DIV"), line));
                            state = 0;
                            word = "";
                            break;

                        case 33:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("DOT"), line));
                            state = 0;
                            word = "";
                            break;

                        case 34:
                            if (c == '&') {
                                state = 35;
                                break;
                            } else {
                                System.out.println("Expected & on line: " + line);
                                err = 1;
                                i = text.length();
                                break;
                            }

                        case 35:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("AND"), line));
                            state = 0;
                            word = "";
                            break;

                        case 36:
                            if (c == '|') {
                                state = 37;
                                break;
                            } else {
                                System.out.println("Expected | on line: " + line);
                                err = 1;
                                i = text.length();
                                break;
                            }

                        case 37:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("OR"), line));
                            state = 0;
                            word = "";
                            break;

                        case 38:
                            if (c == '=') {
                                state = 42;
                                break;
                            } else {
                                state = 43;
                                break;
                            }

                        case 39:
                            if (c == '=') {
                                state = 40;
                                break;
                            } else {
                                state = 41;
                                break;
                            }

                        case 40:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("EQUAL"), line));
                            state = 0;
                            word = "";
                            break;

                        case 41:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("ASSIGN"), line));
                            state = 0;
                            word = "";
                            break;

                        case 42:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("NOTEQ"), line));
                            state = 0;
                            word = "";
                            break;

                        case 43:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("NOT"), line));
                            state = 0;
                            word = "";
                            break;

                        case 44:
                            if (c == '=') {
                                state = 45;
                                break;
                            } else {
                                state = 46;
                                break;
                            }

                        case 45:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("LESSEQ"), line));
                            state = 0;
                            word = "";
                            break;

                        case 46:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("LESS"), line));
                            state = 0;
                            word = "";
                            break;

                        case 47:
                            if (c == '=') {
                                state = 48;
                                break;
                            } else {
                                state = 49;
                                break;
                            }

                        case 48:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("GREATEREQ"), line));
                            state = 0;
                            word = "";
                            break;

                        case 49:
                            i--;
                            tokenList.add(new Token(Token.tokens.indexOf("GREATER"), line));
                            state = 0;
                            word = "";
                            break;

                        case 51:
                            if (c == '/') {
                                state = 52;
                                break;
                            }
                            if (c == '*') {
                                state = 54;
                                break;
                            } else {
                                state = 32;
                                i--;
                                break;
                            }

                        case 52:
                            if (c == '\n') {
                                line++;
                                state = 0;
                            }

                            if (c == '\0') {
                                state = 0;
                            }

                            break;


                        case 54:
                            if (c == '*')
                                state = 55;

                            break;

                        case 55:
                            if (c == '/') {
                                state = 0;
                                break;
                            }
                            if (!(c == '*'))
                                state = 54;
                            break;
                    }
                }
            }

            //printToken.tokens();
            if(err == 0) {

                tokenList.forEach(t -> System.out.print(t.toString()));
            }




        return tokenList;
    }
}


