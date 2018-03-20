package com.lftc;

import java.util.LinkedList;
import java.util.ListIterator;

public class SyntaxAnalyser {
    private LinkedList<Token> tokensToAnalyse;
    private Token consumed;
    private ListIterator<Token> tokenIterator = null;

    public SyntaxAnalyser(LinkedList<Token> tokensToAnalyse){
        this.tokensToAnalyse = tokensToAnalyse;
        this.tokenIterator = tokensToAnalyse.listIterator();
    }

    public boolean consume(int code){
        if(tokenIterator.next().getCode() ==  code){
            consumed = tokensToAnalyse.get(tokenIterator.nextIndex() - 1);
            //System.out.println("consumed: " + consumed);
            return true;
        }
        return false;
    }
}
