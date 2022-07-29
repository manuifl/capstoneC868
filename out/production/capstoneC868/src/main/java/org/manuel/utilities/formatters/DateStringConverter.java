package org.manuel.utilities.formatters;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateStringConverter extends StringConverter<LocalDate> {
    private DateTimeFormatter formatter;
    private String string;
    private LocalDate localDate;

    public DateStringConverter() {
        super();
    }

    public DateStringConverter(DateTimeFormatter formatter, String string, LocalDate localDate) {
        super();
        this.formatter = formatter;
        this.string = string;
        this.localDate = localDate;
    }

    @Override
    public String toString(LocalDate localDate) {
        this.localDate = localDate;
        if (localDate != null) {
            return this.formatter.format(localDate);
        } else {
            return "";
        }
    }

    @Override
    public LocalDate fromString(String string) {
        this.string = string;
        if (string != null && !string.isEmpty()) {
            return LocalDate.parse(string, this.formatter);
        } else {
            return null;
        }
    }
    public void setProperties(DateTimeFormatter formatter, String string, LocalDate localDate){
        this.formatter = formatter;
        this.string = string;
        this.localDate = localDate;
    }


}
