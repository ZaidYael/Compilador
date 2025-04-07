package com.example.compilador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
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

    @FXML public void initialize() {
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

}

