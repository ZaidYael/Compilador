package com.example.compilador.functions;

import com.example.compilador.models.Token;
import javafx.scene.control.TextArea;
import org.fxmisc.richtext.CodeArea;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class Compilar {
    String codigo;

    public Compilar(CodeArea codeArea) {
        this.codigo = codeArea.getText();


    }

    public void compilar() {
        try {
            new PrintWriter("tabla_simbolos.dat").close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Tokenizer tokenizer = new Tokenizer(codigo);
        List<Token> tokens = tokenizer.tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
