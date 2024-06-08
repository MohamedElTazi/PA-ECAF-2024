package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.AG;
import com.ecaf.ecafclientjava.entites.Evenement;
import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.technique.Theme;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class VueCalendrier extends VBox {

    private YearMonth currentYearMonth;
    private final GridPane calendarGrid;
    private final Label monthYearLabel;
    private final List<Evenement> evenements;
    private final List<Tache> taches;
    private final List<AG> ags;
    private final ListView<String> eventListView;

    public VueCalendrier(List<Evenement> evenements, List<Tache> taches, List<AG> ags) {
        this.evenements = evenements != null ? evenements : new ArrayList<>();
        this.taches = taches != null ? taches : new ArrayList<>();
        this.ags = ags != null ? ags : new ArrayList<>();
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
        calendarGrid.setPrefSize(500, 300);
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
        applyCurrentTheme();
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();
        monthYearLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());

        DayOfWeek[] daysOfWeek = {
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
        };
        for (int col = 0; col < daysOfWeek.length; col++) {
            Label dayLabel = new Label(daysOfWeek[col].getDisplayName(java.time.format.TextStyle.FULL, Locale.FRANCE));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            calendarGrid.add(dayLabel, col, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = (firstOfMonth.getDayOfWeek().getValue() + 6) % 7; // Convert to Monday=0, Tuesday=1, ..., Sunday=6

        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate date = currentYearMonth.atDay(day);
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setMaxWidth(Double.MAX_VALUE);
            dayButton.setMaxHeight(Double.MAX_VALUE);

            if (hasEvent(date) || hasTask(date) || hasAg(date)) {
                dayButton.setStyle("-fx-background-color: lightgreen;");
            }
            dayButton.setOnAction(e -> handleDayClick(date));

            int col = (dayOfWeek + day - 1) % 7;
            int row = (dayOfWeek + day - 1) / 7 + 1;
            calendarGrid.add(dayButton, col, row);
        }
    }

    private boolean hasEvent(LocalDate date) {
        return evenements.stream()
                .anyMatch(event -> LocalDate.ofInstant(event.getDate(), ZoneId.systemDefault()).equals(date));
    }
    private boolean hasTask(LocalDate date) {
        return taches.stream()
                .anyMatch(task -> LocalDate.ofInstant(task.getDateDebut(), ZoneId.systemDefault()).equals(date));
    }
    private boolean hasAg(LocalDate date) {
        return ags.stream()
                .anyMatch(ags -> LocalDate.ofInstant(ags.getDate(), ZoneId.systemDefault()).equals(date));
    }

    private void handleDayClick(LocalDate date) {
        List<String> eventDetails = evenements.stream()
                .filter(event -> LocalDate.ofInstant(event.getDate(), ZoneId.systemDefault()).equals(date))
                .map(event -> "Événement : " + event.getNom() + " : " + event.getDescription())
                .collect(Collectors.toList());

        List<String> taskDetails = taches.stream()
                .filter(task -> LocalDate.ofInstant(task.getDateDebut(), ZoneId.systemDefault()).equals(date))
                .map(task -> "Tâche : " + task.getDescription())
                .collect(Collectors.toList());

        List<String> agDetails = ags.stream()
                .filter(ag -> LocalDate.ofInstant(ag.getDate(), ZoneId.systemDefault()).equals(date))
                .map(ag -> "AG : " + ag.getDescription())
                .collect(Collectors.toList());

        eventListView.getItems().setAll(eventDetails);
        eventListView.getItems().addAll(taskDetails);
        eventListView.getItems().addAll(agDetails);
    }

    private void changeMonth(int months) {
        currentYearMonth = currentYearMonth.plusMonths(months);
        updateCalendar();
    }

    public void applyCurrentTheme() {
        this.getStylesheets().clear();
        this.getStylesheets().add(getClass().getResource(Theme.themeVueCalendrier).toExternalForm());
        this.setStyle("-fx-background-color: " + Theme.backgroudColorMain + ";");
    }
}
