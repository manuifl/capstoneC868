/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.dao;

import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.manuel.models.Location;

/**
 *
 * @author Manuel Fuentes
 */
public class DBLocations {
    private static final Connection conn = DBConnection.getConnection();

    // private constructor for non-instantiable utility class
    private DBLocations() {
        throw new AssertionError();
    }

    /**
     * ObservableList containing a ResultSet of all records in the locations
     * table. Used to populate the country combo box.
     * @return ObservableList
     */
    public static ObservableList<Location> getAllLocations() throws SQLException {
        ObservableList<Location> locationList = FXCollections.observableArrayList();
        try {
            //MySQL Statement
            String sql = "SELECT Location, Location_ID FROM locations;";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            //execute query and get results
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //get result sets
                Location location = new Location(
                        rs.getInt("Location_ID"),
                        rs.getString("Location")
                );
                locationList.add(location);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return locationList;
    }

    /**
     * ObservableList containing a ResultSet of all locations that are available,
     * given a selected start and end time .Used to populate the appointment add/edit location combo box.
     * @return ObservableList
     */
    public static ObservableList<Location> getOnlyAvailableLocations(Timestamp newStart, Timestamp newEnd) {
        ObservableList<Location> locationList = FXCollections.observableArrayList();
        try {
            //MySQL Statement
            String sql = """
                    SELECT Location, Location_ID
                    FROM locations
                    WHERE Location_ID NOT IN(
                    SELECT Location_ID
                    FROM appointments
                    WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End))
                    ORDER BY Location_ID;""";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setTimestamp(1, newStart);
            ps.setTimestamp(2, newEnd);
            ps.setTimestamp(3, newStart);
            ps.setTimestamp(4, newEnd);
            //execute query and get results
            ResultSet rs = ps.executeQuery();
            //go through result set, one row at a time
            while (rs.next()) {
                //get result sets
                Location location = new Location(
                        rs.getInt("Location_ID"),
                        rs.getString("Location")
                );
                locationList.add(location);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return locationList;
    }

}
