/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.manuel.Main;


/**
 * FXML Controller class
 *
 * @author Manuel Fuentes
 */
public class SettingsViewController implements Initializable {
    @FXML
    private TextArea logHistoryTA;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logHistoryTA.setText(readLogHistory());
    }

    public String readLogHistory(){
        //constructs a string buffer with no characters
        StringBuilder sb = new StringBuilder();
        try {
            //creates a new file instance
            File file = new File("src/main/resources/org/manuel/logs/login_activity.txt");
            //reads the file
            FileReader fr = new FileReader(file.getAbsolutePath());
            //creates a buffering character input stream
            BufferedReader br = new BufferedReader(fr);
            String line;
            String comparator = "[" + Main.getUser().getUserName() + "]";
            while((line = br.readLine())!=null)
            {
                if(line.contains(comparator)){
                    //appends line to string buffer
                    sb.append(line).append(System.lineSeparator());
                }
            }
            //closes the stream and release the resources
            fr.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
}
