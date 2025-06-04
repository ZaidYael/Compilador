package com.example.compilador.functions;

import com.example.compilador.models.Token;
import com.example.compilador.Tokens;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private final StringBuffer result = new StringBuffer();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String parse() {
        while (!isAtEnd()) {
            parseStatement();
        }
        return result.toString();
    }

    private void parseStatement() {
        Token token = peek();
        if (match(Tokens.SHOW_ME_THAT)) {
            result.append(parseShow());
        } else if (match(Tokens.LIGHT, Tokens.WEIGHT, Tokens.BABY)) {
            parseDeclaration();
        } else {
            result.append(error(token, "Instrucción no válida o desconocida").getMessage()).append("\n");
            advance(); // Evita ciclo infinito si hay error
        }
    }

    private String parseShow() {
        if (match(Tokens.PARENTESIS_IZQ)) {
            Token name = consume(Tokens.IDENTIFICADOR, "Se esperaba un nombre de variable.");
            consume(Tokens.PARENTESIS_DER, "Se esperaba ')'.");
            consume(Tokens.PUNTO_Y_COMA, "Se esperaba ';'. al final");
            return "Mostrar variable: " + name.getTipo() + "\n";
        }
        return error(peek(), "Falta paréntesis después de showMeThat").getMessage() + "\n";
    }

    private void parseDeclaration() {
        Token type = previous();
        Token name = consume(Tokens.IDENTIFICADOR, "Se esperaba nombre de variable.");
        consume(Tokens.ASIGNACION, "Se esperaba '='.");
        Token value = advance(); // Mejora esto para soportar expresiones
        consume(Tokens.PUNTO_Y_COMA, "Se esperaba ';'. al final");
        result.append("Declaración: ").append(type.getTipo()).append(" ").append(name.getTipo())
                .append(" = ").append(value.getTipo()).append("\n");
    }

    private boolean match(Tokens... types) {
        for (Tokens type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(Tokens type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private boolean check(Tokens type) {
        if (isAtEnd()) return false;
        return peek().getTipo() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private RuntimeException error(Token token, String message) {
        return new RuntimeException("Error en línea " + token.getLine() + ": " + message);
    }
}

