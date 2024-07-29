package com.ecaf.ecafclientjava.plugins;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import com.ecaf.ecafclientjava.plugins.note.NotePlugin;

public class NotePluginImpl implements NotePlugin {

    @Override
    public Pane getUI() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: #f8f9fa;");

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #ffffff; -fx-border-color: #ced4da; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        pane.setCenter(textArea);
        return pane;
    }
}
