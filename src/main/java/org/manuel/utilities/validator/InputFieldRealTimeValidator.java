/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.utilities.validator;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import org.manuel.models.*;

import java.time.LocalTime;

import javafx.beans.value.ChangeListener;

//import javafx.scene.image.ImageView;
//import javafx.scene.paint.Paint;
//import javafx.scene.text.Text;
//import javafx.stage.Window;
//import javafx.util.Duration;
/**
 *
 * @author Manuel Fuentes
 */
public class InputFieldRealTimeValidator {

    public InputFieldRealTimeValidator(){

    }


        /**
     * Validates titles for length and symbols
     *
     * @param tf
     * @param label
     */
    public static void validateNameTextField(TextField tf, Label label) {

        tf.textProperty().addListener((ObservableValue<? extends String> ov, String oldPropertyValue, String newPropertyValue) -> {
            Tooltip tooltip1 = new Tooltip();
            ValidatorStylingProperties.setErrorTooltipProperties(tooltip1);

            //if (!newPropertyValue.matches("^.{1,50}$")) {
            if (newPropertyValue.length() > 50) {
                Tooltip.uninstall(label, tooltip1);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip1.setText("Text field value exceeds character limit");
                Tooltip.install(label, tooltip1);
            } else if (!StringValidator.customerNameRegex(tf.getText())) {
//            } else if (!newPropertyValue.matches("^[a-zA-Z0-9\\s-]*$")) {
                Tooltip.uninstall(label, tooltip1);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip1.setText("Field has invalid characters " + System.lineSeparator() 
                                + "Valid characters are: " + System.lineSeparator()
                                + "   A-Z a-z 0-9 - " + System.lineSeparator());
                Tooltip.install(label, tooltip1);

            } else if (newPropertyValue.isEmpty()) {
                Tooltip.uninstall(label, tooltip1);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip1.setText("Please type a value for this text field");
                Tooltip.install(label, tooltip1);

            } else {
                //System.out.println("Valid Input Value");
                ValidatorStylingProperties.setNormalLabel(label);
                Tooltip.uninstall(label, tooltip1);
            }
        });
    }
    
    public static void validateAddressTextField(TextField tf, Label label) {

        tf.textProperty().addListener((ObservableValue<? extends String> ov, String oldPropertyValue, String newPropertyValue) -> {
            Tooltip tooltip2 = new Tooltip();
            ValidatorStylingProperties.setErrorTooltipProperties(tooltip2);

            if (newPropertyValue.length() > 50) {
                Tooltip.uninstall(label, tooltip2);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip2.setText("Field value exceeds character limit");
                Tooltip.install(label, tooltip2);
            } else if (!StringValidator.customerAddressRegex(tf.getText())) {
                Tooltip.uninstall(label, tooltip2);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip2.setText("Field has invalid characters " + System.lineSeparator() 
                                + "Valid characters are: " + System.lineSeparator()
                                + "   A-Z a-z 0-9 . , - #" + System.lineSeparator());
                Tooltip.install(label, tooltip2);
            } else if (newPropertyValue.isEmpty()) {
                Tooltip.uninstall(label, tooltip2);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip2.setText("Please type a value for this text field");
                Tooltip.install(label, tooltip2);

            } else {
                //System.out.println("Valid Input Value");
                ValidatorStylingProperties.setNormalLabel(label);
                Tooltip.uninstall(label, tooltip2);
            }
        });
    }
    
    public static void validatePostalCodeTextField(TextField tf, Label label) {

        tf.textProperty().addListener((ObservableValue<? extends String> ov, String oldPropertyValue, String newPropertyValue) -> {
            Tooltip tooltip3 = new Tooltip();
            ValidatorStylingProperties.setErrorTooltipProperties(tooltip3);
            
            if (newPropertyValue.length() > 15) {
                Tooltip.uninstall(label, tooltip3);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip3.setText("Text field value exceeds character limit");
                Tooltip.install(label, tooltip3);

            } else if (!StringValidator.customerPostalCodeRegex(tf.getText())) {
                Tooltip.uninstall(label, tooltip3);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip3.setText("Field has invalid characters " + System.lineSeparator() 
                                + "Valid characters:" + System.lineSeparator()                          
                                + "   0-9 -" + System.lineSeparator());
                Tooltip.install(label, tooltip3);

            } else if (newPropertyValue.isEmpty()) {
                Tooltip.uninstall(label, tooltip3);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip3.setText("Please type a value for this text field");
                Tooltip.install(label, tooltip3);

            } else {
                //System.out.println("Valid Input Value");
                ValidatorStylingProperties.setNormalLabel(label);
                Tooltip.uninstall(label, tooltip3);
            }
        });
    }
    
    public static void validatePhoneTextField(TextField tf, Label label) {

        tf.textProperty().addListener((ObservableValue<? extends String> ov, String oldPropertyValue, String newPropertyValue) -> {
            Tooltip tooltip4 = new Tooltip();
            ValidatorStylingProperties.setErrorTooltipProperties(tooltip4);

            if (newPropertyValue.length() > 50) {
                Tooltip.uninstall(label, tooltip4);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip4.setText("Text field value exceeds character limit");
                Tooltip.install(label, tooltip4);

            } else if (!StringValidator.customerPhoneRegex(tf.getText())) {
                Tooltip.uninstall(label, tooltip4);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip4.setText("Field has invalid characters " + System.lineSeparator()
                        + "Valid characters:" + System.lineSeparator()
                        + "   0-9 . - ( ) +" + System.lineSeparator());
                Tooltip.install(label, tooltip4);
            } else if (newPropertyValue.isEmpty()) {
                Tooltip.uninstall(label, tooltip4);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip4.setText("Please type a value for this text field");
                Tooltip.install(label, tooltip4);

            } else {
                //System.out.println("Valid Input Value");
                ValidatorStylingProperties.setNormalLabel(label);
                Tooltip.uninstall(label, tooltip4);
            }
        });
//        tf.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
//            Tooltip tooltip3 = new Tooltip();
//            ValidatorStylingProperties.setErrorTooltipProperties(tooltip3);
//
//            //If focus changes
//            if (newPropertyValue) {
//                //System.out.println("Textfield on focus");
//
//            } else {
//                //Listener activated
//                //System.out.println("Textfield out focus");
//                if (tf.getText().isBlank() == false) {
//                    //Too long
//                    if (StringValidator.onlyNumericHyphensParethesesSpacesAndDots(tf.getText()) == false) {
//                        //System.out.println("Invalid Phone Format");
//                        ValidatorStylingProperties.setErrorLabel(label);
//                        tooltip3.setText("Invalid Phone Format" + System.lineSeparator() + "Examples:" + System.lineSeparator()
//                                + "   +111 (202) 555-0125" + System.lineSeparator()
//                                + "   (202) 555-0125" + System.lineSeparator()
//                                + "   2025550125" + System.lineSeparator());
//                        Tooltip.install(label, tooltip3);
//                    } else {
//                        //System.out.println("Valid Input Value");
//                        ValidatorStylingProperties.setErrorLabel(label);
//                        Tooltip.uninstall(label, tooltip3);
//                    }
//                } //Empty
//                else {
//                    //System.out.println("Textfield is empty");
//                    ValidatorStylingProperties.setErrorLabel(label);
//                    tooltip3.setText("Please type a value for this text field");
//                    //invalidLength.show(label, labelX, labelY);
//                    Tooltip.install(label, tooltip3);
//                }
//            }
//        });
//        return false;
    }
    public static void validateEmailTextField(TextField tf, Label label) {

        tf.textProperty().addListener((ObservableValue<? extends String> ov, String oldPropertyValue, String newPropertyValue) -> {
            Tooltip tooltip15 = new Tooltip();
            ValidatorStylingProperties.setErrorTooltipProperties(tooltip15);

            if (newPropertyValue.length() > 50) {
                Tooltip.uninstall(label, tooltip15);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip15.setText("Text field value exceeds character limit");
                Tooltip.install(label, tooltip15);

            } else if (!StringValidator.customerEmailRegex(tf.getText())) {
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip15.setText("Invalid Format " + System.lineSeparator()
                        + "Valid Example:" + System.lineSeparator()
                        + "   example_123@domain.suffix" + System.lineSeparator());
                Tooltip.install(label, tooltip15);
            } else if (newPropertyValue.isEmpty()) {
                Tooltip.uninstall(label, tooltip15);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip15.setText("Please type a value for this text field");
                Tooltip.install(label, tooltip15);

            } else {
                ValidatorStylingProperties.setNormalLabel(label);
                Tooltip.uninstall(label, tooltip15);
            }
        });
    }

    public static void validateStateCB(ComboBox<UnitedStateOfAmerica> cb, Label label) {

        cb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                Tooltip tooltip5 = new Tooltip();
                ValidatorStylingProperties.setErrorTooltipProperties(tooltip5);

                //If focus changes
                if (newPropertyValue) {

                } else {
                    //Listener activated
                    if (cb.getValue() == null) {
                        ValidatorStylingProperties.setErrorLabel(label);
                        tooltip5.setText("A country needs to be selected");
                        Tooltip.install(label, tooltip5);
                    } //Empty
                    else {
                        ValidatorStylingProperties.setNormalLabel(label);
                        Tooltip.uninstall(label, tooltip5);
                    }
                }
            }
        });

    }

    public static void validateCityCB(ComboBox<City> cb, Label label) {

        cb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                Tooltip tooltip6 = new Tooltip();
                ValidatorStylingProperties.setErrorTooltipProperties(tooltip6);

                //If focus changes
                if (newPropertyValue) {
                    //System.out.println("Textfield on focus");

                } else {
                    //Listener activated
                    if (cb.getValue() == null) {
                        ValidatorStylingProperties.setErrorLabel(label);
                        tooltip6.setText("A city needs to be selected");
                        Tooltip.install(label, tooltip6);
                    } //Empty
                    else {
                        ValidatorStylingProperties.setNormalLabel(label);
                        Tooltip.uninstall(label, tooltip6);
                    }
                }
            }
        });

    }
    
     /**
     * Validates titles for length and symbols
     * @param tf
     * @param label
     */
    public static void validateTitleTextField(TextField tf, Label label) {

        tf.textProperty().addListener((ObservableValue<? extends String> ov, String oldPropertyValue, String newPropertyValue) -> {
            Tooltip tooltip7 = new Tooltip();
            ValidatorStylingProperties.setErrorTooltipProperties(tooltip7);

            //if (!newPropertyValue.matches("^.{1,50}$")) {
            if (newPropertyValue.length() > 50) {
                Tooltip.uninstall(label, tooltip7);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip7.setText("Text field value exceeds character limit");
                Tooltip.install(label, tooltip7);

            } else if (!StringValidator.appointmentTitleRegex(tf.getText())) {
                Tooltip.uninstall(label, tooltip7);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip7.setText("Field has invalid characters " + System.lineSeparator() 
                                + "Valid characters:" + System.lineSeparator()                              
                                + "   A-Z a-z 0-9 -" + System.lineSeparator());
                Tooltip.install(label, tooltip7);

            } else if (newPropertyValue.isEmpty()) {
                Tooltip.uninstall(label, tooltip7);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip7.setText("Please type a value for this text field");
                Tooltip.install(label, tooltip7);

            } else {
                ValidatorStylingProperties.setNormalLabel(label);
                Tooltip.uninstall(label, tooltip7);
            }
        });
    }
    
    /**
     * Validates titles for length and symbols
     * @param tf
     * @param label
     */
    public static void validateDescTextField(TextField tf, Label label) {

        tf.textProperty().addListener((ObservableValue<? extends String> ov, String oldPropertyValue, String newPropertyValue) -> {
            Tooltip tooltip8 = new Tooltip();
            ValidatorStylingProperties.setErrorTooltipProperties(tooltip8);

            if (newPropertyValue.length() > 50) {
                Tooltip.uninstall(label, tooltip8);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip8.setText("Text field value exceeds character limit");
                Tooltip.install(label, tooltip8);

            } else if (!StringValidator.appointmentDescriptionRegex(tf.getText())) {
                Tooltip.uninstall(label, tooltip8);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip8.setText("Field has invalid characters " + System.lineSeparator() 
                                + "Valid characters:" + System.lineSeparator()
                                + "   alphanumeric" + System.lineSeparator()
                                + "   whitespaces" + System.lineSeparator()
                                + "   A-Z a-z 0-9 . , - ' ? !" + System.lineSeparator());
                Tooltip.install(label, tooltip8);

            } else if (newPropertyValue.isEmpty()) {
                Tooltip.uninstall(label, tooltip8);
                ValidatorStylingProperties.setErrorLabel(label);
                tooltip8.setText("Please type a value for this text field");
                Tooltip.install(label, tooltip8);

            } else {
                //System.out.println("Valid Input Value");
                ValidatorStylingProperties.setNormalLabel(label);
                Tooltip.uninstall(label, tooltip8);
            }
        });
    }

    public static void validateDatePicker(DatePicker dp, Label label) {

        dp.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                Tooltip tooltip9 = new Tooltip();
                ValidatorStylingProperties.setErrorTooltipProperties(tooltip9);

                //If focus changes
                if (newPropertyValue) {

                } else {
                    //Listener activated
                    //System.out.println("CB out focus");
                    if (dp.getValue() == null) {
                        ValidatorStylingProperties.setErrorLabel(label);
                        tooltip9.setText("A date needs to be selected");
                        Tooltip.install(label, tooltip9);
                    } //Empty
                    else {
                        ValidatorStylingProperties.setNormalLabel(label);
                        Tooltip.uninstall(label, tooltip9);
                    }
                }
            }
        });

    }

    public static void validateTypeCB(ComboBox<String> cb, Label label) {

        cb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                Tooltip tooltip10 = new Tooltip();
                ValidatorStylingProperties.setErrorTooltipProperties(tooltip10);

                //If focus changes
                if (newPropertyValue) {

                } else {
                    //Listener activated
                    //System.out.println("CB out focus");
                    if (cb.getValue() == null) {
                        ValidatorStylingProperties.setErrorLabel(label);
                        tooltip10.setText("An appointment type needs to be selected");
                        Tooltip.install(label, tooltip10);
                    } //Empty
                    else {
                        ValidatorStylingProperties.setNormalLabel(label);
                        Tooltip.uninstall(label, tooltip10);
                    }
                }
            }
        });

    }

    public static void validateLocationCB(ComboBox<Location> cb, Label label) {

        cb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                Tooltip tooltip11 = new Tooltip();
                ValidatorStylingProperties.setErrorTooltipProperties(tooltip11);

                //If focus changes
                if (newPropertyValue) {

                } else {
                    //Listener activated
                    if (cb.getValue() == null) {
                        ValidatorStylingProperties.setErrorLabel(label);
                        tooltip11.setText("An appointment location needs to be selected");
                        Tooltip.install(label, tooltip11);
                    } //Empty
                    else {
                        ValidatorStylingProperties.setNormalLabel(label);
                        Tooltip.uninstall(label, tooltip11);
                    }
                }
            }
        });

    }
    public static void validateStartTimeCB(ComboBox<LocalTime> cb, Label label) {

        cb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                Tooltip tooltip12 = new Tooltip();
                ValidatorStylingProperties.setErrorTooltipProperties(tooltip12);

                //If focus changes
                if (newPropertyValue) {
                    //System.out.println("Textfield on focus");

                } else {
                    //Listener activated
                    if (cb.getValue() == null) {
                        ValidatorStylingProperties.setErrorLabel(label);
                        tooltip12.setText("An appointment start time needs to be selected after selecting a date");
                        Tooltip.install(label, tooltip12);
                    } //Empty
                    else {
                        ValidatorStylingProperties.setNormalLabel(label);
                        Tooltip.uninstall(label, tooltip12);
                    }
                }
            }
        });

    }

    public static void validateEndTimeCB(ComboBox<LocalTime> cb, Label label) {

        cb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                Tooltip tooltip13 = new Tooltip();
                ValidatorStylingProperties.setErrorTooltipProperties(tooltip13);

                //If focus changes
                if (newPropertyValue) {
                    //System.out.println("Textfield on focus");

                } else {
                    //Listener activated
                    if (cb.getValue() == null) {
                        ValidatorStylingProperties.setErrorLabel(label);
                        tooltip13.setText("An appointment end time needs to be selected after selecting a date");
                        Tooltip.install(label, tooltip13);
                    } //Empty
                    else {
                        ValidatorStylingProperties.setNormalLabel(label);
                        Tooltip.uninstall(label, tooltip13);
                    }
                }
            }
        });

    }

    public static void validateCustomersCB(ComboBox<Customer> cb, Label label) {

        cb.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                Tooltip tooltip14 = new Tooltip();
                ValidatorStylingProperties.setErrorTooltipProperties(tooltip14);

                //If focus changes
                if (newPropertyValue) {

                } else {
                    //Listener activated
                    if (cb.getValue() == null) {
                        ValidatorStylingProperties.setErrorLabel(label);
                        tooltip14.setText("A customer must be associated with each appointment");
                        Tooltip.install(label, tooltip14);
                    } //Empty
                    else {
                        ValidatorStylingProperties.setNormalLabel(label);
                        Tooltip.uninstall(label, tooltip14);
                    }
                }
            }
        });

    }

    public static void limitToOnlyNumbers(TextField tf) {
        tf.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));

    }
}
