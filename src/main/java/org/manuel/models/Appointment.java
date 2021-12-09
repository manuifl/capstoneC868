package org.manuel.models;

import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Data class for appointments
 */

public final class Appointment extends ModelBase{
    private String title;
    private String description;
    private String type;
    private Location location;
    private LocalDate startDate;
    private Timestamp startTime;
    private Timestamp endTime;
    private Customer customer;
    private User user;


    /**
     * Empty constructor
     */
    public Appointment() {
        super();
    }

    /**
     * Constructor fot database fetch operations
     * @param id Appointment id
     * @param title Appointment title
     * @param description Appointment description
     * @param type Appointment type
     * @param location Appointment location
     * @param startDate Appointment date
     * @param startTime Appointment start time
     * @param endTime Appointment end time
     * @param customer Customer hosting the appointment
     */
    public Appointment(int id, String title, String description, String type, Location location, LocalDate startDate, Timestamp startTime, Timestamp endTime, Customer customer) {
        super(id);
        this.title = title;
        this.description = description;
        this.type = type;
        this.location = location;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customer = customer;
    }

    /**
     * Constructor for add operations
     * @param title Appointment title
     * @param description Appointment description
     * @param type Appointment type
     * @param location Appointment location
     * @param startTime Appointment start time
     * @param endTime Appointment end time
     * @param customer Customer hosting the appointment
     * @param user User that created the appointment
     */
    public Appointment(String title, String description, String type, Location location, Timestamp startTime, Timestamp endTime, Customer customer, User user) {
        super();
        this.title = title;
        this.description = description;
        this.type = type;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customer = customer;
        this.user = user;
    }

    /**
     * Constructor for update operations
     * @param id Appointment id
     * @param title Appointment title
     * @param description Appointment description
     * @param type Appointment type
     * @param location Appointment location
     * @param startTime Appointment start time
     * @param endTime Appointment end time
     * @param customer Customer hosting the appointment
     * @param user User that created the appointment
     */
    public Appointment(int id, String title, String description, String type, Location location, Timestamp startTime, Timestamp endTime, Customer customer, User user) {
        super(id);
        this.title = title;
        this.description = description;
        this.type = type;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customer = customer;
        this.user = user;
    }

    //Setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setUser(User user) {
        this.user = user;
    }


    //Getters

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public User getUser() {
        return user;
    }


    @Override
    public String toString(){
        return getTitle();
    }
}
