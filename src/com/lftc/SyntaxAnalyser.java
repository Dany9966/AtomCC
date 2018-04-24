package com.lftc;
//TODO plz debug this fast
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
        System.exit(1);
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
                        else {
                            tkErr(crtTk, "Missing ID after COMMA");
                            break;
                        }
                    }
                    break;
                }
                if(consume(Token.codeOf("SEMICOLON")))
                    return true;
                else tkErr(crtTk, "Missing SEMICOLON");
            }
            else{
                tkErr(crtTk, "Missing ID");
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
            else tkErr(crtTk, "Missing RBRACKET");
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
            else{
                tokenIterator.previous();
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
            else {
                tkErr(crtTk, "Missing AND element");
                return false;
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
            else {
                tkErr(crtTk, "Missing relation");
                return false;
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
                return false;
            }
        }
        crtTk = init;

        return true;
    }

    private boolean exprAdd() {
        System.out.println("exprAdd " + crtTk);
        Token init = crtTk;

        if(exprMul()){
            return exprAdd1();
        }
        crtTk = init;
        return false;
    }

    private boolean exprMul() {
        System.out.println("exprMul " + crtTk);
        Token init = crtTk;
        if(exprCast()){
            return exprMul1();
        }

        crtTk = init;
        return false;
    }

    private boolean exprMul1() {
        System.out.println("exprMul1 " + crtTk);
        Token init = crtTk;
        if(consume(Token.codeOf("MUL")) || consume(Token.codeOf("DIV"))){
            if(exprCast()){
                if(exprMul1()){
                    return true;
                }
            }else{
                tkErr(crtTk, "Missing cast expr");
                return false;
            }
        }

        return true;
    }

    private boolean exprCast() {
        System.out.println("exprCast " + crtTk);
        Token init = crtTk;

        if(consume(Token.codeOf("LPAR"))){
            if(typeName()){
                if(consume(Token.codeOf("RPAR"))){
                    if(exprCast()){
                        return true;
                    }
                }
                else{
                    tkErr(crtTk, "Missing RPAR");
                }
            }
            else{
                tkErr(crtTk, "Missing cast type after LPAR");
            }
        }
        else{
            if(exprUnary())
                return true;
        }

        crtTk = init;
        return false;
    }

    private boolean typeName() {
        System.out.println("typeName " + crtTk);
        Token init = crtTk;

        if(typeBase()){
            if(arrayDecl()){

            }
            return true;
        }

        crtTk = init;
        return false;
    }

    private boolean exprAdd1() {
        System.out.println("exprAdd1 " + crtTk);
        Token init = crtTk;

        if(consume(Token.codeOf("ADD")) || consume(Token.codeOf("SUB"))){
            if(exprMul()){
                if(exprAdd1()){
                    return true;
                }
            }
            else{
                tkErr(crtTk, "Missing mul expression");
                return false;
            }
        }
        return true;
    }


    private boolean exprUnary() {
        System.out.println("exprUnary " + crtTk);
        if(consume(Token.codeOf("SUB")) || consume(Token.codeOf("NOT"))){
            return exprUnary();
        }
        else return exprPostfix();
    }

    private boolean exprPostfix() {
        System.out.println("exprPostfix " + crtTk);
        Token init = crtTk;

        if(exprPrimary()){
            return exprPostfix1();
        }

        crtTk = init;
        return false;
    }

    private boolean exprPrimary() {
        System.out.println("exprPrimary " + crtTk);
        Token init = crtTk;

        if(consume(Token.codeOf("ID"))){
            if(consume(Token.codeOf("LPAR"))){
                if(expr()){
                    while(true){
                        if(consume(Token.codeOf("COMMA"))){
                            if(expr()){
                                continue;
                            }
                            else{
                                tkErr(crtTk, "Missing expression after COMMA");
                                return false;
                            }

                        }
                        else{
                            break;
                        }
                    }
                }
                if(consume(Token.codeOf("RPAR"))){

                }
                else{
                    tkErr(crtTk, "Missing RPAR");
                    crtTk = init;
                    return false;
                }
            }



            return true;
        }
        else if(consume(Token.codeOf("CT_INT")))
            return true;

        else if(consume(Token.codeOf("CT_REAL")))
            return true;

        else if(consume(Token.codeOf("CT_CHAR")))
            return true;

        else if(consume(Token.codeOf("CT_STRING")))
            return true;

        else if(consume(Token.codeOf("LPAR"))){
            if(expr()){
                if(consume(Token.codeOf("RPAR")))
                    return true;
            }
        }

        crtTk = init;
        return false;
    }

    private boolean exprPostfix1() {
        System.out.println("exprPostfix1 " + crtTk);
        Token init = crtTk;

        if(consume(Token.codeOf("LBRACKET"))){
            if(expr()){
                if(consume(Token.codeOf("RBRACKET"))){
                    if(exprPostfix1()){
                        return true;
                    }
                }
                else{
                    tkErr(crtTk, "Missing RBRACKET");
                    return false;
                }
            }
            else{
                tkErr(crtTk, "Missing expression after LBRACKET");
                return false;
            }
        }
        else{
            if(consume(Token.codeOf("DOT"))){
                if(consume(Token.codeOf("ID"))){
                    if(exprPostfix1()){
                        return true;
                    }
                }
                else{
                    tkErr(crtTk, "Missing ID after DOT");
                    return false;
                }
            }
        }

        //crtTk = init;
        return true;
    }


    private boolean typeBase() {
        System.out.println("typeBase " + crtTk);
        Token init = crtTk;

        if(consume(Token.codeOf("INT")) || consume(Token.codeOf("DOUBLE")) || consume(Token.codeOf("CHAR"))) {
            return true;
        }
        else if(consume(Token.codeOf("STRUCT"))){
            if(consume(Token.codeOf("ID")))
                return true;
            else{
                tkErr(crtTk, "Missing ID");
            }
        }

        crtTk = init;
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
                            tkErr(crtTk, "Missing argument after COMMA");
                            return false;
                        }
                        break;
                    }
                }
                if(consume(Token.codeOf("RPAR"))){
                    if(stmCompound())
                        return true;
                    else{
                        tkErr(crtTk, "Missing function definition");
                    }
                }
                else{
                    tkErr(crtTk, "Missing RPAR");
                }
            }
            else{
                tkErr(crtTk, "Missing LPAR after function ID");
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
            else{
                tkErr(crtTk, "Missing RACC");
            }
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
            else{
                tkErr(crtTk, "Missing ID");
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
                        else tkErr(crtTk, "Missing SEMICOLON");
                    }
                    else{
                        tkErr(crtTk, "Missing RACC");
                    }
                }else{
                    tkErr(crtTk, "Missing struct definition");
                }
            }
            else{
                tkErr(crtTk, "Missing STRUCT ID");
            }
        }
        crtTk = init;
        return false;
    }

    public boolean consume(int code){
        if(crtTk.getCode() ==  code){
            consumed = crtTk;
            if(tokenIterator.hasNext()) {
                crtTk = tokenIterator.next();
            }
            System.out.println("consumed: " + consumed);
            return true;
        }
        return false;
    }
}
