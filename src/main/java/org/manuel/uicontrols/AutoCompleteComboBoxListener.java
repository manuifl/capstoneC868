/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.uicontrols;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Manuel Fuentes
 * @param <T>
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private ComboBox<T> comboBox;
    private StringBuilder sb;
    private ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public AutoCompleteComboBoxListener(final ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        sb = new StringBuilder();
        data = comboBox.getItems();
        Label placeHolder = new Label("No matches found");
        this.comboBox.setPlaceholder(placeHolder);

        this.comboBox.setEditable(true);

        this.comboBox.setOnKeyPressed(new EventHandler<>() {

            @Override
            public void handle(KeyEvent t) {
                comboBox.show();
            }
        });
        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
    }

    @Override
    public void handle(KeyEvent event) {

        if (event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.DOWN) {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if (event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if (event.getCode() == KeyCode.ENTER) {
            int selectedIndex = comboBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex > 0) {
                comboBox.getSelectionModel().select(selectedIndex);
            } else {
                comboBox.getSelectionModel().selectFirst();
                return;
            }
        }

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }

        ObservableList<T> list = FXCollections.observableArrayList();
        for (T datum : data) {
            if (datum.toString().toLowerCase().startsWith(
                    AutoCompleteComboBoxListener.this.comboBox
                            .getEditor().getText().toLowerCase())) {
                list.add(datum);
            }
        }
//        //Original
//        ObservableList list = FXCollections.observableArrayList();
//        for (int i = 0; i < data.size(); i++) {
//            if (data.get(i).toString().toLowerCase().startsWith(
//                    AutoCompleteComboBoxListener.this.comboBox
//                            .getEditor().getText().toLowerCase())) {
//                list.add(data.get(i));
//            }
//        }
        String t = comboBox.getEditor().getText();

        comboBox.setItems(list);
        comboBox.getEditor().setText(t);
        if (!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(t.length());
        if (!list.isEmpty()) {
            comboBox.show();
        }
//        else{
//            comboBox.show();
//        }
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

}
