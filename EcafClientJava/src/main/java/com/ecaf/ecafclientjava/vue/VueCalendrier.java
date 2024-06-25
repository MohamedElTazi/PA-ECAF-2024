package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.AG;
import com.ecaf.ecafclientjava.entites.Evenement;
import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.technique.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class VueCalendrier extends VBox {

    private YearMonth currentYearMonth;
    private final GridPane calendarGrid;
    private final Label monthYearLabel;
    private final List<Evenement> evenements;
    private final List<Tache> taches;
    private final List<AG> ags;

    public VueCalendrier(List<Evenement> evenements, List<Tache> taches, List<AG> ags) {
        this.evenements = evenements;
        this.taches = taches;
        this.ags = ags;
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
        calendarGrid.setPrefSize(700, 400); // Adjusted size to fit event names
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

        updateCalendar();

        this.getChildren().addAll(header, calendarGrid);
    }

    public List<Object> getCalendarActivity(){
        List<Object> e = new ArrayList<>();
        e.add(this.evenements);
        e.add(this.ags);
        e.add(this.taches);
        return e;
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
            Label dayLabel = new Label(daysOfWeek[col].getDisplayName(TextStyle.FULL, Locale.FRANCE));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            calendarGrid.add(dayLabel, col, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = (firstOfMonth.getDayOfWeek().getValue() + 6) % 7;

        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate date = currentYearMonth.atDay(day);

            VBox dayBox = new VBox();
            dayBox.setAlignment(Pos.TOP_CENTER);
            dayBox.setSpacing(5);

            Text dayText = new Text(String.valueOf(day));
            dayBox.getChildren().add(dayText);

            List<Evenement> dayEvents = evenements.stream()
                    .filter(event -> LocalDate.ofInstant(event.getDate(), ZoneId.systemDefault()).equals(date))
                    .sorted(Comparator.comparing(event -> LocalTime.from(event.getDate())))
                    .collect(Collectors.toList());

            for (int i = 0; i < Math.min(dayEvents.size(), 2); i++) {
                Evenement event = dayEvents.get(i);
                Rectangle eventRectangle = new Rectangle(60, 20);
                eventRectangle.setFill(Color.LIGHTBLUE);
                Text eventText = new Text(event.getNom().substring(0, Math.min(event.getNom().length(), 10)));
                StackPane eventPane = new StackPane(eventRectangle, eventText);
                eventPane.setAlignment(Pos.CENTER_LEFT);
                dayBox.getChildren().add(eventPane);
            }

            Rectangle dayRectangle = new Rectangle(100, 100);
            dayRectangle.setFill(Color.WHITE);
            dayRectangle.setStroke(Color.BLACK);

            StackPane dayPane = new StackPane(dayRectangle, dayBox);
            dayPane.setAlignment(Pos.TOP_CENTER);

            if (hasEvent(date) || hasTask(date) || hasAg(date)) {
                dayRectangle.setFill(Color.LIGHTGREEN);
            }

            dayPane.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    handleDayDoubleClick(date, e.getX(), e.getY());
                } else {
                    handleDayClick(date);
                }
            });

            int col = (dayOfWeek + day - 1) % 7;
            int row = (dayOfWeek + day - 1) / 7 + 1;
            calendarGrid.add(dayPane, col, row);
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
                .anyMatch(ag -> LocalDate.ofInstant(ag.getDate(), ZoneId.systemDefault()).equals(date));
    }

    private void handleDayClick(LocalDate date) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Map<LocalTime, String> eventsByHour = evenements.stream()
                .filter(event -> LocalDate.ofInstant(event.getDate(), ZoneId.systemDefault()).equals(date))
                .collect(Collectors.toMap(
                        event -> event.getDate().atZone(ZoneId.systemDefault()).toLocalTime().withMinute(0).withSecond(0).withNano(0),
                        event -> "Événement : " + event.getNom() + " - " + event.getDescription(),
                        (existing, replacement) -> existing 
                ));

        Map<LocalTime, String> tasksByHour = taches.stream()
                .filter(task -> LocalDate.ofInstant(task.getDateDebut(), ZoneId.systemDefault()).equals(date))
                .collect(Collectors.toMap(
                        task -> task.getDateDebut().atZone(ZoneId.systemDefault()).toLocalTime().withMinute(0).withSecond(0).withNano(0),
                        task -> "Tâche : " + task.getDescription(),
                        (existing, replacement) -> existing
                ));

        Map<LocalTime, String> agsByHour = ags.stream()
                .filter(ag -> LocalDate.ofInstant(ag.getDate(), ZoneId.systemDefault()).equals(date))
                .collect(Collectors.toMap(
                        ag -> ag.getDate().atZone(ZoneId.systemDefault()).toLocalTime().withMinute(0).withSecond(0).withNano(0),
                        ag -> "AG : " + ag.getDescription(),
                        (existing, replacement) -> existing
                ));

        List<String> eventDetails = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            String eventDetail = eventsByHour.getOrDefault(time, "");
            String taskDetail = tasksByHour.getOrDefault(time, "");
            String agDetail = agsByHour.getOrDefault(time, "");
            String detail = timeFormatter.format(time) + " - " +
                    (eventDetail.isEmpty() ? "" : eventDetail + "\n") +
                    (taskDetail.isEmpty() ? "" : taskDetail + "\n") +
                    (agDetail.isEmpty() ? "" : agDetail);
            eventDetails.add(detail);
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Events for " + date);
        ListView<String> eventListView = new ListView<>();
        eventListView.getItems().addAll(eventDetails);
        dialog.getDialogPane().setContent(eventListView);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();

    }

    private void handleDayDoubleClick(LocalDate date, double x, double y) {
        int hour = (int) (y / 25); // Assuming each hour is represented by 25 pixels
        LocalTime time = LocalTime.of(hour, 0);

        Dialog<Evenement> dialog = new Dialog<>();
        dialog.setTitle("Create Event");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField eventName = new TextField();
        eventName.setPromptText("Event Name");
        TextArea eventDescription = new TextArea();
        eventDescription.setPromptText("Event Description");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(eventName, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(eventDescription, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Evenement newEvent = new Evenement();
                newEvent.setNom(eventName.getText());
                newEvent.setDescription(eventDescription.getText());
                newEvent.setDate(date.atTime(time).atZone(ZoneId.systemDefault()).toInstant());
                return newEvent;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(event -> evenements.add(event));
        updateCalendar();
    }

    private void changeMonth(int months) {
        currentYearMonth = currentYearMonth.plusMonths(months);
        updateCalendar();
    }

    public void applyCurrentTheme() {
        this.getStylesheets().clear();
        this.getStylesheets().add(getClass().getResource(Theme.themeVucalendrier).toExternalForm());
        this.setStyle("-fx-background-color: " + Theme.backgroudColorMain + ";");
    }
}
