package com.example.compilador.functions;

import com.example.compilador.Tokens;
import com.example.compilador.models.Token;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Tokenizer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Tokenizer(String source) {
        this.source = source;
        System.out.println(source+'c');
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token("", Tokens.SKINNY_B, line, current));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(': addToken(Tokens.PARENTESIS_IZQ); break;
            case ')': addToken(Tokens.PARENTESIS_DER); break;
            case '+': addToken(Tokens.SUMA); break;
            case '-': addToken(Tokens.RESTA); break;
            case '*': addToken(Tokens.MULTIPLICACION); break;
            case '/': addToken(Tokens.DIVISION); break;
            case '=': addToken(Tokens.ASIGNACION); break;
            case ';': addToken(Tokens.PUNTO_Y_COMA); break;
            case '\n': line++; break;
            case ' ':
            case '\r':
            case '\t':
                break; // Ignorar espacios
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    addToken(Tokens.ERROR, "Carácter inesperado: " + c);
                }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        System.out.println(text);
        switch (text) {
            case "light": addToken(Tokens.LIGHT); break;
            case "weight": addToken(Tokens.WEIGHT); break;
            case "baby": addToken(Tokens.BABY); break;
            case "showMeThat": addToken(Tokens.SHOW_ME_THAT); break;
            case "YeahBuddy": addToken(Tokens.YEAH_BUDDY); break;
            case "SkinnyB": addToken(Tokens.SKINNY_B); break;
            case "yup": addToken(Tokens.YUP); break;
            case "uuuu": addToken(Tokens.UUUU); break;
            case "true":
            case "false":
                addToken(Tokens.BOOLEANO, text);
                guardarEnTabla(text);
                break;
            default:
                addToken(Tokens.IDENTIFICADOR, text);
                guardarEnTabla(text);
                break;
        }
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consume '.'
            while (isDigit(peek())) advance();
        }

        String numberText = source.substring(start, current);
        addToken(Tokens.NUMERO, numberText);
        guardarEnTabla(numberText);
    }

    private void guardarEnTabla(String lexema) {
        try {
            SymbolTable.guardar(lexema);
        } catch (IOException e) {
            System.err.println("Error al guardar en tabla de símbolos: " + e.getMessage());
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(Tokens type) {
        addToken(type, source.substring(start, current));
    }

    private void addToken(Tokens type, String value) {
        tokens.add(new Token(value, type, line, start));
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
