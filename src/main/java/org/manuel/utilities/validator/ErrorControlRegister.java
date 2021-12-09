/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.utilities.validator;

import java.util.LinkedHashMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
//import validatedTextField.ValidatedTextField;
import org.manuel.uicontrols.ValidatedTextField;

/**
 *
 * @author Manuel Fuentes
 */
public class ErrorControlRegister {
    
    //public static <T extends Pane> Map<Node, Object> getFormValues(T parent){
   public static Map<String, Object> getFormValues(Pane formControlsPane) {
        return formValues(formControlsPane, new LinkedHashMap<>());
    }

   //private static <T extends Pane> Map<Node, Object> getFormValues(T parent, Map<Node, Object> map) {
    private static Map<String, Object> formValues(Pane parent, Map<String, Object> map) {
        
        //boolean isLocalTime = ComboBox.class.isAssignableFrom(LocalTime);
        try {
            for (Node node : parent.getChildren()) {
                // Nodes - You can add more.
                //if (node instanceof TextField) {
                
                if (node instanceof ValidatedTextField vtf) {
                    if(vtf.getHasEmptyErrors() || vtf.getHasRegexErrors() || vtf.getHasCardinalityErrors()){
                        map.put(node.getId(), vtf.getErrorList());
                    }
                }
                if (node instanceof DatePicker) {
                    DatePicker dp = ((DatePicker) node);
                    if(dp.getValue() == null){
                        map.put(node.getId(), "EMPTY");
                        //map.put(node, ((TextField) node).getText());
                    }
                }
                if (node instanceof ComboBox) {
                    ComboBox cb = ((ComboBox) node);

                    if(cb.getValue() == null){
                        map.put(node.getId(), "EMPTY");
                    }
                }
                // Recursive.
                if (node instanceof Pane) {
                    formValues((Pane) node, map);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return map;
    }

}
