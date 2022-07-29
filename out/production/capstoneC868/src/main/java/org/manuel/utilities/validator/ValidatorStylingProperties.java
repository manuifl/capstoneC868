/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.utilities.validator;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextBoundsType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.File;

/**
 *
 * @author Manuel Fuentes
 */
public class ValidatorStylingProperties {

    private static ImageView setErrorImage() {
        File errorIconFile = new File("src/main/resources/org/manuel/Images/errorIcon.png");
        ImageView iv = new ImageView();
        iv.setFitWidth(15.0);
        iv.setFitHeight(15.0);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        Image errorIconImage = new Image(errorIconFile.toURI().toString());
        iv.setImage(errorIconImage);
        return iv;
    }

    public static void setErrorTooltipProperties(Tooltip tp) {
        tp.setStyle("-fx-background-color: red;");
        tp.showDelayProperty().setValue(Duration.millis(10));
        tp.showDurationProperty().setValue(Duration.seconds(5));
    }

    public static void setErrorLabel(Label label) {

        Paint paint = Paint.valueOf("red");
        label.setTextFill(paint);
        label.setUnderline(true);
        label.setGraphic(setErrorImage());
    }

    public static void setNormalLabel(Label label) {
        Paint paint = Paint.valueOf("black");
        label.setTextFill(paint);
        label.setUnderline(false);
        label.setGraphic(null);
    }

    public static void emptyTextFieldLabel(Label label) {
        ValidatorStylingProperties.setErrorLabel(label);
        String s = "Please type a value for this text field";
        Tooltip tp;
        //Has previous tooltip
        if(label.getTooltip() != null){
            tp = label.getTooltip();
            tp.setText(s);
            ValidatorStylingProperties.setErrorTooltipProperties(tp);
            
        } else{
            tp = new Tooltip(s);
            ValidatorStylingProperties.setErrorTooltipProperties(tp);
            Tooltip.install(label, tp);
        }
    }
    public static void unselectedControlLabel(Label label) {
        ValidatorStylingProperties.setErrorLabel(label);
        String s = "Please select a value for this control";
        Tooltip tp;
        //Has previous tooltip
        if(label.getTooltip() != null){
            tp = label.getTooltip();
            tp.setText(s);
            ValidatorStylingProperties.setErrorTooltipProperties(tp);

        } else{
            tp = new Tooltip(s);
            ValidatorStylingProperties.setErrorTooltipProperties(tp);
            Tooltip.install(label, tp);
        }
    }
}
