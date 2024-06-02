package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.Evenement;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VueCalendrier extends Dialog<Void> {

    private YearMonth currentYearMonth;
    private final GridPane calendarGrid;
    private final Label monthYearLabel;
    private final Map<LocalDate, Evenement> eventMap;

    public VueCalendrier() {
        this.setTitle("Sélection de Date");
        this.setHeaderText("Calendrier");

        currentYearMonth = YearMonth.now();
        calendarGrid = new GridPane();
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);

        // Set column constraints for equal width
        for (int i = 0; i < 7; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / 7); // each column takes up 1/7th of the width
            calendarGrid.getColumnConstraints().add(colConstraints);
        }

        monthYearLabel = new Label();
        monthYearLabel.setStyle("-fx-font-size: 20px;");

        Button prevMonthButton = new Button("<<");
        Button nextMonthButton = new Button(">>");

        prevMonthButton.setOnAction(e -> updateCalendar(currentYearMonth.minusMonths(1)));
        nextMonthButton.setOnAction(e -> updateCalendar(currentYearMonth.plusMonths(1)));

        HBox header = new HBox(prevMonthButton, monthYearLabel, nextMonthButton);
        header.setAlignment(Pos.CENTER);
        header.setSpacing(10);

        VBox vbox = new VBox(header, calendarGrid);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        this.getDialogPane().setContent(vbox);
        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        eventMap = new HashMap<>();
        // Ajouter des événements de test
        eventMap.put(LocalDate.now(), new Evenement(LocalDate.now(), "Événement Test"));

        updateCalendar(currentYearMonth);
    }

    private void updateCalendar(YearMonth yearMonth) {
        currentYearMonth = yearMonth;
        monthYearLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE) + " " + currentYearMonth.getYear());

        calendarGrid.getChildren().clear();

        String[] daysOfWeek = {"dimanche", "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-font-weight: bold;");
            dayLabel.setAlignment(Pos.CENTER);
            calendarGrid.add(dayLabel, i, 0);
            GridPane.setHalignment(dayLabel, HPos.CENTER);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int row = 1;
        int col = dayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setMinSize(40, 40); // Set minimum size to make buttons uniform
            dayButton.setOnAction(e -> showEventForDay(date));
            calendarGrid.add(dayButton, col, row);
            GridPane.setHalignment(dayButton, HPos.CENTER);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private void showEventForDay(LocalDate date) {
        Evenement event = eventMap.get(date);
        String message = (event != null) ? "Événement: " + event.getDescription() : "Aucun événement";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Événement du " + date);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        VueCalendrier vueCalendrier = new VueCalendrier();
        Stage stage = new Stage();
        vueCalendrier.initOwner(stage);
        vueCalendrier.showAndWait();
    }
}
