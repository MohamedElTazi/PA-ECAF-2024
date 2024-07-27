package com.ecaf.ecafclientjava.plugins;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import com.ecaf.ecafclientjava.plugins.kanban.KanbanPlugin;

public class KanbanPluginImpl implements KanbanPlugin {

    private VBox toDoColumn;
    private VBox inProgressColumn;
    private VBox doneColumn;

    @Override
    public Pane getUI() {
        HBox kanbanBoard = new HBox(10);
        kanbanBoard.setPadding(new Insets(10));

        toDoColumn = createColumn("À faire");
        inProgressColumn = createColumn("En cours");
        doneColumn = createColumn("Terminé");

        kanbanBoard.getChildren().addAll(toDoColumn, inProgressColumn, doneColumn);

        return kanbanBoard;
    }

    private VBox createColumn(String title) {
        VBox column = new VBox(10);
        column.setPadding(new Insets(10));
        column.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        TextField titleField = new TextField(title);
        titleField.setEditable(false);
        titleField.setStyle("-fx-font-weight: bold; -fx-background-color: transparent; -fx-border-color: transparent;");

        TextField newTaskField = new TextField();
        newTaskField.setPromptText("Nouvelle tâche");

        Button addButton = new Button("Ajouter");
        addButton.setOnAction(e -> addTask(column, newTaskField));

        column.getChildren().addAll(titleField, newTaskField, addButton);

        return column;
    }

    private void addTask(VBox column, TextField newTaskField) {
        String taskText = newTaskField.getText();
        if (taskText == null || taskText.trim().isEmpty()) {
            return;
        }

        HBox taskBox = new HBox(10);
        taskBox.setPadding(new Insets(5));
        taskBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        TextField taskField = new TextField(taskText);
        taskField.setEditable(false);

        Button moveRightButton = new Button(">");
        moveRightButton.setOnAction(e -> moveTaskRight(taskBox));

        Button moveLeftButton = new Button("<");
        moveLeftButton.setOnAction(e -> moveTaskLeft(taskBox));

        taskBox.getChildren().addAll(moveLeftButton, taskField, moveRightButton);
        column.getChildren().add(column.getChildren().size() - 2, taskBox);

        newTaskField.clear();
    }

    private void moveTaskRight(HBox taskBox) {
        VBox parentColumn = (VBox) taskBox.getParent();
        if (parentColumn == toDoColumn) {
            toDoColumn.getChildren().remove(taskBox);
            inProgressColumn.getChildren().add(inProgressColumn.getChildren().size() - 2, taskBox);
        } else if (parentColumn == inProgressColumn) {
            inProgressColumn.getChildren().remove(taskBox);
            doneColumn.getChildren().add(doneColumn.getChildren().size() - 2, taskBox);
        }
    }

    private void moveTaskLeft(HBox taskBox) {
        VBox parentColumn = (VBox) taskBox.getParent();
        if (parentColumn == doneColumn) {
            doneColumn.getChildren().remove(taskBox);
            inProgressColumn.getChildren().add(inProgressColumn.getChildren().size() - 2, taskBox);
        } else if (parentColumn == inProgressColumn) {
            inProgressColumn.getChildren().remove(taskBox);
            toDoColumn.getChildren().add(toDoColumn.getChildren().size() - 2, taskBox);
        }
    }
}
