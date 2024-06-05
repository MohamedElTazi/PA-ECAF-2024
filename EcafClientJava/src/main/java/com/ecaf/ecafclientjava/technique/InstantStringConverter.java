package com.ecaf.ecafclientjava.technique;
import javafx.util.StringConverter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
public class InstantStringConverter extends StringConverter<Instant> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public String toString(Instant instant) {
        if (instant != null) {
            return formatter.format(instant);
        } else {
            return "";
        }
    }

    @Override
    public Instant fromString(String string) {
        if (string != null && !string.isEmpty()) {
            return LocalDateTime.parse(string, formatter).atZone(ZoneId.systemDefault()).toInstant();
        } else {
            return null;
        }
    }
}