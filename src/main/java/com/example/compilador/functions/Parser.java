package com.example.compilador.functions;

import com.example.compilador.models.Token;
import com.example.compilador.Tokens;
import java.util.List;
import java.util.Set;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private final StringBuffer result = new StringBuffer();
    Set<Tokens> stopTokens = Set.of(Tokens.LIGHT, Tokens.SKINNY_B, Tokens.BABY, Tokens.YUP
    , Tokens.UUUU, Tokens.SHOW_ME_THAT, Tokens.SECOND, Tokens.YEAH_BUDDY, Tokens.WEIGHT
    ,Tokens.ONEMORE, Tokens.EOF, Tokens.NATY);

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String parse() {
        System.out.println("Entra a metodo parse");
        if (!match(Tokens.YEAH_BUDDY)) {
            return "Error: el programa debe comenzar con 'YeahBuddy'\n";
        }

        while (!check(Tokens.SKINNY_B) && !isAtEnd()) {
            String instruccion = parseStatement();
            result.append(instruccion);
            if (instruccion.contains("Error")) break;  // Detiene en caso de error
        }

        if (!match(Tokens.SKINNY_B)) {
            return "Error: el programa debe terminar con 'SkinnyB'\n";
        }
        if (!check(Tokens.EOF)) {
            return "Error: se encontró código después de 'SkinnyB'. No debe haber nada más.\n";
        }

        return result.toString();
    }

    private String parseStatement() {
        System.out.println("Entra a parseStatement");
        Token token = peek();
        System.out.println(token +"token a tratar");
        switch (token.getTipo()) {
            case SHOW_ME_THAT -> {
                return parseShow();
            }
            case LIGHT, WEIGHT, BABY -> {
                return parseDeclaration();
            }
            case SECOND -> {
                return parseSecond();
            }
            case ONEMORE -> {
                return parseOneMore();
            }
            case NATY -> {
                return parseCmon();
            }
            default -> {
                advance(); // Avanza 1 token inválido
                return error(token, "Error Instrucción no válida o desconocida").getMessage() + "\n";
            }
        }
    }

    private String parseShow() {
        System.out.println("Entra a parseShow");
        advance();
        if (match(Tokens.PARENTESIS_IZQ)) {
            Token name = consume(Tokens.IDENTIFICADOR, " Error Se esperaba un nombre de variable.");
            consume(Tokens.PARENTESIS_DER, "Error Se esperaba ')'.");
            consume(Tokens.PUNTO_Y_COMA, "Error Se esperaba ';' al final.");
            return "Mostrar variable: " + name.getTipo() + "\n";
        }
        return error(peek(), "Error Falta paréntesis después de showMeThat").getMessage() + "\n";
    }

    private String parseDeclaration() {
        System.out.println("Entra a parseDeclaration");
        Token type = advance();
        System.out.println(type.getTipo()+"Valor de variable type");
        Token name = consume(Tokens.IDENTIFICADOR, "Error Se esperaba nombre de variable.");
        System.out.println(name.getTipo()+"Valor de variable name");
        consume(Tokens.ASIGNACION, "Error Se esperaba '='.");
        String value = parseExpression();
        consume(Tokens.PUNTO_Y_COMA, "Error Se esperaba ';' al final.");
        return "Declaración: " + type.getTipo() + " " + name.getTipo() + " = " + value + "\n";
    }

    private String parseExpression() {
        advance();
        System.out.println("Entra a parseExpression");
        StringBuilder expr = new StringBuilder();
        while (!isAtEnd() && !check(Tokens.PUNTO_Y_COMA) && !stopTokens.contains(peek().getTipo())) {
            expr.append(advance().getTipo()).append(" ");
        }

        return expr.toString().trim();
    }

    private String parseSecond() {
        advance();
        System.out.println("Entra a parseSecond");
        consume(Tokens.PARENTESIS_IZQ, " Error Se esperaba '(' en second");
        Token inicio = consume(Tokens.NUMERO, "Error Se esperaba valor inicial");
        consume(Tokens.COMA, "Error Se esperaba ','");
        Token fin = consume(Tokens.NUMERO, "Error Se esperaba valor final");
        consume(Tokens.COMA, "Error Se esperaba ','");
        Token paso = consume(Tokens.NUMERO, "Error Se esperaba paso");
        consume(Tokens.PARENTESIS_DER, "Error Se esperaba ')'");
        consume(Tokens.LLAVE_IZQ, "Error Se esperaba '{'");

        StringBuilder cuerpo = new StringBuilder();
        while (!check(Tokens.LLAVE_DER) && !isAtEnd()) {
            cuerpo.append(parseStatement());
        }
        consume(Tokens.LLAVE_DER, "Error Se esperaba '}'");

        return "Ciclo for: desde " + inicio.getTipo() + " hasta " + fin.getTipo() +
                " paso " + paso.getTipo() + "\n" + cuerpo;
    }

    private String parseOneMore() {
        advance();
        System.out.println("Entra a parseOneMore");
        consume(Tokens.PARENTESIS_IZQ, "Error Se esperaba '(' en oneMore");
        Token condicion = consume(Tokens.IDENTIFICADOR, "Error Se esperaba condición");
        consume(Tokens.PARENTESIS_DER, "Error Se esperaba ')'");
        consume(Tokens.LLAVE_IZQ, "Error Se esperaba '{'");

        StringBuilder cuerpo = new StringBuilder();
        while (!check(Tokens.LLAVE_DER) && !isAtEnd()) {
            cuerpo.append(parseStatement());
        }
        consume(Tokens.LLAVE_DER, " Error Se esperaba '}'");

        return "While: condición " + condicion.getTipo() + "\n" + cuerpo;
    }

    private String parseCmon() {
        advance();
        System.out.println("Entra a parseCmon");
        consume(Tokens.PARENTESIS_IZQ, "Error Se esperaba '(' en cmon");
        Token condicion = consume(Tokens.IDENTIFICADOR, "Error Se esperaba condición");
        consume(Tokens.PARENTESIS_DER, "Error Se esperaba ')'");
        consume(Tokens.LLAVE_IZQ, "Error Se esperaba '{'");

        StringBuilder ifBody = new StringBuilder();
        while (!check(Tokens.LLAVE_DER) && !isAtEnd()) {
            ifBody.append(parseStatement());
        }
        consume(Tokens.LLAVE_DER, "Error Se esperaba '}' después del cuerpo if");

        if (!match(Tokens.ELSE)) {
            result.append(peek()+"Error Se esperaba 'else' después del if\n");
        }

        consume(Tokens.LLAVE_IZQ, "Error Se esperaba '{' en else");
        StringBuilder elseBody = new StringBuilder();
        while (!check(Tokens.LLAVE_DER) && !isAtEnd()) {
            elseBody.append(parseStatement());
        }
        consume(Tokens.LLAVE_DER, "Error Se esperaba '}' después del else");

        return "If con condición " + condicion.getTipo() + "\n" + ifBody + "Else:\n" + elseBody;
    }

    // Helpers

    private boolean match(Tokens... types) {
        System.out.println("Entra a match");
        for (Tokens type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(Tokens type, String message) {
        System.out.println("Entra a consume");
        if (check(type)) return advance();
        result.append(peek()+": " +message+"\n");
        return peek();
    }

    private boolean check(Tokens type) {
        System.out.println("Entra a check");
        if (isAtEnd()) return false;
        System.out.println(peek().getTipo()+" Valor de peek()getTipo");
        return peek().getTipo() == type;
    }

    private Token advance() {
        System.out.println("Entra a advance");
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        System.out.println("Entra a isAtEnd");
        System.out.println(tokens.size()+"token size");
        System.out.println(current+"current token size");
        return current >= tokens.size();
    }

    private Token peek() {
        System.out.println("Entra a peek");
        if (isAtEnd()) {
            return new Token("", Tokens.EOF, -1, -1); // Token dummy para EOF
        }
        return tokens.get(current);
    }


    private Token previous() {
        System.out.println("Entra a previous");
        return tokens.get(current - 1);
    }
    private Token next() {
        System.out.println("Entra a next");
        return tokens.get(current + 1);
    }

    private RuntimeException error(Token token, String message) {
        return new RuntimeException("Error en línea " + token.getLine() + ": " + message);
    }
}
