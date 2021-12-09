package org.manuel.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.manuel.models.UnitedStateOfAmerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBStates {

    private static final Connection conn = DBConnection.getConnection();


    public static ObservableList<UnitedStateOfAmerica> getAllStates() {
        ObservableList<UnitedStateOfAmerica> unitedStateOfAmericaList = FXCollections.observableArrayList();

        try{
            //MySQL Statement
            String sql = "SELECT State_Name, State_Code FROM states;";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            //execute query and get results
            ResultSet rs = ps.executeQuery();
            //go through result set, one row at a time
            while(rs.next()){
                //get result sets
                UnitedStateOfAmerica state = new UnitedStateOfAmerica(
                        rs.getString("State_Code"),
                        rs.getString("State_Name")
                );
                unitedStateOfAmericaList.add(state);
            }
        }
        catch(SQLException ex){
            ex.printStackTrace(System.out);
        }
        return unitedStateOfAmericaList;
    }
}
