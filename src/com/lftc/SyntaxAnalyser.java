package com.lftc;

//
//TODO recursivitate method repairs (init and all)
import java.util.LinkedList;
import java.util.ListIterator;

public class SyntaxAnalyser {
    private LinkedList<Token> tokensToAnalyse;
    private Token consumed;
    private ListIterator<Token> tokenIterator = null;
    private Token crtTk;

    public SyntaxAnalyser(LinkedList<Token> tokensToAnalyse){
        this.tokensToAnalyse = tokensToAnalyse;
        this.tokenIterator = tokensToAnalyse.listIterator();
    }

    public void tkErr(Token t, String s){
        System.out.println(s + " Error on line: " + t.getLine());
    }

    public void analyse(){
        crtTk = tokenIterator.next();
        if(unit()){
            System.out.println("Sintaxa corecta!");
        }
        else {
            System.out.println("eroare sintaxa");
        }
    }

    public boolean unit(){
        while(declStruct() || declFunc() || declVar()){
            if(consume(Token.tokens.indexOf("END"))){
                return true;
            }
        }
        return false;
    }

    private boolean declVar() {
        System.out.println("declVar " + crtTk);
        Token init = crtTk;
        if(typeBase()){
            if(consume(Token.codeOf("ID"))){
                if(arrayDecl()){

                }
                while(true){
                    if(consume(Token.codeOf("COMMA"))){
                        if(consume(Token.codeOf("ID"))){
                            if(arrayDecl()){

                            }
                            continue;
                        }
                        else return false;
                    }
                    break;
                }
                if(consume(Token.codeOf("SEMICOLON")))
                    return true;
            }
        }
        crtTk = init;
        return false;
    }

    private boolean arrayDecl() {
        System.out.println("arrayDecl " + crtTk);
        Token init = crtTk;
        if(consume(Token.codeOf("LBRACKET"))){
            if(expr()){

            }
            if(consume(Token.codeOf("RBRACKET")))
                return true;
        }
        crtTk = init;
        return false;
    }

    private boolean expr() {
        System.out.println("expr " + crtTk);
        return exprAssign();
    }

    private boolean exprAssign() {
        System.out.println("exprAssign " + crtTk);
        Token init = crtTk;
        if(exprUnary()){
            if(consume(Token.codeOf("ASSIGN"))){
                if(exprAssign())
                    return true;
            }
        }
        crtTk = init;
        return exprOr();
    }

    private boolean exprOr() {
        System.out.println("exprOr " + crtTk);
        Token init = crtTk;
        if(exprAnd()){
            return exprOr1();

        }
        crtTk = init;
        return false;
    }

    private boolean exprOr1() {
        System.out.println("exprOr1 " + crtTk);
        if(consume(Token.codeOf("OR"))){
            if(exprAnd()){
                if(exprOr1()){}
            }else{
                tkErr(crtTk, "Missing OR element");
            }
        }
        return true;
    }

    private boolean exprAnd(){
        System.out.println("exprAnd " + crtTk);
        Token init = crtTk;
        if(exprEq()){
            return exprAnd1();
        }
        crtTk = init;
        return false;
    }

    private boolean exprAnd1() {
        System.out.println("exprAnd1 " + crtTk);
        if(consume(Token.codeOf("AND"))){
            if(exprEq()){
                if(exprAnd1()){}
            }
        }

        return true;
    }

    private boolean exprEq() {
        System.out.println("exprEq " + crtTk);
        Token init = crtTk;
        if(exprRel()){
            return exprEq1();
        }
        crtTk = init;
        return false;
    }

    private boolean exprEq1() {
        System.out.println("exprEq1 " + crtTk);
        Token init = crtTk;
        if(consume(Token.codeOf("EQUAL")) || consume(Token.codeOf("NOTEQ"))){
            if(exprRel()){
                if(exprEq1()){
                    return true;
                }
            }
        }
        crtTk = init;
        return true;
    }

    private boolean exprRel() {
        System.out.println("exprRel " + crtTk);
        Token init = crtTk;
        if(exprAdd()){
            return exprRel1();
        }
        crtTk = init;
        return false;
    }

    private boolean exprRel1() {
        System.out.println("exprRel1 " + crtTk);
        Token init = crtTk;
        if(consume(Token.codeOf("LESS")) || consume(Token.codeOf("LESSEQ")) ||
                consume(Token.codeOf("GREATER")) || consume(Token.codeOf("GREATEREQ"))){
            if(exprAdd()){
                if(exprRel1()){

                }
            }else{
                tkErr(crtTk, "Missing relation");
            }
        }
        crtTk = init;

        return true;
    }

    private boolean exprAdd() {
        System.out.println("exprAdd " + crtTk);
        Token init = crtTk;
        //if()
        //TODO exprAdd from notebook
        return false;
    }

    private boolean exprUnary() {
        System.out.println("exprUnary " + crtTk);
        if(consume(Token.codeOf("SUB")) || consume(Token.codeOf("NOT"))){
            return exprUnary();
        }
        else return exprPostfix();
    }

    private boolean exprPostfix() {
        //TODO recursivitate stanga

        return false;
    }

    private boolean typeBase() {
        System.out.println("typeBase " + crtTk);

        if(consume(Token.codeOf("INT")) || consume(Token.codeOf("DOUBLE")) || consume(Token.codeOf("CHAR"))
                || consume(Token.codeOf("STRUCT"))){
            if(consume(Token.codeOf("ID")))
                return true;
        }
        return false;
    }

    private boolean declFunc() {
        System.out.println("declFunc " + crtTk);
        Token init = crtTk;

        if(typeBase()){
            if(consume(Token.codeOf("MUL"))){
                return declFuncCommon();
            }
        }
        else if(consume(Token.codeOf("VOID"))){
            return declFuncCommon();
        }

        crtTk = init;
        return false;
    }

    private boolean declFuncCommon() {
        System.out.println("declFuncCommon " + crtTk);
        Token init = crtTk;
        if(consume(Token.codeOf("ID"))){
            if(consume(Token.codeOf("LPAR"))){
                if(funcArg()){
                    while(true){
                        if(consume(Token.codeOf("COMMA"))){
                            if(funcArg()){
                                continue;
                            }
                            return false;
                        }
                        break;
                    }
                }
                if(consume(Token.codeOf("RPAR"))){
                    if(stmCompound())
                        return true;
                }
            }
        }
        crtTk = init;
        return false;
    }

    private boolean stmCompound() {
        System.out.println("stmCompound " + crtTk);
        Token init = crtTk;
        if(consume(Token.codeOf("LACC"))){
            while(true){
                if(declVar() || stm())
                    continue;
                break;
            }
            if(consume(Token.codeOf("RACC")))
                return true;
        }
        crtTk = init;
        return false;
    }

    private boolean stm() {
        System.out.println("stm " + crtTk);
        Token init = crtTk;
        if(stmCompound())
            return true;
        else if(consume(Token.codeOf("IF"))){
            if(consume(Token.codeOf("LPAR"))){
                if(expr()){
                    if(consume(Token.codeOf("RPAR"))){
                        if(stm()){
                            if(consume(Token.codeOf("ELSE"))){
                                if(stm()){

                                }else{
                                    tkErr(crtTk, "Else statement missing");
                                }

                            }
                            return true;
                        }
                        else{
                            tkErr(crtTk, "Missing if statement");
                        }
                    }
                    else{
                        tkErr(crtTk, "Missing \')\' from if condition");
                    }
                }
                else{
                    tkErr(crtTk, "Missing if condition");
                }
            }
            else{
                tkErr(crtTk, "Missing \'(\' from if condition");
            }
        }
        else if(consume(Token.codeOf("WHILE"))){
            if(consume(Token.codeOf("LPAR"))){
                if(expr()){
                    if(consume(Token.codeOf("RPAR"))){
                        if(stm()){
                            return true;
                        }else{
                            tkErr(crtTk, "Missing while statements");
                        }
                    }else{
                        tkErr(crtTk, "Missing \')\' from while condition");
                    }
                }else{
                    tkErr(crtTk, "Missing while condition");
                }
            }else{
                tkErr(crtTk, "Missing \'(\' from while statement");
            }
        }
        else if(consume(Token.codeOf("FOR"))){
            if(consume(Token.codeOf("LPAR"))){
                if(expr()){ }

                if(consume(Token.codeOf("SEMICOLON"))){
                    if(expr()){}
                    if(consume(Token.codeOf("SEMICOLON"))){
                        if(expr()){}
                        if(consume(Token.codeOf("RPAR"))){
                            if(stm())
                                return true;
                            else tkErr(crtTk, "Missing for statements");
                        }else{
                            tkErr(crtTk, "Missing \')\' from for conditions");
                        }
                    }
                    else{
                        tkErr(crtTk, "Missing \';\'");
                    }
                }else{
                    tkErr(crtTk, "Missing \';\'");
                }


            }else{
                tkErr(crtTk, "Missing \'(\' from for conditions");
            }
        }
        else if(consume(Token.codeOf("BREAK"))){
            if(consume(Token.codeOf("SEMICOLON"))){
                return true;
            }else{
                tkErr(crtTk, "Missing \';\'");
            }
        }
        else if(consume(Token.codeOf("RETURN"))){
            if(expr())
            {}
            if (consume(Token.codeOf("SEMICOLON"))){
                return true;
            }
            else{
                tkErr(crtTk, "Missing \';\'");
            }
        }
        else if(expr()){}
        if(consume(Token.codeOf("SEMICOLON")))
            return true;
        crtTk = init;

        return false;
    }

    private boolean funcArg() {
        System.out.println("funcArg " + crtTk);
        Token init = crtTk;
        if(typeBase()){
            if(consume(Token.codeOf("ID"))){
                if(arrayDecl()){

                }
                return true;
            }
        }
        crtTk = init;
        return false;
    }

    private boolean declStruct() {
        System.out.println("declStruct " + crtTk);
        Token init = crtTk;
        if(consume(Token.tokens.indexOf("STRUCT"))){
            if(consume(Token.tokens.indexOf("ID"))){
                if(consume(Token.tokens.indexOf("LACC"))){
                    while(declVar()){

                    }
                    if(consume(Token.tokens.indexOf("RACC"))){
                        if(consume(Token.tokens.indexOf("SEMICOLON")))
                            return true;
                    }
                }
            }
        }
        crtTk = init;
        return false;
    }

    public boolean consume(int code){
        if(crtTk.getCode() ==  code){
            consumed = crtTk;
            crtTk = tokenIterator.next();
            //System.out.println("consumed: " + consumed);
            return true;
        }
        return false;
    }
}
