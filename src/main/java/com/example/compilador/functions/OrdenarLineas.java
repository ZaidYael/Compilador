package com.example.compilador.functions;

import com.example.compilador.models.Token;

import java.util.Comparator;

public class OrdenarLineas implements Comparator<Token> {

    public int compare(Token o1, Token o2) {
        if(o1.getLine() <o2.getLine()) {
            return -1;
        }else if (o1.getLine() > o2.getLine()) {return 0;}
        else{return 1;}
    }
}
