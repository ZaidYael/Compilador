package com.example.compilador.models;

import com.example.compilador.Tokens;

public class Errors {
    private final String valor, tipe;
    private final Tokens tipo;
    private final int line, column;

    public Errors(String valor, Tokens tipo, int line, int column, String tipe) {
        this.valor = valor;
        this.tipo = tipo;
        this.line = line;
        this.column = column;
        this.tipe = tipe;
    }

    public String getTipe() {
        return tipe;
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
        return "Errors{" +
                "valor='" + valor + '\'' +
                ", tipo=" + tipo +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
