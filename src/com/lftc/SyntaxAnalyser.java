package com.lftc;
//TODO plz debug this fast
import java.util.ArrayList;
import java.util.ListIterator;
public class SyntaxAnalyser {
    private ArrayList<Token> tokensToAnalyse;
    private Token consumed;
    private ListIterator<Token> tokenIterator = null;
    private int crtTkIndex;

    public SyntaxAnalyser(ArrayList<Token> tokensToAnalyse){
        this.tokensToAnalyse = tokensToAnalyse;
        this.tokenIterator = tokensToAnalyse.listIterator();
    }

    public void tkErr(int index, String s){
        System.out.println(s + " Error on line: " + tokensToAnalyse.get(index).getLine());
        System.exit(1);
    }

    public void analyse(){
        crtTkIndex = 0;
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
        System.out.println("declVar " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
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
                            tkErr(crtTkIndex, "Missing ID after COMMA");
                            break;
                        }
                    }
                    break;
                }
                if(consume(Token.codeOf("SEMICOLON")))
                    return true;
                else tkErr(crtTkIndex, "Missing SEMICOLON");
            }
            else{
                tkErr(crtTkIndex, "Missing ID");
            }
        }
        crtTkIndex = init;
        return false;
    }

    private boolean arrayDecl() {
        System.out.println("arrayDecl " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(consume(Token.codeOf("LBRACKET"))){
            if(expr()){

            }
            if(consume(Token.codeOf("RBRACKET")))
                return true;
            else tkErr(crtTkIndex, "Missing RBRACKET");
        }
        crtTkIndex = init;
        return false;
    }

    private boolean expr() {
        System.out.println("expr " + tokensToAnalyse.get(crtTkIndex));
        return exprAssign();
    }

    private boolean exprAssign() {
        System.out.println("exprAssign " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(exprUnary()){
            if(consume(Token.codeOf("ASSIGN"))){
                if(exprAssign())
                    return true;
            }
        }

        crtTkIndex = init;
        return exprOr();
    }

    private boolean exprOr() {
        System.out.println("exprOr " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(exprAnd()){
            return exprOr1();

        }
        crtTkIndex = init;
        return false;
    }

    private boolean exprOr1() {
        System.out.println("exprOr1 " + tokensToAnalyse.get(crtTkIndex));
        if(consume(Token.codeOf("OR"))){
            if(exprAnd()){
                if(exprOr1()){}
            }else{
                tkErr(crtTkIndex, "Missing OR element");
            }
        }
        return true;
    }

    private boolean exprAnd(){
        System.out.println("exprAnd " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(exprEq()){
            return exprAnd1();
        }
        crtTkIndex = init;
        return false;
    }

    private boolean exprAnd1() {
        System.out.println("exprAnd1 " + tokensToAnalyse.get(crtTkIndex));
        if(consume(Token.codeOf("AND"))){
            if(exprEq()){
                if(exprAnd1()){}
            }
            else {
                tkErr(crtTkIndex, "Missing AND element");
                return false;
            }
        }

        return true;
    }

    private boolean exprEq() {
        System.out.println("exprEq " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(exprRel()){
            return exprEq1();
        }
        crtTkIndex = init;
        return false;
    }

    private boolean exprEq1() {
        System.out.println("exprEq1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;
        if(consume(Token.codeOf("EQUAL")) || consume(Token.codeOf("NOTEQ"))){
            if(exprRel()){
                if(exprEq1()){
                    return true;
                }
            }
            else {
                tkErr(crtTkIndex, "Missing relation");
                return false;
            }
        }
        // crtTkIndex = init;
        return true;
    }

    private boolean exprRel() {
        System.out.println("exprRel " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(exprAdd()){
            return exprRel1();
        }
        crtTkIndex = init;
        return false;
    }

    private boolean exprRel1() {
        System.out.println("exprRel1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;
        if(consume(Token.codeOf("LESS")) || consume(Token.codeOf("LESSEQ")) ||
                consume(Token.codeOf("GREATER")) || consume(Token.codeOf("GREATEREQ"))){
            if(exprAdd()){
                if(exprRel1()){

                }
            }else{
                tkErr(crtTkIndex, "Missing relation");
                return false;
            }
        }
        //crtTkIndex = init;

        return true;
    }

    private boolean exprAdd() {
        System.out.println("exprAdd " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(exprMul()){
            return exprAdd1();
        }
        crtTkIndex = init;
        return false;
    }

    private boolean exprMul() {
        System.out.println("exprMul " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(exprCast()){
            return exprMul1();
        }

        crtTkIndex = init;
        return false;
    }

    private boolean exprMul1() {
        System.out.println("exprMul1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;
        if(consume(Token.codeOf("MUL")) || consume(Token.codeOf("DIV"))){
            if(exprCast()){
                if(exprMul1()){
                    return true;
                }
            }else{
                tkErr(crtTkIndex, "Missing cast expr");
                return false;
            }
        }

        return true;
    }

    private boolean exprCast() {
        System.out.println("exprCast " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(consume(Token.codeOf("LPAR"))){
            if(typeName()){
                if(consume(Token.codeOf("RPAR"))){
                    if(exprCast()){
                        return true;
                    }
                }
                else{
                    tkErr(crtTkIndex, "Missing RPAR");
                }
            }
            else{
                tkErr(crtTkIndex, "Missing cast type after LPAR");
            }
        }
        else{
            if(exprUnary())
                return true;
        }

        crtTkIndex = init;
        return false;
    }

    private boolean typeName() {
        System.out.println("typeName " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(typeBase()){
            if(arrayDecl()){

            }
            return true;
        }

        crtTkIndex = init;
        return false;
    }

    private boolean exprAdd1() {
        System.out.println("exprAdd1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;

        if(consume(Token.codeOf("ADD")) || consume(Token.codeOf("SUB"))){
            if(exprMul()){
                if(exprAdd1()){
                    return true;
                }
            }
            else{
                tkErr(crtTkIndex, "Missing mul expression");
                return false;
            }
        }
        return true;
    }


    private boolean exprUnary() {
        System.out.println("exprUnary " + tokensToAnalyse.get(crtTkIndex));
        if(consume(Token.codeOf("SUB")) || consume(Token.codeOf("NOT"))){
            return exprUnary();
        }
        else return exprPostfix();
    }

    private boolean exprPostfix() {
        System.out.println("exprPostfix " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(exprPrimary()){
            return exprPostfix1();
        }

        crtTkIndex = init;
        return false;
    }

    private boolean exprPrimary() {
        System.out.println("exprPrimary " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(consume(Token.codeOf("ID"))){
            if(consume(Token.codeOf("LPAR"))){
                if(expr()){
                    while(true){
                        if(consume(Token.codeOf("COMMA"))){
                            if(expr()){
                                continue;
                            }
                            else{
                                tkErr(crtTkIndex, "Missing expression after COMMA");
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
                    tkErr(crtTkIndex, "Missing RPAR");
                    crtTkIndex = init;
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

        crtTkIndex = init;
        return false;
    }

    private boolean exprPostfix1() {
        System.out.println("exprPostfix1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;

        if(consume(Token.codeOf("LBRACKET"))){
            if(expr()){
                if(consume(Token.codeOf("RBRACKET"))){
                    if(exprPostfix1()){
                        return true;
                    }
                }
                else{
                    tkErr(crtTkIndex, "Missing RBRACKET");
                    return false;
                }
            }
            else{
                tkErr(crtTkIndex, "Missing expression after LBRACKET");
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
                    tkErr(crtTkIndex, "Missing ID after DOT");
                    return false;
                }
            }
        }

        //crtTkIndex = init;
        return true;
    }


    private boolean typeBase() {
        System.out.println("typeBase " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(consume(Token.codeOf("INT")) || consume(Token.codeOf("DOUBLE")) || consume(Token.codeOf("CHAR"))) {
            return true;
        }
        else if(consume(Token.codeOf("STRUCT"))){
            if(consume(Token.codeOf("ID")))
                return true;
            else{
                tkErr(crtTkIndex, "Missing ID");
            }
        }

        crtTkIndex = init;
        return false;
    }

    private boolean declFunc() {
        System.out.println("declFunc " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(typeBase()){
            if(consume(Token.codeOf("MUL"))){

            }

        }
        else if(consume(Token.codeOf("VOID"))){

        }

        if(declFuncCommon()){
            return true;
        }

        crtTkIndex = init;
        return false;
    }

    private boolean declFuncCommon() {
        System.out.println("declFuncCommon " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(consume(Token.codeOf("ID"))){
            if(consume(Token.codeOf("LPAR"))){
                if(funcArg()){
                    while(true){
                        if(consume(Token.codeOf("COMMA"))){
                            if(funcArg()){
                                continue;
                            }
                            tkErr(crtTkIndex, "Missing argument after COMMA");
                            return false;
                        }
                        break;
                    }
                }
                if(consume(Token.codeOf("RPAR"))){
                    if(stmCompound())
                        return true;
                    else{
                        tkErr(crtTkIndex, "Missing function definition");
                    }
                }
                else{
                    tkErr(crtTkIndex, "Missing RPAR");
                }
            }

        }
        crtTkIndex = init;
        return false;
    }

    private boolean stmCompound() {
        System.out.println("stmCompound " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(consume(Token.codeOf("LACC"))){
            while(true){
                if(declVar() || stm())
                    continue;
                break;
            }
            if(consume(Token.codeOf("RACC")))
                return true;
            else{
                tkErr(crtTkIndex, "Missing RACC");
            }
        }
        crtTkIndex = init;
        return false;
    }

    private boolean stm() {
        System.out.println("stm " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
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
                                    tkErr(crtTkIndex, "Else statement missing");
                                }

                            }
                            return true;
                        }
                        else{
                            tkErr(crtTkIndex, "Missing if statement");
                        }
                    }
                    else{
                        tkErr(crtTkIndex, "Missing \')\' from if condition");
                    }
                }
                else{
                    tkErr(crtTkIndex, "Missing if condition");
                }
            }
            else{
                tkErr(crtTkIndex, "Missing \'(\' from if condition");
            }
        }
        else if(consume(Token.codeOf("WHILE"))){
            if(consume(Token.codeOf("LPAR"))){
                if(expr()){
                    if(consume(Token.codeOf("RPAR"))){
                        if(stm()){
                            return true;
                        }else{
                            tkErr(crtTkIndex, "Missing while statements");
                        }
                    }else{
                        tkErr(crtTkIndex, "Missing \')\' from while condition");
                    }
                }else{
                    tkErr(crtTkIndex, "Missing while condition");
                }
            }else{
                tkErr(crtTkIndex, "Missing \'(\' from while statement");
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
                            else tkErr(crtTkIndex, "Missing for statements");
                        }else{
                            tkErr(crtTkIndex, "Missing \')\' from for conditions");
                        }
                    }
                    else{
                        tkErr(crtTkIndex, "Missing \';\'");
                    }
                }else{
                    tkErr(crtTkIndex, "Missing \';\'");
                }


            }else{
                tkErr(crtTkIndex, "Missing \'(\' from for conditions");
            }
        }
        else if(consume(Token.codeOf("BREAK"))){
            if(consume(Token.codeOf("SEMICOLON"))){
                return true;
            }else{
                tkErr(crtTkIndex, "Missing \';\'");
            }
        }
        else if(consume(Token.codeOf("RETURN"))){
            if(expr())
            {}
            if (consume(Token.codeOf("SEMICOLON"))){
                return true;
            }
            else{
                tkErr(crtTkIndex, "Missing \';\'");
            }
        }
        else if(expr()){}
        if(consume(Token.codeOf("SEMICOLON")))
            return true;
        crtTkIndex = init;

        return false;
    }

    private boolean funcArg() {
        System.out.println("funcArg " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(typeBase()){
            if(consume(Token.codeOf("ID"))){
                if(arrayDecl()){

                }
                return true;
            }
            else{
                tkErr(crtTkIndex, "Missing ID");
            }
        }
        crtTkIndex = init;
        return false;
    }

    private boolean declStruct() {
        System.out.println("declStruct " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(consume(Token.tokens.indexOf("STRUCT"))){
            if(consume(Token.tokens.indexOf("ID"))){
                if(consume(Token.tokens.indexOf("LACC"))){
                    while(declVar()){

                    }
                    if(consume(Token.tokens.indexOf("RACC"))){
                        if(consume(Token.tokens.indexOf("SEMICOLON")))
                            return true;
                        else tkErr(crtTkIndex, "Missing SEMICOLON");
                    }
                    else{
                        tkErr(crtTkIndex, "Missing RACC");
                    }
                }
            }
        }
        crtTkIndex = init;
        return false;
    }

    public boolean consume(int code){
        if(tokensToAnalyse.get(crtTkIndex).getCode() ==  code){
            consumed = tokensToAnalyse.get(crtTkIndex);
            if(crtTkIndex < tokensToAnalyse.size())
                crtTkIndex++;
            System.out.println("consumed: " + consumed);
            return true;
        }
        return false;
    }
}
