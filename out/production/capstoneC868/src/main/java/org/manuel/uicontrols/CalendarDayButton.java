package org.manuel.uicontrols;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.manuel.models.Appointment;

import java.time.LocalDate;

public class CalendarDayButton extends RadioButton {

    private LocalDate date;

    private ObservableList<Appointment> appointmentList;

    public CalendarDayButton() {
        super();
        getStyleClass().remove("radio-button");
        getStyleClass().add("calendar-days-button");
        setAlignment(Pos.CENTER);
        setPrefSize(68.0, 68.0);
        setMaxSize(68.0, 68.0);
        setMinSize(50.0, 50.0);
    }

    public CalendarDayButton(LocalDate date, ObservableList<Appointment> appointmentList) {
        super();
        this.date = date;
        this.appointmentList = appointmentList;
        getStyleClass().remove("radio-button");
        getStyleClass().add("button");
        getStyleClass().add("calendar-days-button");
        setAlignment(Pos.CENTER);
        setPrefSize(68.0, 68.0);
        setMaxSize(68.0, 68.0);
        setMinSize(50.0, 50.0);
        setText(Integer.toString(date.getDayOfMonth()));
        if (!this.appointmentList.isEmpty()) {
            this.setGraphic(IconStackPane.appointmentQtyGraphic(String.valueOf(this.appointmentList.size())));
        } else {
            this.setGraphic(IconStackPane.appointmentEmptyGraphic());
        }
        this.setContentDisplay(ContentDisplay.BOTTOM);
    }


    // Setters
    public void setAppointmentsList(ObservableList<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    // Getters

    public ObservableList<Appointment> getAppointmentsList() {
        return appointmentList;
    }

    public LocalDate getDate() {
        return date;
    }

    private static class IconStackPane {
        static Node appointmentQtyGraphic(String number) {
            StackPane sp = new StackPane();
            Label lab = new Label(number);
            lab.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
            lab.setTextFill(Color.WHITE);
            Circle circle = new Circle(10);
            circle.setStroke(Color.rgb(181, 179, 179, .9));
            circle.setFill(Color.rgb(181, 179, 179, .9));
            circle.setSmooth(true);
            sp.getChildren().addAll(circle, lab);
            return sp;
        }
        static Node appointmentEmptyGraphic() {
            StackPane sp = new StackPane();
            Label label = new Label("0");
            label.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
            label.setTextFill(Color.TRANSPARENT);
            Circle circle = new Circle(10);
            circle.setStroke(Color.rgb(224, 224, 224, .9));
            circle.setFill(Color.TRANSPARENT);
            circle.setSmooth(true);
            sp.getChildren().addAll(circle, label);
            return sp;
        }
    }
}
