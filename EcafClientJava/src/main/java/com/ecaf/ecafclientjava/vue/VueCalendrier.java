package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.Evenement;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class VueCalendrier extends VBox {

    private YearMonth currentYearMonth;
    private final GridPane calendarGrid;
    private final Label monthYearLabel;
    private final List<Evenement> evenements;
    private final ListView<String> eventListView;

    public VueCalendrier(List<Evenement> evenements) {
        this.evenements = evenements;
        currentYearMonth = YearMonth.now();

        // Header with navigation and month/year label
        HBox header = new HBox();
        Button prevMonthButton = new Button("<<");
        prevMonthButton.setOnAction(e -> changeMonth(-1));
        Button nextMonthButton = new Button(">>");
        nextMonthButton.setOnAction(e -> changeMonth(1));
        monthYearLabel = new Label();
        header.getChildren().addAll(prevMonthButton, monthYearLabel, nextMonthButton);
        header.setAlignment(Pos.CENTER);
        HBox.setHgrow(monthYearLabel, Priority.ALWAYS);
        monthYearLabel.setMaxWidth(Double.MAX_VALUE);
        monthYearLabel.setAlignment(Pos.CENTER);
        monthYearLabel.setFont(new Font("Arial", 24));

        // Calendar grid
        calendarGrid = new GridPane();
        calendarGrid.setGridLinesVisible(true);
        calendarGrid.setAlignment(Pos.CENTER);
        calendarGrid.setPrefSize(800, 600);
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 7);
            calendarGrid.getColumnConstraints().add(col);
        }

        for (int i = 0; i < 7; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 7);
            calendarGrid.getRowConstraints().add(row);
        }

        // Event list view
        eventListView = new ListView<>();
        eventListView.setPrefHeight(200);

        updateCalendar();

        this.getChildren().addAll(header, calendarGrid, eventListView);
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();
        monthYearLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());

        String[] daysOfWeek = {"dimanche", "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"};
        for (int col = 0; col < daysOfWeek.length; col++) {
            Label dayLabel = new Label(daysOfWeek[col]);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            calendarGrid.add(dayLabel, col, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Convert to Sunday=0, Monday=1, ..., Saturday=6

        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate date = currentYearMonth.atDay(day);
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setMaxWidth(Double.MAX_VALUE);
            dayButton.setMaxHeight(Double.MAX_VALUE);

            if (hasEvent(date)) {
                dayButton.setStyle("-fx-background-color: lightgreen;");
            }

            dayButton.setOnAction(e -> handleDayClick(date));

            int col = (dayOfWeek + day - 1) % 7;
            int row = (dayOfWeek + day - 1) / 7 + 1;
            calendarGrid.add(dayButton, col, row);
        }
    }

    private boolean hasEvent(LocalDate date) {
        return evenements.stream().anyMatch(event -> event.getDate().equals(date));
    }

    private void handleDayClick(LocalDate date) {
        List<String> eventTitles = evenements.stream()
                .filter(event -> event.getDate().equals(date))
                .map(Evenement::getTitre)
                .collect(Collectors.toList());
        eventListView.getItems().setAll(eventTitles);
    }

    private void changeMonth(int months) {
        currentYearMonth = currentYearMonth.plusMonths(months);
        updateCalendar();
    }
}
