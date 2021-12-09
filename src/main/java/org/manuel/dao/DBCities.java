package org.manuel.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.manuel.models.City;
import org.manuel.models.UnitedStateOfAmerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBCities {

    private static final Connection conn = DBConnection.getConnection();

    // private constructor for non-instantiable utility class
    private DBCities() {
        throw new AssertionError();
    }


    public static ObservableList<City> getAllCitiesForState(String stateCode) {
        ObservableList<City> cityList = FXCollections.observableArrayList();
        try{
            String sql = """
                    SELECT City_ID, City_Name, c.State_Code, s.State_Name FROM cities c
                    INNER JOIN states s ON c.State_Code = s.State_Code
                    WHERE c.State_Code = ? ORDER BY City_Name;""";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            //PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, stateCode);
            //execute query and get results
            ResultSet rs = ps.executeQuery();
            //go through result set, one row at a time
            while(rs.next()){
                //get result sets
                UnitedStateOfAmerica state = new UnitedStateOfAmerica(
                        stateCode,
                        rs.getString("State_Name")
                );

                City city = new City(
                        rs.getInt("City_ID"),
                        rs.getString("City_Name"),
                        state
                );
                cityList.add(city);
            }
        }
        catch(SQLException ex){
            ex.printStackTrace(System.out);
        }

        return cityList;
    }
}
