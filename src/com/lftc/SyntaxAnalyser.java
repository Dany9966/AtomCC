package com.lftc;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

public class SyntaxAnalyser {
    private ArrayList<Token> tokensToAnalyse;
    private Token consumed;
    private int crtTkIndex;
    private ListIterator<Symbol> symbolListIterator;
    private ArrayList<Symbol> symbols = new ArrayList<Symbol>();
    private int crtDepth = 0;
    private Symbol crtFunc;
    private Symbol crtStruct;
    private Symbol foundSymbol;
    private static Map<String,Integer> typeMap = Map.of("TB_CHAR", 1, "TB_INT", 2, "TB_DOUBLE", 3);


    public SyntaxAnalyser(ArrayList<Token> tokensToAnalyse){
        this.tokensToAnalyse = tokensToAnalyse;
    }

    public void tkErr(int index, String s){
        System.out.println(s + " Error on line: " + tokensToAnalyse.get(index).getLine());
        System.exit(1);
    }

    public void addExtFunc(String name, SymType type){
        Symbol s = new Symbol(name, "CLS_EXTFUNC", crtDepth, type);
        s.setArgs(new ArrayList<Symbol>());
        symbols.add(s);
    }

    public void addFuncArg(Symbol func, String name, SymType type){
        func.getArgs().add(new Symbol(name, "CLS_VAR", crtDepth, type));
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

        // adding void put_s(char s[]) function
        addExtFunc("put_s", new SymType("TB_VOID", -1));
        addFuncArg(symbols.get(symbols.size() - 1), "s", new SymType("TB_CHAR", 0));

        // adding void get_s(char s[]) function
        addExtFunc("get_s", new SymType("TB_VOID", -1));
        addFuncArg(symbols.get(symbols.size() - 1), "s", new SymType("TB_CHAR", 0));

        // adding void put_i(int i) function
        addExtFunc("put_i", new SymType("TB_VOID", -1));
        addFuncArg(symbols.get(symbols.size() - 1), "i", new SymType("TB_INT", -1));

        // adding int get_i() function
        addExtFunc("get_i", new SymType("TB_INT", -1));

        // adding void put_d(double d) function
        addExtFunc("put_d", new SymType("TB_VOID", -1));
        addFuncArg(symbols.get(symbols.size() - 1), "d", new SymType("TB_DOUBLE", -1));

        // adding double get_d() function
        addExtFunc("get_d", new SymType("TB_DOUBLE", -1));

        // adding void put_c(char c) function
        addExtFunc("put_c", new SymType("TB_VOID", -1));
        addFuncArg(symbols.get(symbols.size() - 1), "c", new SymType("TB_CHAR", -1));

        // adding char get_c() function
        addExtFunc("get_c", new SymType("TB_CHAR", -1));

        // adding double seconds() function
        addExtFunc("seconds", new SymType("TB_DOUBLE", -1));

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
        SymType t = typeBase();
        if(t != null){
            if(consume(Token.codeOf("ID"))){
                String tkName = consumed.getContent();
                if(arrayDecl(t) != null){
                    t.setNElements(0);
                }
                else t.setNElements(-1);
                addVar(tkName, t);
                while(true){
                    if(consume(Token.codeOf("COMMA"))){
                        if(consume(Token.codeOf("ID"))){
                            tkName = consumed.getContent();
                            if(arrayDecl(t) != null){
                                t.setNElements(0);
                            }
                            else t.setNElements(-1);
                            addVar(tkName, t);
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

    private SymType arrayDecl(SymType ret) {
        System.out.println("arrayDecl " + tokensToAnalyse.get(crtTkIndex));
        // SymType ret;
        int init = crtTkIndex;
        if(consume(Token.codeOf("LBRACKET"))){
            RetVal rv = expr();
            if(rv != null){
                if(!rv.isCtVal()){
                    tkErr(crtTkIndex, "the array size is not a constant");
                }

                if(!rv.getType().getTypebase().equals("TB_INT")){
                    tkErr(crtTkIndex, "the array size is not an integer");
                }

                ret.setNElements((int)rv.getCtVal().getI());
            }
            else {
                ret.setNElements(0);
            }
            if(consume(Token.codeOf("RBRACKET")))
                return ret;
            else tkErr(crtTkIndex, "Missing RBRACKET");
        }
        crtTkIndex = init;
        return null;
    }

    private RetVal expr() {
        System.out.println("expr " + tokensToAnalyse.get(crtTkIndex));
        return exprAssign();
    }

    private RetVal exprAssign() { // returns RetVal ret
        System.out.println("exprAssign " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        RetVal rv = exprUnary();

        if(rv != null){
            if(consume(Token.codeOf("ASSIGN"))){
                RetVal rve = exprAssign();
                if(rve != null) {
                    if(!rv.isLVal()){
                        tkErr(crtTkIndex, "cannot assign to a non-lval");
                    }
                    if(rv.getType().getNElements() > -1 ||
                            rve.getType().getNElements() > -1){
                        tkErr(crtTkIndex, "the arrays cannot be assigned");
                    }

                    rv.getType().castTo(rve.getType());
//                     rv.setLVal(false);
//                     rv.setCtVal(false);
                    return rv;
                }
            }
        }

        crtTkIndex = init;
        return exprOr();
    }

    private RetVal exprOr() {
        System.out.println("exprOr " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        RetVal rv = exprAnd();
        if(rv != null){
            return exprOr1(rv);

        }
        crtTkIndex = init;
        return null;
    }

    private RetVal exprOr1(RetVal rv) {
        System.out.println("exprOr1 " + tokensToAnalyse.get(crtTkIndex));
        if(consume(Token.codeOf("OR"))){
            RetVal rve = exprAnd();
            if(rve != null){
                if(rv.getType().getTypebase().equals("TB_STRUCT") ||
                        rve.getType().getTypebase().equals("TB_STRUCT")){
                    tkErr(crtTkIndex, "a structure cannot be logically tested");
                }
                rv.setType(new SymType("TB_INT", -1));
//                rv.setCtVal(false);
//                rv.setLVal(false);

                if(exprOr1(rv) != null){}
            }else{
                tkErr(crtTkIndex, "Missing OR element");
            }
        }
        return rv;
    }

    private RetVal exprAnd(){
        System.out.println("exprAnd " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        RetVal rv = exprEq();
        if(rv != null){
            return exprAnd1(rv);
        }
        crtTkIndex = init;
        return null;
    }

    private RetVal exprAnd1(RetVal rv) {
        System.out.println("exprAnd1 " + tokensToAnalyse.get(crtTkIndex));
        if(consume(Token.codeOf("AND"))){
            RetVal rve = exprEq();
            if(rve != null){
                if(rv.getType().getTypebase().equals("TB_STRUCT") ||
                        rve.getType().getTypebase().equals("TB_STRUCT")){
                    tkErr(crtTkIndex, "a structure cannot be logically tested");
                }
                rv.setType(new SymType("TB_INT", -1));
//                rv.setCtVal(false);
//                rv.setLVal(false);

            if(exprAnd1(rv) != null){}

            }
            else {
                tkErr(crtTkIndex, "Missing AND element");
                return null;
            }
        }

        return rv;
    }

    private RetVal exprEq() {
        System.out.println("exprEq " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        RetVal rv = exprRel();
        if(rv != null){
            return exprEq1(rv);
        }
        crtTkIndex = init;
        return null;
    }

    private RetVal exprEq1(RetVal rv) {
        System.out.println("exprEq1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;
        if(consume(Token.codeOf("EQUAL")) || consume(Token.codeOf("NOTEQ"))){
            RetVal rve = exprRel();
            if(rve != null){
                if(rv.getType().getTypebase().equals("TB_STRUCT") ||
                        rve.getType().getTypebase().equals("TB_STRUCT")){
                    tkErr(crtTkIndex, "a structure cannot be compared");
                }
                rv.setType(new SymType("TB_INT", -1));
//                rv.setCtVal(false);
//                rv.setLVal(false);

                if(exprEq1(rv) != null){

                }
            }
            else {
                tkErr(crtTkIndex, "Missing relation");
                return null;
            }
        }
        // crtTkIndex = init;
        return rv;
    }

    private RetVal exprRel() {
        System.out.println("exprRel " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        RetVal rv = exprAdd();
        if(rv != null){
            return exprRel1(rv);
        }
        crtTkIndex = init;
        return null;
    }

    private RetVal exprRel1(RetVal rv) {
        System.out.println("exprRel1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;
        if(consume(Token.codeOf("LESS")) || consume(Token.codeOf("LESSEQ")) ||
                consume(Token.codeOf("GREATER")) || consume(Token.codeOf("GREATEREQ"))){
            RetVal rve = exprAdd();
            if(rve != null){
                if(rv.getType().getNElements() > -1 || rve.getType().getNElements() > -1)
                    tkErr(crtTkIndex, "an array cannot be compared");

                if(rv.getType().getTypebase().equals("TB_STRUCT") ||
                        rve.getType().getTypebase().equals("TB_STRUCT")){
                    tkErr(crtTkIndex, "a structure cannot be compared");
                }
                rv.setType(new SymType("TB_INT", -1));
//                rv.setCtVal(false);
//                rv.setLVal(false);

                if(exprRel1(rv) != null){

                }
            }else{
                tkErr(crtTkIndex, "Missing relation");
                return null;
            }
        }
        //crtTkIndex = init;

        return rv;
    }

    private RetVal exprAdd() {
        System.out.println("exprAdd " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        RetVal rv = exprMul();
        if(rv != null){
            return exprAdd1(rv);
        }
        crtTkIndex = init;
        return null;
    }

    private RetVal exprMul() {
        System.out.println("exprMul " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        RetVal rv = exprCast();
        if(rv != null){
            return exprMul1(rv);
        }

        crtTkIndex = init;
        return null;
    }

    private RetVal exprMul1(RetVal rv) {
        System.out.println("exprMul1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;
        if(consume(Token.codeOf("MUL")) || consume(Token.codeOf("DIV"))){
            RetVal rve = exprCast();
            if(rve != null){
                if(rv.getType().getNElements() > -1 || rve.getType().getNElements() > -1)
                    tkErr(crtTkIndex, "an array cannot be multiplied or divided");

                if(rv.getType().getTypebase().equals("TB_STRUCT") ||
                        rve.getType().getTypebase().equals("TB_STRUCT")){
                    tkErr(crtTkIndex, "a structure cannot be multiplied or divided");
                }
                rv.setType(getArithType(rv.getType(), rve.getType()));
//                rv.setCtVal(false);
//                rv.setLVal(false);

                if(exprMul1(rv) != null){

                }
            }else{
                tkErr(crtTkIndex, "Missing cast expr");
                return null;
            }
        }

        return rv;
    }

    private RetVal exprCast() {
        System.out.println("exprCast " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(consume(Token.codeOf("LPAR"))){
            SymType t = typeName();
            if(t != null){
                if(consume(Token.codeOf("RPAR"))){
                    RetVal rve = exprCast();
                    if(rve != null){
                        t.castTo(rve.getType());
                        return new RetVal(t, false, false);
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
            RetVal rv = exprUnary();
            if(rv != null)
                return rv;
        }

        crtTkIndex = init;
        return null;
    }

    private SymType typeName() {
        System.out.println("typeName " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        SymType t;
        if((t = typeBase()) != null){
            if(arrayDecl(t) != null){
                return t;
            }
            else {
                t.setNElements(-1);
                return t;
            }

        }

        crtTkIndex = init;
        return null;
    }

    private RetVal exprAdd1(RetVal rv) {
        System.out.println("exprAdd1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;

        if(consume(Token.codeOf("ADD")) || consume(Token.codeOf("SUB"))){
            RetVal rve = exprMul();
            if(rve != null){
                if(rv.getType().getNElements() > -1 || rve.getType().getNElements() > -1)
                    tkErr(crtTkIndex, "an array cannot be added or subtracted");

                if(rv.getType().getTypebase().equals("TB_STRUCT") ||
                        rve.getType().getTypebase().equals("TB_STRUCT")){
                    tkErr(crtTkIndex, "a structure cannot be added or subtracted");
                }
                rv.setType(getArithType(rv.getType(), rve.getType()));
//                rv.setCtVal(false);
//                rv.setLVal(false);

                if(exprAdd1(rv) != null){

                }
            }
            else{
                tkErr(crtTkIndex, "Missing mul expression");
                return null;
            }
        }
        return rv;
    }


    private RetVal exprUnary() {
        System.out.println("exprUnary " + tokensToAnalyse.get(crtTkIndex));
        RetVal rv;
        if(consume(Token.codeOf("SUB")) || consume(Token.codeOf("NOT"))){
            int tkop = consumed.getCode();
            rv = exprUnary();
            if(rv != null){
                if(tkop == Token.tokens.indexOf("SUB")){
                    if(rv.getType().getNElements() >= 0)
                        tkErr(crtTkIndex, "unary '-' cannot be applied to array");
                    if(rv.getType().getTypebase().equals("TB_STRUCT"))
                        tkErr(crtTkIndex, "unary '-' cannot be applied to a struct");

                } else {
                    if(rv.getType().getTypebase().equals("TB_STRUCT"))
                        tkErr(crtTkIndex, "'!' cannot be applied to structures");

                    rv.setType(new SymType("TB_INT", -1));
                }

//                rv.setLVal(false);
//                rv.setCtVal(false);

            }
        }
        else return exprPostfix();

        return rv;
    }

    private RetVal exprPostfix() {
        System.out.println("exprPostfix " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        RetVal rv = exprPrimary();
        if(rv != null){
            return exprPostfix1(rv);
        }

        crtTkIndex = init;
        return null;
    }

    private RetVal exprPrimary() {
        System.out.println("exprPrimary " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(consume(Token.codeOf("ID"))){
            String tkName = consumed.getContent();
            findSymbol(symbols, tkName);
            if(foundSymbol == null){
                tkErr(crtTkIndex, "undefined symbol: " + tkName);
            }
            RetVal rv = new RetVal(foundSymbol.getType(), true, false);
            if(consume(Token.codeOf("LPAR"))){
                ListIterator<Symbol> crtDefArg = foundSymbol.getArgs().listIterator();
                if(!foundSymbol.getCls().equals("CLS_FUNC") && !foundSymbol.getCls().equals("CLS_EXTFUNC")){
                    tkErr(crtTkIndex, "call of the non-function: " + tkName);
                }
                RetVal arg = expr();
                if(arg != null){
                    if(!crtDefArg.hasNext()){
                        tkErr(crtTkIndex, "too many arguments in call");
                    }

                    crtDefArg.next().getType().castTo(arg.getType());


                    while(true){
                        if(consume(Token.codeOf("COMMA"))){
                            arg = expr();
                            if(arg != null){
                                if(!crtDefArg.hasNext()){
                                    tkErr(crtTkIndex, "too many arguments in call");
                                }

                                foundSymbol.getType().castTo(arg.getType());
                                crtDefArg.next();
                            }
                            else{
                                tkErr(crtTkIndex, "Missing expression after COMMA");
                            }

                        }
                        else{
                            break;
                        }
                    }
                }
                if(consume(Token.codeOf("RPAR"))){
                    if(crtDefArg.hasNext())
                        tkErr(crtTkIndex, "too few arguments in call");

                    rv.setType(foundSymbol.getType());
//                    rv.setCtVal(false);
//                    rv.setLVal(false);

                }
                else{
                    tkErr(crtTkIndex, "Missing RPAR");
                }
            }
            else {
                if(foundSymbol.getCls().equals("CLS_FUNC") || foundSymbol.getCls().equals("CLS_EXTFUNC")){
                    tkErr(crtTkIndex, "missing call for function " + tkName);
                }
            }

            return rv;
        }
        else if(consume(Token.codeOf("CT_INT")))
            return new RetVal(new SymType("TB_INT", -1),
                    new CtVal(Long.parseLong(consumed.getContent())),
                    false,
                    true
            );

        else if(consume(Token.codeOf("CT_REAL")))
            return new RetVal(
                    new SymType("TB_DOUBLE", -1),
                    new CtVal(Double.parseDouble(consumed.getContent())),
                    false,
                    true
            );

        else if(consume(Token.codeOf("CT_CHAR")))
            return new RetVal(
                    new SymType("TB_CHAR", -1),
                    new CtVal(consumed.getContent()),
                    false,
                    true
            );

        else if(consume(Token.codeOf("CT_STRING")))
            return new RetVal(
                    new SymType("TB_CHAR", 0),
                    new CtVal(consumed.getContent()),
                    false,
                    true
            );

        else if(consume(Token.codeOf("LPAR"))){
            RetVal rv = expr();
            if(rv != null){
                if(consume(Token.codeOf("RPAR")))
                    return rv;
            }
        }

        crtTkIndex = init;
        return null;
    }

    private RetVal exprPostfix1(RetVal rv) {
        System.out.println("exprPostfix1 " + tokensToAnalyse.get(crtTkIndex));
        //int init = crtTkIndex;

        if(consume(Token.codeOf("LBRACKET"))){
            RetVal rve = expr();
            if(rve != null){
                if(rv.getType().getNElements() < 0)
                    tkErr(crtTkIndex, "only an array can be indexed");

                SymType typeInt = new SymType("TB_INT", -1);
                typeInt.castTo(rve.getType());

                // rv.getType().setNElements(-1);
//                rv.setCtVal(false);
//                rv.setLVal(true);

                if(consume(Token.codeOf("RBRACKET"))){
                    if(exprPostfix1(rv) != null){
                        return rv;
                    }
                }
                else{
                    tkErr(crtTkIndex, "Missing RBRACKET");
                }
            }
            else{
                tkErr(crtTkIndex, "Missing expression after LBRACKET");
            }
        }
        else{
            if(consume(Token.codeOf("DOT"))){
                if(consume(Token.codeOf("ID"))){
                    String tkName = consumed.getContent();
                    Symbol sStruct = rv.getType().getS();
                    findSymbol(sStruct.getMembers(), tkName);
                    if(foundSymbol == null){
                        tkErr(crtTkIndex, "struct " + sStruct.getName() + " does not have a member " + tkName);
                    }

                    rv.setType(foundSymbol.getType());
//                    rv.setLVal(true);
//                    rv.setCtVal(false);

                    if(exprPostfix1(rv) != null){
                        return rv;
                    }
                }
                else{
                    tkErr(crtTkIndex, "Missing ID after DOT");
                    return null;
                }
            }
        }

        //crtTkIndex = init;
        return rv;
    }


    private SymType typeBase() {
        System.out.println("typeBase " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;

        if(consume(Token.codeOf("INT"))){
            return new SymType("TB_INT");
        } else if (consume(Token.codeOf("DOUBLE"))){
            return new SymType("TB_DOUBLE");
        } else if(consume(Token.codeOf("CHAR"))) {
            return new SymType("TB_CHAR");
        }
        else if(consume(Token.codeOf("STRUCT"))){
            if(consume(Token.codeOf("ID"))){
                String tkName = consumed.getContent();
                findSymbol(symbols, tkName);
                if(foundSymbol == null){
                    tkErr(crtTkIndex, "undefined symbol: " + tkName);
                }
                if(!(foundSymbol.getCls().equals("CLS_STRUCT"))){
                    tkErr(crtTkIndex, tkName + " is not a struct");
                }

                return new SymType("TB_STRUCT", foundSymbol);
            }

            else{
                tkErr(crtTkIndex, "Missing ID");
            }
        }

        crtTkIndex = init;
        return null;
    }

    private boolean declFunc() {
        System.out.println("declFunc " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        SymType t = typeBase();
        if(t != null){
            if(consume(Token.codeOf("MUL"))){
                t.setNElements(0);
            } else {
                t.setNElements(-1);
            }

        }
        else if(consume(Token.codeOf("VOID"))){
            t = new SymType("TB_VOID");
        }

        if(declFuncCommon(t)){
            return true;
        }

        crtTkIndex = init;
        return false;
    }

    private boolean declFuncCommon(SymType t){
        System.out.println("declFuncCommon " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        if(consume(Token.codeOf("ID"))){
            String tkName = consumed.getContent();
            if(consume(Token.codeOf("LPAR"))){
                if(findSymbol(symbols, tkName)){
                    tkErr(crtTkIndex, "symbol redefinition " + tkName);
                }

                crtFunc = new Symbol(tkName, "CLS_FUNC", crtDepth, t);
                crtFunc.setMembers(new ArrayList<Symbol>());
                symbols.add(crtFunc);
                crtDepth++;

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
                    crtDepth--;
                    if(stmCompound()) {
                        // deleteSymbolsAfter()
                        if(symbols.contains(crtFunc)) {
                            symbols.subList(symbols.indexOf(crtFunc) + 1, symbols.size()).clear();
                        }
                        return true;
                    }
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
        int lastIndex = symbols.size() - 1;
        if(consume(Token.codeOf("LACC"))){
            crtDepth++;
            while(true){
                if(declVar() || stm())
                    continue;
                break;
            }
            if(consume(Token.codeOf("RACC"))) {
                crtDepth--;
                symbols.subList(lastIndex + 1, symbols.size()).clear();
                return true;
            }
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
        RetVal rv;
        if(stmCompound())
            return true;
        else if(consume(Token.codeOf("IF"))){
            if(consume(Token.codeOf("LPAR"))){
                rv = expr();
                if(rv!= null){
                    if(rv.getType().getTypebase().equals("TB_STRUCT")){
                        tkErr(crtTkIndex, "a structure cannot be logically tested");
                    }

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
                rv = expr();
                if(rv != null){
                    if(rv.getType().getTypebase().equals("TB_STRUCT")){
                        tkErr(crtTkIndex, "a structure cannot be logically tested");
                    }

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
                RetVal rv1 = expr();
                if(rv1 != null){ }

                if(consume(Token.codeOf("SEMICOLON"))){
                    RetVal rv2 = expr();
                    if(rv2 != null){
                        if(rv2.getType().getTypebase().equals("TB_STRUCT")){
                            tkErr(crtTkIndex, "a structure cannot be logically tested");
                        }
                    }

                    if(consume(Token.codeOf("SEMICOLON"))){
                        expr();

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
            rv = expr();
            if(rv != null){
                if(crtFunc.getType().getTypebase().equals("TB_VOID")){
                    tkErr(crtTkIndex, "a void function cannot return a value");
                }
            }

            crtFunc.getType().castTo(rv.getType());
            if (consume(Token.codeOf("SEMICOLON"))){
                return true;
            }
            else{
                tkErr(crtTkIndex, "Missing \';\'");
            }
        }
        else {
            rv = expr();
            if (rv != null) {
            }


            if (consume(Token.codeOf("SEMICOLON")))
                return true;

        }

        crtTkIndex = init;

        return false;
    }

    private boolean funcArg() {
        System.out.println("funcArg " + tokensToAnalyse.get(crtTkIndex));
        int init = crtTkIndex;
        SymType t = typeBase();
        if(t != null){
            if(consume(Token.codeOf("ID"))){
                String tkName = consumed.getContent();
                if(arrayDecl(t) != null){

                }
                else {
                    t.setNElements(-1);
                }

                symbols.add(new Symbol(tkName, "CLS_VAR", crtDepth, t, "MEM_ARG"));
                crtFunc.setArgs(new ArrayList<Symbol>());
                crtFunc.getArgs().add(new Symbol(tkName, "CLS_VAR", crtDepth, t, "MEM_ARG"));
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
                // symTable

                String tkName = consumed.getContent();

                // end
                if(consume(Token.tokens.indexOf("LACC"))){
                    // symTable

                    if(findSymbol(symbols, tkName)){
                        tkErr(crtTkIndex, "symbol redefinition " + tkName);
                    }
                    crtStruct = new Symbol(tkName, "CLS_STRUCT", crtDepth);
                    crtStruct.setMembers(new ArrayList<Symbol>());
                    symbols.add(crtStruct);

                    // end
                    while(declVar()){

                    }

                    if(consume(Token.tokens.indexOf("RACC"))){
                        if(consume(Token.tokens.indexOf("SEMICOLON"))) {
                            crtStruct = null;
                            return true;
                        }
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

    public boolean findSymbol(ArrayList<Symbol> symlist, String name) {
        Symbol s;
        if(symlist != null) {
            for (int i = symlist.size() - 1; i >= 0; i--) {
                s = symlist.get(i);
                if (s.getName().equals(name)) {
                    foundSymbol = s;
                    return true;
                }
            }
        }
        foundSymbol = null;
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

    public void addVar(String tkName, SymType t){
        if(crtStruct != null){
            if(findSymbol(crtStruct.getMembers(), tkName)){
                tkErr(crtTkIndex, "symbol redefinition " + tkName);
            }

            crtStruct.getMembers().add(new Symbol(tkName, "CLS_VAR", crtDepth, t));
        }
        else if (crtFunc != null){
            if(findSymbol(symbols, tkName) && foundSymbol != null && foundSymbol.getDepth() == crtDepth){
                tkErr(crtTkIndex, "symbol redefinition " + tkName);
            }

            symbols.add(new Symbol(tkName, "CLS_VAR", crtDepth, t, "MEM_LOCAL"));
        }
        else {
            if(findSymbol(symbols, tkName)){
                tkErr(crtTkIndex, "symbol redefinition " + tkName);
            }

            symbols.add(new Symbol(tkName, "CLS_VAR", crtDepth, t, "MEM_GLOBAL"));
        }
    }

    public static SymType getArithType(SymType t1, SymType t2){
        SymType ret = null;
        String s1 = t1.getTypebase();
        String s2 = t2.getTypebase();

        if(typeMap.containsKey(s1)) {
            int i1 = typeMap.get(t1.getTypebase());
            if(typeMap.containsKey(s2)) {
                int i2 = typeMap.get(t2.getTypebase());

                if(i1 >= i2){
                    ret = t1;

                }
                else{
                    ret = t2;
                }

                ret.setNElements(-1);
            }
            else{
                System.out.println("Unconvertable type: " + t2.getTypebase());
                System.exit(-1);
            }
        }
        else{

            System.out.println("Unconvertable type: " + t1.getTypebase());
            System.exit(-1);
        }

        return ret;

    }

}
