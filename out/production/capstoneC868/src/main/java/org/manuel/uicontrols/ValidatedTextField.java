/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.uicontrols;

import java.util.ArrayList;
import java.util.regex.Pattern;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * Custom TextField for input validation.
 * Registers errors for isEmpty, cardinality and regex errors
 * @author Manuel Fuentes
 */
public class ValidatedTextField extends TextField {

    /**
     *
     */
    public static final String DEFAULT_ERROR_STYLE_CLASS = "error";

    /**
     * ValidationTextField empty constructor, calls super constructor
     * Contains TextField
     */
    public ValidatedTextField() {
        super();

    }

    /*------------------------------------------------------------------------*/
    /*--------------------------------METHODS---------------------------------*/
    /*------------------------------------------------------------------------*/

    /**
     * Returns the errors in a ValidationTextField
     * @return ArrayList of Strings
     */


    public ArrayList<Errors> getAllErrors() {

        evalTextFieldRequired();
        evalTextFieldRegex();
        evalTextFieldCardinality();

        return errorList;
    }

    /**
     * Evaluates ValidationTextFields
     */
    public void eval() {
        //get list
        getAllErrors();
        //System.out.println(srcControl.get().getId());

    }

    /**
     * Sets hasEmptyErrors to true for empty ValidationTextFields and adds
     * "Empty Value" String to errorList
     */
    protected void evalTextFieldRequired() {
        //System.out.println(srcControl.get().getId());
        ValidatedTextField vtf = (ValidatedTextField) srcControl.get();
        if (errorList.contains(Errors.EMPTY)) {

            if (vtf.getText() == null || vtf.getText().equals("")) {
                hasEmptyErrors.set(true);
            } else {
                hasEmptyErrors.set(false);
                errorList.remove(Errors.EMPTY);
            }
        } else {
            if (vtf.getText() == null || vtf.getText().equals("")) {
                hasEmptyErrors.set(true);
                errorList.add(Errors.EMPTY);
            } else {
                hasEmptyErrors.set(false);
            }
        }
    }

    /**
     * Sets hasRegexErrors to true for ValidationTextFields that don't match
     * the regex property and adds "Invalid Format" String to errorList
     */
    protected void evalTextFieldRegex() {
        ValidatedTextField vtf = (ValidatedTextField) srcControl.get();
        if (errorList.contains(Errors.REGEX)) {

            if (Pattern.matches(regex.get(), (CharSequence) vtf.getText()) || vtf.getText().isEmpty() || vtf.getText().isBlank()) {
                hasRegexErrors.set(false);
                errorList.remove(Errors.REGEX);

            } else {
                //String text = vtf.getText();
                hasRegexErrors.set(true);
            }
        } else {
            if (Pattern.matches(regex.get(), (CharSequence) vtf.getText()) || vtf.getText().isEmpty() || vtf.getText().isBlank()) {
                hasRegexErrors.set(false);

            } else {
                hasRegexErrors.set(true);
                errorList.add(Errors.REGEX);
            }
        }
    }

    /**
     * Sets hasCardinalityErrors to true for ValidationTextFields that exceed
     * the max property or are inferior to the min property and adds
     * "Invalid Length" String to errorList
     */
    protected void evalTextFieldCardinality() {
        ValidatedTextField vtf = (ValidatedTextField) srcControl.get();
        int vtfLength = vtf.getText().length();
        String str = vtf.getText();
        if (min.get() == -1 || max.get() == -1) {
            //No min or max values were set Do nothing
        } else {
            if (errorList.contains(Errors.CARDINALITY)) {
                if (vtfLength >= min.get() && vtfLength <= max.get() || str.isBlank() || str.isEmpty()) {
                    hasCardinalityErrors.set(false);
                    errorList.remove(Errors.CARDINALITY);
                } else {
                    hasCardinalityErrors.set(true);
                }
            } else {
                if (vtfLength >= min.get() && vtfLength <= max.get() || str.isBlank() || str.isEmpty()) {
                    hasCardinalityErrors.set(false);
                } else {
                    hasCardinalityErrors.set(true);
                    errorList.add(Errors.CARDINALITY);
                }

            }

        }
    }


    /*------------------------------------------------------------------------*/
    /*-----------------------------PROPERTIES---------------------------------*/
    /*------------------------------------------------------------------------*/
    public enum Errors {
        EMPTY,
        REGEX,
        CARDINALITY
    }
    /**
     * ArrayList of errors
     */
    private ArrayList<Errors> errorList = new ArrayList<>();

    /**
     * ReadOnlyBooleanWrapper for empty ValidationTextField
     */
    protected ReadOnlyBooleanWrapper hasEmptyErrors = new ReadOnlyBooleanWrapper(false);

    /**
     * ReadOnlyBooleanWrapper for ValidationTextFields that don't match its
     * regex SimpleStringProperty
     */
    protected ReadOnlyBooleanWrapper hasRegexErrors = new ReadOnlyBooleanWrapper(false);

    /**
     * ReadOnlyBooleanWrapper for ValidationTextFields that exceed its max
     * SimpleIntegerProperty or are inferior to its min SimpleIntegerProperty.
     */
    protected ReadOnlyBooleanWrapper hasCardinalityErrors = new ReadOnlyBooleanWrapper(false);

    /**
     *
     */
    protected SimpleStringProperty errorStyleClass = new SimpleStringProperty(DEFAULT_ERROR_STYLE_CLASS);

    /**
     * SimpleObjectProperty of Node type that represents the source of a
     * ValidationTextField
     */
    protected SimpleObjectProperty<Node> srcControl = new SimpleObjectProperty<>();

    /**
     * SimpleStringProperty for regex matching
     */
    protected SimpleStringProperty regex = new SimpleStringProperty();

    /**
     * Integer referencing the minimum length for a ValidationTextField
     */
    protected SimpleIntegerProperty min = new SimpleIntegerProperty(-1);

    /**
     * Integer referencing the maximum length for a ValidationTextField
     */
    protected SimpleIntegerProperty max = new SimpleIntegerProperty(-1);


    /*------------------------------------------------------------------------*/
    /*--------------------------------SETTERS---------------------------------*/
    /*------------------------------------------------------------------------*/
    /**
     * Sets errorList for a given ValidationTextField
     * @param errorList ArrayList
     */
    public void setErrorList(ArrayList<Errors> errorList) {
        this.errorList = errorList;
    }

    /**
     *
     * @param styleClass String
     */
    public void setErrorStyleClass(String styleClass) {
        this.errorStyleClass.set(styleClass);
    }

    /**
     * Sets the srcControl SimpleObjectProperty of a ValidationTextField
     * @param srcControl Node
     */
    public void setSrcControl(Node srcControl) {
        this.srcControl.set(srcControl);
    }

    /**
     * Sets regex SimpleStringProperty
     * @param regex a Regex String
     */
    public void setRegex(String regex) {
        this.regex.set(regex);
    }

    /**
     * Set min SimpleIntegerProperty
     * @param min Integer
     */
    public void setMin(int min) {
        this.min.set(min);
    }

    /**
     * Set max SimpleIntegerProperty
     * @param max Integer
     */
    public void setMax(int max) {
        this.max.set(max);
    }

    public void setProperties(Node src, String regexS, int maxInt, int minInt) {
        this.srcControl.set(src);
        this.regex.set(regexS);
        this.max.set(maxInt);
        this.min.set(minInt);
    }
    public void setProperties(Node src, String regexS) {
        this.srcControl.set(src);
        this.regex.set(regexS);
    }

    /*------------------------------------------------------------------------*/
    /*--------------------------------GETTERS---------------------------------*/
    /*------------------------------------------------------------------------*/

    /**
     * Gets errorList for a given ValidationTextField
     * @return ArrayList
     */
    public ArrayList<Errors> getErrorList() {
        return errorList;
    }

    /**
     * Gets hasEmptyErrors ReadOnlyBooleanWrapper.
     * @return Boolean
     */
    public boolean getHasEmptyErrors() {
        return hasEmptyErrors.get();
    }

    /**
     * Accesses the ReadOnlyBooleanProperty of hasEmptyErrors ReadOnlyBooleanWrapper
     * @return ReadOnlyBooleanProperty
     */
    public ReadOnlyBooleanProperty hasEmptyErrorsProperty() {
        return hasEmptyErrors.getReadOnlyProperty();
    }

    /**
     * Gets hasRegexErrors ReadOnlyBooleanWrapper.
     * @return Boolean
     */
    public boolean getHasRegexErrors() {
        return hasRegexErrors.get();
    }

    /**
     * Accesses the ReadOnlyBooleanProperty of hasRegexErrors ReadOnlyBooleanWrapper.
     * @return ReadOnlyBooleanProperty
     */
    public ReadOnlyBooleanProperty hasRegexErrorsProperty() {
        return hasRegexErrors.getReadOnlyProperty();
    }


    /**
     * Gets hasCardinalityErrors ReadOnlyBooleanWrapper
     * @return Boolean
     */
    public boolean getHasCardinalityErrors() {
        return hasCardinalityErrors.get();
    }

    /**
     * Accesses the ReadOnlyBooleanProperty of hasCardinalityErrors ReadOnlyBooleanWrapper.
     * @return ReadOnlyBooleanProperty
     */
    public ReadOnlyBooleanProperty hasCardinalityErrorsProperty() {
        return hasCardinalityErrors.getReadOnlyProperty();
    }

    /**
     *
     * @return String
     */
    public String getErrorStyleClass() {
        return this.errorStyleClass.get();
    }

    /**
     *
     * @return SimpleStringProperty
     */
    public StringProperty errorStyleClassProperty() {
        return this.errorStyleClass;
    }

    /**
     * Gets the source of a ValidationTextField
     * @return Node
     */
    public Node getSrcControl() {
        return this.srcControl.get();
    }

    /**
     * Accesses the ObjectProperty Property of srcControl Node SimpleObjectProperty.
     * @return ObjectProperty

     */
    public ObjectProperty<Node> srcControlProperty() {
        return this.srcControl;
    }

    /**
     * Gets regex SimpleStringProperty
     * @return String
     */
    public String getRegex() {
        return this.regex.get();
    }

    /**
     * Accesses the StringProperty of regex SimpleStringProperty.
     * @return StringProperty
     */
    public StringProperty regexProperty() {
        return this.regex;
    }

    /**
     * Get min SimpleIntegerProperty
     * @return Integer
     */
    public int getMin() {
        return this.min.get();
    }

    /**
     * Accesses the IntegerProperty of min SimpleIntegerProperty.
     * @return IntegerProperty
     */
    public IntegerProperty minProperty() {
        return this.min;
    }

    /**
     * Get max SimpleIntegerProperty
     * @return Integer
     */
    public int getMax() {
        return this.max.get();
    }

    /**
     * Accesses the IntegerProperty of max SimpleIntegerProperty.
     * @return IntegerProperty
     */
    public IntegerProperty maxProperty() {
        return this.max;
    }

}
