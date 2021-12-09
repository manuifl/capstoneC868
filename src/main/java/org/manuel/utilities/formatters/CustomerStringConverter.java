package org.manuel.utilities.formatters;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.manuel.models.Customer;

public class CustomerStringConverter extends StringConverter<Customer> {
    ComboBox<Customer> cb;

    public CustomerStringConverter(ComboBox<Customer> cb) {
        this.cb = cb;
    }

    /**
     * Converts the object provided into its string form.
     * Format of the returned string is defined by the specific converter.
     *
     * @param object the object of type {@code T} to convert
     * @return a string representation of the object passed in.
     */
    @Override
    public String toString(Customer object) {
        if(object == null) {
            return "";
        }
        return object.toString();
    }

    /**
     * Converts the string provided into an object defined by the specific converter.
     * Format of the string and type of the resulting object is defined by the specific converter.
     *
     * @param string the {@code String} to convert
     * @return an object representation of the string passed in.
     */
    @Override
    public Customer fromString(String string) {
        return cb
                .getItems()
                .stream()
                .filter(customer -> customer.toString().equals(string))
                .findFirst()
                .orElseGet(()-> cb.getSelectionModel().getSelectedItem());
    }
}
