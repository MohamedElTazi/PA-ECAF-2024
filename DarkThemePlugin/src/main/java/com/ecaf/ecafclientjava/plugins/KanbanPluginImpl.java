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
        kanbanBoard.setPadding(new Insets(20));
        kanbanBoard.setStyle("-fx-background-color: #e9ecef;");

        toDoColumn = createColumn("À faire");
        inProgressColumn = createColumn("En cours");
        doneColumn = createColumn("Terminé");

        kanbanBoard.getChildren().addAll(toDoColumn, inProgressColumn, doneColumn);

        return kanbanBoard;
    }

    private VBox createColumn(String title) {
        VBox column = new VBox(10);
        column.setPadding(new Insets(10));
        column.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        TextField titleField = new TextField(title);
        titleField.setEditable(false);
        titleField.setStyle("-fx-font-weight: bold; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 16px;");

        TextField newTaskField = new TextField();
        newTaskField.setPromptText("Nouvelle tâche");
        newTaskField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ced4da; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Button addButton = new Button("Ajouter");
        addButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        addButton.setOnMouseEntered(e -> addButton.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        addButton.setOnMouseExited(e -> addButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
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
        taskBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        TextField taskField = new TextField(taskText);
        taskField.setEditable(false);
        taskField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        Button moveRightButton = new Button(">");
        moveRightButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        moveRightButton.setOnMouseEntered(e -> moveRightButton.setStyle("-fx-background-color: #117a8b; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        moveRightButton.setOnMouseExited(e -> moveRightButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        moveRightButton.setOnAction(e -> moveTaskRight(taskBox));

        Button moveLeftButton = new Button("<");
        moveLeftButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        moveLeftButton.setOnMouseEntered(e -> moveLeftButton.setStyle("-fx-background-color: #117a8b; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        moveLeftButton.setOnMouseExited(e -> moveLeftButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
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
