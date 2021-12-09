package org.manuel.uicontrols;

import javafx.scene.control.ProgressIndicator;

/**
 * @author Manuel Fuentes
 */
public class SmallProgressIndicator extends ProgressIndicator {
    /**
     * Creates a new indeterminate ProgressIndicator.
     */
    public SmallProgressIndicator() {
        setPrefSize(25.0, 25.0);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setStyle("-fx-progress-color: red");
    }
}
