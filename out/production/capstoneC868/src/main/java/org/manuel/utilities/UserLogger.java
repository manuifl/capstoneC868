/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Manuel Fuentes
 */
public class UserLogger {

    public static File activityLog = new File("src/main/resources/org/manuel/logs/login_activity.txt");
//    public static String fileName = "activityLog";
//    public static File activityLog = new File(fileName);

    /**
     * Appends
     * @param user String
     * @param loggedIn Boolean
     */
    public static void trackingLogin (String user, boolean loggedIn) {
        LocalDateTime localTime = LocalDateTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                
                if(loggedIn){
                    try (FileWriter fw = new FileWriter(activityLog, true)) {
                       fw.write("Successful Login: user:[" + user + "] Access Time: " + localTime.format(dateFormat) + " at " + localTime.format(timeFormat) + System.lineSeparator());
                    }
                    
                }
                
                else{
                    try (FileWriter fw = new FileWriter(activityLog, true)) {
                        fw.write("Failed Login: user:[" + user + "] Access attempted at: " + localTime.format(dateFormat) + " at " + localTime.format(timeFormat) + System.lineSeparator());
                    }
                }
        
            } 
            
            catch (IOException ex) {
            System.out.println("Logger error: " + ex.getMessage());
        }

    }
    
        public static void trackingLogout (String user, boolean loggedOut) {
        LocalDateTime localTime = LocalDateTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                
                if(loggedOut){
                    try (FileWriter fw = new FileWriter(activityLog, true)) {
                        fw.write("Successful Logout: user:[" + user + "] Logout time: " + localTime.format(dateFormat) + " at " + localTime.format(timeFormat) + System.lineSeparator());
                    }
                    
                }
                
                else{
                    try (FileWriter fw = new FileWriter(activityLog, true)) {
                        fw.write("Failed Logout: user:[" + user + "] Logout attempted at: " + localTime.format(dateFormat) + " at " + localTime.format(timeFormat) + System.lineSeparator());
                    }
                }
        
            } 
            
            catch (IOException ex) {
            System.out.println("Logger error: " + ex.getMessage());
        }

    }

    public static File getActivityLog() {
        return activityLog;
    }

}
