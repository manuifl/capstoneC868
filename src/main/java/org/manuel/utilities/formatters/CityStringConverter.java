package org.manuel.utilities.formatters;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.manuel.models.City;

public class CityStringConverter extends StringConverter<City> {

    ComboBox<City> cb;

    public CityStringConverter(ComboBox<City> cb) {
        this.cb = cb;
    }

    @Override
    public String toString(City object) {
        if(object == null) {
            return "";
        }
        return object.toString();
    }
    @Override
    public City fromString(String string) {
        return cb
                .getItems()
                .stream()
                .filter(cities -> cities.toString().equals(string))
                .findFirst()
                .orElseGet(()-> cb.getSelectionModel().getSelectedItem());
    }
}
