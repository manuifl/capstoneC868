package org.manuel.models;


public class City {

private int cityId;
    private String cityName;
    private UnitedStateOfAmerica unitedStateOfAmerica;

    /**
     * Empty constructor
     */
    public City(){
    }

    /**
     * Constructor fot database fetch operations
     * @param cityId City id
     * @param cityName City name
     * @param unitedStateOfAmerica City state
     */
    public City(int cityId, String cityName, UnitedStateOfAmerica unitedStateOfAmerica) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.unitedStateOfAmerica = unitedStateOfAmerica;
    }


    // Setters
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setState(UnitedStateOfAmerica unitedStateOfAmerica) {
        this.unitedStateOfAmerica = unitedStateOfAmerica;
    }


    // Getters
    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public UnitedStateOfAmerica getState() {
        return unitedStateOfAmerica;
    }


    @Override
    public String toString(){
        return getCityName();

    }
}
