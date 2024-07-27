package com.ecaf.ecafclientjava.plugins;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import com.ecaf.ecafclientjava.plugins.note.NotePlugin;

public class NotePluginImpl implements NotePlugin {

    @Override
    public Pane getUI() {
        BorderPane pane = new BorderPane();
        TextArea textArea = new TextArea();
        pane.setCenter(textArea);
        return pane;
    }
}
