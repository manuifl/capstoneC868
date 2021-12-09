/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.models;


/**
 *
 * @author Manuel Fuentes
 */
public class Location {

    private int locationId;
    private String locationName;

    /**
     * Empty Constructor
     */
    public Location(){

    }

    /**
     * Constructor fot database fetch operations
     * @param locationId Location id
     * @param locationName Location name
     */
    public Location(int locationId, String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }

    // Setters

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    // Getters
    public int getLocationId() {
        return locationId;
    }
    public String getLocationName() {
        return locationName;
    }

    @Override
    public String toString(){
        return getLocationName();
    
    }
    
}
