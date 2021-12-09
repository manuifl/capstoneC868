package org.manuel.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UnitedStateOfAmerica {

      private String stateCode;
      private String stateName;

      public UnitedStateOfAmerica(){

      }

    public UnitedStateOfAmerica(String stateCode, String stateName) {
        this.stateCode = stateCode;
        this.stateName = stateName;
    }

    public UnitedStateOfAmerica(String stateCode) {
        this.stateCode = stateCode;
    }
    //setters

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }


    // getters


    public String getStateCode() {
        return stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    @Override
    public String toString(){
        return getStateName() + " - " + getStateCode();
    }
}
