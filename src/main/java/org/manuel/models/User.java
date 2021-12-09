/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.models;

//import java.util.Calendar;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.Date;
/**
 *
 * @author manue
 */
public final class User extends ModelBase{
    
    private String userName;
    private String password;


    /**
     * Empty constructor
     */
    public User(){
        super();
    }
    /**
     * Constructor for populating user comboBox
     * @param userId int
     * @param userName String
     */
    public User(int userId, String userName) {
        super(userId);
        this.userName = userName;
    }

    //SETTERS
    /**
     * Set userName.
     * @param userName String
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    //GETTERS
    /**
     * Get userName
     * @return String
     */
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }






    @Override
    public String toString(){
        return userName;

    }
    
    
}
