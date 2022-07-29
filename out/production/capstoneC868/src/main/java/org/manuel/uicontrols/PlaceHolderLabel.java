package org.manuel.uicontrols;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Manuel Fuentes
 */
public class PlaceHolderLabel extends Label {

    /**
     * Creates an empty label
     */
    public PlaceHolderLabel() {
        setTextFill(Color.RED);
        setFont(Font.font(12.0));
    }

    /**
     * Creates Label with supplied text.
     *
     * @param text null text is treated as the empty string
     */
    public PlaceHolderLabel(String text) {
        super(text);
    }
}
