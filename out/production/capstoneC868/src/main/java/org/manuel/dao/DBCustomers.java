/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.dao;

import org.manuel.Main;
import org.manuel.models.City;
import org.manuel.models.Customer;

import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.manuel.models.UnitedStateOfAmerica;


/**
 * Contains all database commands related to Customer Models Class
 * @author Manuel Fuentes
 */
public class DBCustomers {
    private static final Connection conn = DBConnection.getConnection();

    // private constructor for non-instantiable utility class
    private DBCustomers() {
        throw new AssertionError();
    }

    /**
     * ObservableList containing a ResultSet of all records in the customers table. A string containing a
     * SELECT statement is passed to a PreparedStatement.
     * @return ObservableList
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        try {
            //MySQL Statement
//            String sql = "SELECT c.Customer_ID, Customer_Name, Address, Postal_Code, Phone, Email, Membership_Status, c.Create_Date, c.Created_By,\n" +
//                    "c.Last_Update, c.Last_Updated_By, c.City_ID, ci.City_Name, ci.State_Code, s.State_Name\n" +
//                    "FROM customers c \n" +
//                    "INNER JOIN cities ci ON c.City_ID = ci.City_ID\n" +
//                    "INNER JOIN states s ON ci.State_Code = s.State_Code;";
            String sql = """
                    SELECT c.Customer_ID, Customer_Name, Address, Postal_Code, Phone, Email, Membership_Status,
                    c.Create_Date, c.Created_By, c.Last_Update, c.Last_Updated_By, c.City_ID,
                    ci.City_Name, ci.State_Code, s.State_Name
                    FROM customers c
                    INNER JOIN cities ci ON c.City_ID = ci.City_ID
                    INNER JOIN states s ON ci.State_Code = s.State_Code;""";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            //PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            //execute query and get results
            ResultSet rs = ps.executeQuery();
            //go through result set, one row at a time
            while (rs.next()) {
                //get result sets
                UnitedStateOfAmerica state = new UnitedStateOfAmerica(
                        rs.getString("State_Code"),
                        rs.getString("State_Name")
                );
                City city = new City(
                        rs.getInt("City_ID"),
                        rs.getString("City_Name"),
                        state
                );
                Customer customer = new Customer(
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getString("Address"),
                        rs.getString("Postal_Code"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        city
                );
                customer.setActiveMember(rs.getBoolean("Membership_Status"));
                customer.setCreateDate(rs.getDate("Create_Date"));
                customer.setCreatedBy(rs.getString("Created_By"));
                customer.setLastUpdate(rs.getDate("Last_Update"));
                customer.setLastUpdatedBy(rs.getString("Last_Updated_By"));

                customerList.add(customer);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        return customerList;

    }

    public static ObservableList<Customer> getAllActiveCustomers() throws SQLException {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        try {
            //MySQL Statement
            String sql = """
                    SELECT c.Customer_ID, Customer_Name, Address, Postal_Code, Phone, Email, Membership_Status,
                    c.Create_Date, c.Created_By, c.Last_Update, c.Last_Updated_By, c.City_ID,
                    ci.City_Name, ci.State_Code, s.State_Name
                    FROM customers c
                    INNER JOIN cities ci ON c.City_ID = ci.City_ID
                    INNER JOIN states s ON ci.State_Code = s.State_Code
                    WHERE Membership_Status IS TRUE;""";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            //PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            //execute query and get results
            ResultSet rs = ps.executeQuery();
            //go through result set, one row at a time
            while (rs.next()) {
                //get result sets
                UnitedStateOfAmerica state = new UnitedStateOfAmerica(
                        rs.getString("State_Code"),
                        rs.getString("State_Name")
                );
                City city = new City(
                        rs.getInt("City_ID"),
                        rs.getString("City_Name"),
                        state
                );
                Customer customer = new Customer(
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getString("Address"),
                        rs.getString("Postal_Code"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        city
                );
                //Optional customer parameters
                customer.setActiveMember(rs.getBoolean("Membership_Status"));
                customer.setCreateDate(rs.getDate("Create_Date"));
                customer.setCreatedBy(rs.getString("Created_By"));
                customer.setLastUpdate(rs.getDate("Last_Update"));
                customer.setLastUpdatedBy(rs.getString("Last_Updated_By"));

                customerList.add(customer);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return customerList;
    }

    public static void addCustomer(Customer c) {
        try {
//            String sql = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Email, Membership_Status, Create_Date, Created_By, Last_Update, Last_Updated_By, City_ID) VALUES (NULL, ?, ?, ?, ?, ?, 1, NOW(), ?, NOW(), ?, ?);";
            String sql = """
                    INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Email,
                    Membership_Status, Create_Date, Created_By, Last_Update, Last_Updated_By, City_ID)
                    VALUES (NULL, ?, ?, ?, ?, ?, 1, NOW(), ?, NOW(), ?, ?);""";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPostalCode());
            ps.setString(4, c.getPhoneNumber());
            ps.setString(5, c.getEmailAddress());
            ps.setString(6, Main.getUser().getUserName());
            ps.setString(7, Main.getUser().getUserName());
            ps.setInt(8, c.getCity().getCityId());
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);

        }

    }

public static void modifyCustomer(Customer c) {
    try {
//        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Email = ?, Last_Update = NOW(), Last_Updated_By = ?, City_ID = ? WHERE Customer_ID = ?;";
        String sql = """
                UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?,
                Phone = ?, Email = ?, Last_Update = NOW(), Last_Updated_By = ?,
                City_ID = ? WHERE Customer_ID = ?;""";
        DBQuery.setPreparedStatement(conn, sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setString(1, c.getName());
        ps.setString(2, c.getAddress());
        ps.setString(3, c.getPostalCode());
        ps.setString(4, c.getPhoneNumber());
        ps.setString(5, c.getEmailAddress());
        ps.setString(6, Main.getUser().getUserName());
        ps.setInt(7, c.getCity().getCityId());
        ps.setInt(8, c.getId());

        ps.execute();

    } catch (SQLException ex) {
        ex.printStackTrace(System.out);

    }
}

    /**
     * Performs DELETE statement to the customer table.
     * To follow table constraints, the customers appointments must be deleted
     * before deleting the customer
     *
     * @param customerId int
     */
    public static void deleteCustomer(int customerId) {
        try {
            // Delete all appointments assigned to the customer
            String sql1 = "DELETE FROM appointments WHERE Customer_ID = ?;";
            DBQuery.setPreparedStatement(conn, sql1);
            PreparedStatement ps1 = DBQuery.getPreparedStatement();
            ps1.setInt(1, customerId);
            ps1.execute();

            // Delete customer
            String sql2 = "DELETE FROM customers WHERE Customer_ID = ?;";
            DBQuery.setPreparedStatement(conn, sql2);
            PreparedStatement ps2 = DBQuery.getPreparedStatement();
            ps2.setInt(1, customerId);
            ps2.execute();

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

    }

    public static boolean checkEmailConflict(String email){
        try{
            String sql = "SELECT Email FROM customers WHERE Email = ?;";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, email);
            ps.execute();
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                    return true;
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean checkEmailConflictForModify(String email, int customerId){
        try{
            String sql = "SELECT Email, Customer_ID FROM customers WHERE Email = ?;";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, email);
            ps.execute();
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("Customer_ID") != customerId;
        }
        catch(SQLException ex){
            //ResultSet is empty therefore email is unique
            return false;
        }

    }

    /**
     * ResultSet of PreparedStatement containing the number of appointments assigned to a customer
     *
     * @param customerId int
     * @return ResultSet containing the number of appointments assigned to a customer
     */
    public static Integer getNumberOfCustomerAppointments(int customerId) {
        int rsCount = 0;
        try {
            String sql = "SELECT COUNT(*) AS Qty FROM appointments WHERE Customer_ID = ?;";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setInt(1, customerId);
            ps.execute();
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rsCount = rs.getInt("Qty");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return rsCount;
    }

}

