package com.example.compilador.models;

import com.example.compilador.Tokens;

public class Token {
    private final String valor;
    private final Tokens tipo;
    private final int line, column;

    public Token(String valor, Tokens tipo, int line, int column) {
        this.valor = valor;
        this.tipo = tipo;
        this.line = line;
        this.column = column;
    }

    public String getValor() {
        return valor;
    }

    public Tokens getTipo() {
        return tipo;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Tokenizer{" +
                "valor='" + valor + '\'' +
                ", tipo=" + tipo +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
