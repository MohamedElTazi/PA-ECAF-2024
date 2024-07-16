package com.ecaf.ecafclientjava.vue;

import com.ecaf.ecafclientjava.entites.AG;
import com.ecaf.ecafclientjava.entites.Evenement;
import com.ecaf.ecafclientjava.entites.Tache;
import com.ecaf.ecafclientjava.technique.Theme;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class VueCalendrier extends VBox {

    private final CalendarView calendarView;
    private final List<Evenement> evenements;
    private final List<Tache> taches;
    private final List<AG> ags;

    public VueCalendrier(List<Evenement> evenements, List<Tache> taches, List<AG> ags) {
        this.evenements = evenements;
        this.taches = taches;
        this.ags = ags;

        // Créer une instance de CalendarView
        calendarView = new CalendarView();

        // Créer et configurer le Calendar
        Calendar calendar = new Calendar("Planning");
        addEntriesToCalendar(calendar);

        // Créer et configurer le CalendarSource
        CalendarSource calendarSource = new CalendarSource("Planning");
        calendarSource.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(calendarSource);

        // Configurer l'affichage des détails des entrées
        calendarView.setEntryDetailsCallback(param -> {
            Entry<?> entry = param.getEntry();
            showAssignmentDialog(entry);
            return null;
        });

        // Ajouter le CalendarView à la VBox
        this.getChildren().add(calendarView);
    }

    private void addEntriesToCalendar(Calendar calendar) {
        evenements.forEach(event -> {
            Entry<String> entry = new Entry<>(event.getNom());
            LocalDate startDate = LocalDate.ofInstant(event.getDate(), ZoneId.systemDefault());
            LocalTime startTime = LocalTime.ofInstant(event.getDate(), ZoneId.systemDefault());
            entry.changeStartDate(startDate);
            entry.changeStartTime(startTime);
            entry.setLocation(event.getDescription());
            calendar.addEntry(entry);
        });

        taches.forEach(task -> {
            Entry<String> entry = new Entry<>(task.getDescription());
            LocalDate startDate = LocalDate.ofInstant(task.getDateDebut(), ZoneId.systemDefault());
            LocalDate endDate = LocalDate.ofInstant(task.getDateFin(), ZoneId.systemDefault());
            LocalTime startTime = LocalTime.ofInstant(task.getDateDebut(), ZoneId.systemDefault());
            LocalTime endTime = LocalTime.ofInstant(task.getDateFin(), ZoneId.systemDefault());
            entry.changeStartDate(startDate);
            entry.changeEndDate(endDate);
            entry.changeStartTime(startTime);
            entry.changeEndTime(endTime);
            calendar.addEntry(entry);
        });

        ags.forEach(ag -> {
            Entry<String> entry = new Entry<>(ag.getDescription());
            LocalDate startDate = LocalDate.ofInstant(ag.getDate(), ZoneId.systemDefault());
            LocalTime startTime = LocalTime.ofInstant(ag.getDate(), ZoneId.systemDefault());
            entry.changeStartDate(startDate);
            entry.changeStartTime(startTime);
            calendar.addEntry(entry);
        });
    }

    private void showAssignmentDialog(Entry<?> entry) {
        // Implémenter le code pour afficher les détails de l'entrée dans une boîte de dialogue
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Détails de l'entrée");
        dialog.setContentText(entry.getTitle() + "\n" + entry.getLocation());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }
}

