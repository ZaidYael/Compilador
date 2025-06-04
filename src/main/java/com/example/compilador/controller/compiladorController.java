package com.example.compilador.controller;

import com.example.compilador.functions.Compilar;
import com.example.compilador.functions.Gymterpreter;
import com.example.compilador.functions.Parser;
import com.example.compilador.functions.Tokenizer;
import com.example.compilador.models.Token;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class compiladorController {
    @FXML
    private Label welcomeText;
    @FXML private CodeArea codigoTextArea;
    @FXML private Button btnNuevo;
    @FXML private Button btnAbrir;
    @FXML private Button btnGuardar;
    @FXML private Button btnCompilar;
    @FXML private TextArea texto;

    @FXML 
    public void initialize() {
        codigoTextArea.setParagraphGraphicFactory(LineNumberFactory.get(codigoTextArea));

        codigoTextArea.textProperty().addListener((obs, oldText, newText) -> {
            codigoTextArea.setStyleSpans(0, computeHighlighting(newText));
        });
    }

    private static final String[] KEYWORDS = new String[] {
            "YeahBuddy", "SkinnyB", "showMeThat", "second",
            "light", "weight", "baby", "yup", "uuuu"
    };
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
    );
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKwEnd = 0;
        while (matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            if (matcher.group("KEYWORD") != null) {
                spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
            }
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @FXML
    private void abrirArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir archivo de código");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"),
                new FileChooser.ExtensionFilter("Archivos fuente", "*.gym")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(codigoTextArea.getScene().getWindow());

        if (archivoSeleccionado != null) {
            try {
                String contenido = Files.readString(archivoSeleccionado.toPath());
                codigoTextArea.replaceText(contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void guardarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo de código");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos .gym", "*.gym")
        );

        File archivoGuardar = fileChooser.showSaveDialog(codigoTextArea.getScene().getWindow());

        if (archivoGuardar != null) {
            try {
                String filePath = archivoGuardar.getAbsolutePath();
                if (!filePath.endsWith(".gym")) {
                    archivoGuardar = new File(filePath + ".gym");
                }

                Files.writeString(archivoGuardar.toPath(), codigoTextArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void compilarCodigo() {
        texto.setText(Gymterpreter.interpreter(codigoTextArea.getText()));
        Compilar compilar = new Compilar(codigoTextArea);
        compilar.compilar();


    }


    @FXML
    private void nuevoArchivo() {
        codigoTextArea.clear();
    }
}
