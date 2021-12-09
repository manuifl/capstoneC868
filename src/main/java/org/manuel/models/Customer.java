/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.models;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


import java.sql.Date;

/**
 *
 * @author Manuel Fuentes
 */
public final class Customer extends ModelBase{

    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private String emailAddress;
    private final BooleanProperty activeMember;
    private City city;

    public Customer(){
        super();
        this.activeMember = new SimpleBooleanProperty();
    }

    //Constructor for database add operations
    public Customer(String name, String address, String postalCode, String phoneNumber, String emailAddress, City city) {
        super();
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.city = city;
        this.activeMember = new SimpleBooleanProperty();
    }

    //Constructor for database modify operations
    public Customer(int id, String name, String address, String postalCode, String phoneNumber, String emailAddress, City city) {
        super(id);
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.city = city;
        this.activeMember = new SimpleBooleanProperty();
    }

    public Customer(int id, String name) {
        super(id);
        this.name = name;
        this.activeMember = new SimpleBooleanProperty();
    }

    //    public Customer(int id, Date createDate, String createdBy, Date lastUpdate, String lastUpdatedBy) {
//        super(id, createDate, createdBy, lastUpdate, lastUpdatedBy);
//        this.activeMember = new SimpleBooleanProperty();
//    }
//
//    public Customer(int id, String name, String address, String postalCode, String phoneNumber, String emailAddress, Date createDate, String createdBy, Date lastUpdate, String lastUpdatedBy, City city) {
//        super(id, createDate, createdBy, lastUpdate, lastUpdatedBy);
//        this.name = name;
//        this.address = address;
//        this.postalCode = postalCode;
//        this.phoneNumber = phoneNumber;
//        this.emailAddress = emailAddress;
//        this.activeMember = new SimpleBooleanProperty();
//        this.city = city;
//    }
    // Setters


    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setActiveMember(boolean activeMember) {
        this.activeMember.set(activeMember);
    }

    public void setCity(City city) {
        this.city = city;
    }


    // Getters


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isActiveMember() {
        return activeMember.get();
    }

    public BooleanProperty activeMemberProperty() {
        return activeMember;
    }

    public City getCity() {
        return city;
    }

    @Override
    public String toString(){
        return getName();
    }
    
    
}
