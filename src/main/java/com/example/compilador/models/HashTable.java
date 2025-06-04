package com.example.compilador.models;

import java.util.HashMap;
import java.util.Map;

public class HashTable {
    public static final int size = 100;

    public static int hash(String token) {
        int hash = 0;
        for(int i=0; i<token.length();i++){
            hash=(31*hash+token.charAt(i))% size;
        }
        return Math.abs(hash);
    }
}
