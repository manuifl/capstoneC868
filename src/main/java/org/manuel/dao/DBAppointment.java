/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.manuel.models.*;
import org.manuel.uicontrols.AlertBoxes;
import org.manuel.utilities.formatters.TZConvert;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Contains all database commands related to the Appointment Models Class
 * Non-instantiable utility class
 * @author Manuel Fuentes
 */
public class DBAppointment {

    private static final Connection conn = DBConnection.getConnection();

    // private constructor for non-instantiable utility class
    private DBAppointment() {
        throw new AssertionError();
    }

    /**
     * ObservableList containing a ResultSet of all records in the appointments
     * table
     * @return ObservableList
     * @throws SQLException exception
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        try {
            String sql = """
                    SELECT a.Appointment_ID, a.Title, a.Description, a.Location_ID, a.Customer_ID, a.User_ID,
                    a.Type, a.Start, a.End, a.Create_Date, a.Created_By, a.Last_Update, a.Last_Updated_By,
                    l.Location, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, c.Email, c.City_ID,
                    ci.City_Name, ci.State_Code
                    FROM appointments a
                    INNER JOIN customers c ON a.Customer_ID = c.Customer_ID
                    INNER JOIN cities ci ON c.City_ID = ci.City_ID
                    INNER JOIN users u ON a.User_ID = u.User_ID
                    INNER JOIN locations l ON a.Location_ID = l.Location_ID
                    ORDER BY a.Start DESC;""";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            //execute query and get results
            ResultSet rs = ps.executeQuery();
            //go through result set, one row at a time
            while (rs.next()) {
                //get result sets
                Location location = new Location(rs.getInt("Location_ID"),
                        rs.getString("Location")
                );
                UnitedStateOfAmerica state = new UnitedStateOfAmerica(
                        rs.getString("State_Code")
                );
                City city = new City(rs.getInt("City_ID"),
                        rs.getString("City_Name"), state
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
                //Convert appointment times from UTC to LocalTime
                Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("Start"));
                Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("End"));
                Appointment appointment = new Appointment(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Type"),
                        location,
                        startUTCTimestamp.toLocalDateTime().toLocalDate(),
                        startUTCTimestamp,
                        endUTCTimestamp,
                        customer
                );
                appointment.setCreateDate(rs.getDate("Create_Date"));
                appointment.setCreatedBy(rs.getString("Created_By"));
                appointment.setLastUpdate(rs.getDate("Last_Update"));
                appointment.setLastUpdatedBy(rs.getString("Last_Updated_By"));
                appointmentList.add(appointment);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        return appointmentList;

    }

    /**
     * ObservableList with appointments that match the specified range
     *
     * @param rangeStart Timestamp
     * @param rangeEnd Timestamp
     * @return ObservableList
     */
    public static ObservableList<Appointment> getAppointmentsInRange(Timestamp rangeStart, Timestamp rangeEnd) throws SQLException {
        ObservableList<Appointment> appointmentInRangeList = FXCollections.observableArrayList();
        try {
            String sql = """
                    SELECT a.Appointment_ID, a.Title, a.Description, l.Location, a.Location_ID, a.Type, a.Start, a.End, c.Customer_Name, a.Customer_ID
                    FROM appointments a
                    INNER JOIN customers c ON a.Customer_ID = c.Customer_ID
                    INNER JOIN locations l ON a.Location_ID = l.Location_ID
                    WHERE a.Start BETWEEN ? AND ?;""";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setTimestamp(1, rangeStart);
            ps.setTimestamp(2, rangeEnd);
            //execute query and get results
            ResultSet rs = ps.executeQuery();
            //go through result set, one row at a time
            while (rs.next()) {
                //get result sets
                Location location = new Location(
                        rs.getInt("Location_ID"),
                        rs.getString("Location")
                );
                Customer customer = new Customer(
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name")
                );

                Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("Start"));
                Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("End"));
                Appointment appointment = new Appointment(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Type"),
                        location,
                        startUTCTimestamp.toLocalDateTime().toLocalDate(),
                        startUTCTimestamp,
                        endUTCTimestamp,
                        customer
                );
                appointmentInRangeList.add(appointment);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return appointmentInRangeList;
    }

    /**
     * Performs insert into statement to the appointments table
     * @param appointment Appointment
     */

    public static void addAppointment(Appointment appointment) {
        try {
            String sql = """
                    INSERT INTO appointments (Appointment_ID, Title, Description, Location_ID, Type, Start,
                    End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID)
                    VALUES (NULL, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?);""";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setInt(3, appointment.getLocation().getLocationId());
            ps.setObject(4, appointment.getType());
            ps.setTimestamp(5, appointment.getStartTime());
            ps.setTimestamp(6, appointment.getEndTime());
            ps.setString(7, appointment.getUser().getUserName());
            ps.setString(8, appointment.getUser().getUserName());
            ps.setInt(9, appointment.getCustomer().getId());
            ps.setInt(10, appointment.getUser().getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            //System.out.println("Inserting to database");
        }
    }

    /**
     * Performs update statement to the appointments table
     * @param appointment Appointment
     */
    public static void modifyAppointment(Appointment appointment) {

        try {
            String sql = """
                    UPDATE appointments SET Title = ?, Description = ?, Location_ID = ?, Type = ?,
                    Start = ?, End = ?, Last_Update = NOW(), Last_Updated_By = ?, Customer_ID = ?
                    WHERE Appointment_ID = ?;""";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setInt(3, appointment.getLocation().getLocationId());
            ps.setObject(4, appointment.getType());
            ps.setTimestamp(5, appointment.getStartTime());
            ps.setTimestamp(6, appointment.getEndTime());
            ps.setString(7, appointment.getUser().getUserName());
            ps.setInt(8, appointment.getCustomer().getId());
            ps.setInt(9, appointment.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            //System.out.println("Updating database");
        }
    }

    /**
     * Performs delete statement to the appointments table
     *
     * @param appointmentId int
     */
    public static void deleteAppointment(int appointmentId) {
        try {
            String sql = "DELETE FROM appointments WHERE Appointment_ID = ?;";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setInt(1, appointmentId);
            ps.execute();

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
    }

    /*------------------------------------------------------------------------------*/
    /*---------------------------------Conflict Check-------------------------------*/
    /*------------------------------------------------------------------------------*/

    /**
     * Check that customer does not have a conflict with an existing record when adding a new appointment
     * @param newStart   Timestamp
     * @param newEnd     Timestamp
     * @param customerId int
     * @return String
     */
    public static String checkCustomerDateTimeConflict(Timestamp newStart, Timestamp newEnd, int customerId) {
        StringBuilder conflicts = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        try {
            String sql = """
                    SELECT a.Appointment_ID, a.Start, a.End, a.Customer_ID, c.Customer_Name, DATE(a.Start) date
                    FROM appointments a
                    INNER JOIN customers c ON a.Customer_ID = c.Customer_ID
                    WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End)
                    AND (a.Customer_ID = ?);""";

            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            ps.setTimestamp(1, newStart);
            ps.setTimestamp(2, newEnd);
            ps.setTimestamp(3, newStart);
            ps.setTimestamp(4, newEnd);
            ps.setInt(5, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.Start"));
                Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.End"));

                String start = startUTCTimestamp.toLocalDateTime().format(formatter);
                String end = endUTCTimestamp.toLocalDateTime().format(formatter);

                conflicts.append("Customer ")
                        .append(rs.getString("c.Customer_Name"))
                        .append(" has a conflicting appointment on ")
                        .append(rs.getDate("date"))
                        .append(" from ").append(start)
                        .append(" to ")
                        .append(end);
            } else {
                return null;
            }

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return conflicts.toString();
    }

    /**
     * Check that customer does not have a conflict with an existing record when modifying appointment
     * @param newStart Timestamp
     * @param newEnd Timestamp
     * @param customerId int
     * @param appointmentId String
     * @return String
     */
    public static String checkCustomerDateTimeConflictForModify(Timestamp newStart, Timestamp newEnd, int customerId, int appointmentId) {
        StringBuilder conflicts = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        try {
            String sql = """
                    SELECT a.Appointment_ID, a.Start, a.End, a.Customer_ID, c.Customer_Name, DATE(a.Start) date
                    FROM appointments a
                    INNER JOIN customers c ON a.Customer_ID = c.Customer_ID
                    WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End)
                    AND (a.Customer_ID = ?);""";

            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            ps.setTimestamp(1, newStart);
            ps.setTimestamp(2, newEnd);
            ps.setTimestamp(3, newStart);
            ps.setTimestamp(4, newEnd);
            ps.setInt(5, customerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            //If conflict is with the current appointment being modified, return null
            if (rs.getInt("a.Customer_ID") == customerId && rs.getInt("a.Appointment_ID") == appointmentId) {
                return null;
            } else {
                Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.Start"));
                Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.End"));

                String start = startUTCTimestamp.toLocalDateTime().format(formatter);
                String end = endUTCTimestamp.toLocalDateTime().format(formatter);

                conflicts.append("Customer ")
                        .append(rs.getString("c.Customer_Name"))
                        .append(" has a conflicting appointment on ")
                        .append(rs.getDate("date"))
                        .append(" from ")
                        .append(start)
                        .append(" to ")
                        .append(end);

                return conflicts.toString();
            }

        } catch (SQLException ex) {
            //ResultSet is empty therefore an appointment can be made
            return null;
        }
    }

    /**
     * Check that location is not occupied when adding a new appointment
     * @param newStart   Timestamp
     * @param newEnd     Timestamp
     * @param locationId int
     * @return String
     */
    public static String checkLocationDateTimeConflict(Timestamp newStart, Timestamp newEnd, int locationId) {
        StringBuilder locationConflict = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        try {
            String sql = """
                    SELECT a.Appointment_ID, a.Start, a.End, a.Location_ID, l.Location, DATE(a.Start) date
                    FROM appointments a
                    INNER JOIN locations l ON a.Location_ID = l.Location_ID
                    WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End)
                    AND (a.Location_ID = ?);""";

            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            ps.setTimestamp(1, newStart);
            ps.setTimestamp(2, newEnd);
            ps.setTimestamp(3, newStart);
            ps.setTimestamp(4, newEnd);
            ps.setInt(5, locationId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.Start"));
                Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.End"));

                String start = startUTCTimestamp.toLocalDateTime().format(formatter);
                String end = endUTCTimestamp.toLocalDateTime().format(formatter);

                locationConflict.append("Room")
                        .append(rs.getString("l.Location"))
                        .append(" is already booked on ")
                        .append(rs.getDate("date"))
                        .append(" from ")
                        .append(start)
                        .append(" to ")
                        .append(end);
            } else {
                return null;
            }

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return locationConflict.toString();
    }

    /**
     * Check that location is not occupied when modifying appointment
     * @param newStart Timestamp
     * @param newEnd imestamp
     * @param locationId int
     * @param appointmentId String
     * @return String
     */
    public static String checkLocationDateTimeConflictForModify(Timestamp newStart, Timestamp newEnd, int locationId, int appointmentId) {
        StringBuilder locationConflict = new StringBuilder();
        try {
            String sql = """
                    SELECT a.Appointment_ID, a.Start, a.End, a.Location_ID, l.Location, DATE(a.Start) date
                    FROM appointments a
                    INNER JOIN locations l ON a.Location_ID = l.Location_ID
                    WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End)
                    AND (a.Location_ID = ?);""";

            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            ps.setTimestamp(1, newStart);
            ps.setTimestamp(2, newEnd);
            ps.setTimestamp(3, newStart);
            ps.setTimestamp(4, newEnd);
            ps.setInt(5, locationId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            //If conflict is with the current appointment being modified, return null
            if (rs.getInt("Location_ID") == locationId && rs.getInt("Appointment_ID") == appointmentId) {
                return null;
            } else {
                Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.Start"));
                Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.End"));

                String start = startUTCTimestamp.toString().substring(11, 16);
                String end = endUTCTimestamp.toString().substring(11, 16);

                locationConflict.append("Room")
                        .append(rs.getString("l.Location"))
                        .append(" is already booked on ")
                        .append(rs.getDate("date"))
                        .append(" from ")
                        .append(start)
                        .append(" to ")
                        .append(end);
                return locationConflict.toString();
            }

        } catch (SQLException ex) {
            //ResultSet is empty therefore an appointment can be made
            return null;
        }

    }

    /**
     * String returning appointments grouped by month and type
     *
     * @return StringBuilder.toString()
     */
    public static String generateMonthTypeReport() {
        try {

            StringBuilder monthTypeReport = new StringBuilder();
            String sql = "SELECT MONTHNAME(Start) as Month, Type, COUNT(*) as 'Amount' FROM appointments GROUP BY Month, Type;";

            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //get result sets
                monthTypeReport.append(rs.getString("Month"), 0, 3)
                        .append("   |  ")
                        .append(rs.getString("Amount"))
                        .append("   |   ")
                        .append(rs.getString("type"))
                        .append(System.lineSeparator());

            }
            return monthTypeReport.toString();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            System.out.println("Error getting report: " + ex.getMessage());
            return "Error";
        }

    }


    /**
     * StringBuider appends SQL query results
     * @param customerName String
     * @return StringBuilder.toString()
     */
    public static String generateCustomerScheduleReport(String customerName) {
        try {
            StringBuilder customerScheduleReport = new StringBuilder();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");

            //SQL statement to get the schedule
            String sql = """
                    SELECT a.Appointment_ID, a.Title, a.Description, l.Location, a.Type,
                    a.Start, a.End, c.Customer_Name
                    FROM appointments a
                    INNER JOIN customers c ON a.Customer_ID = c.Customer_ID
                    INNER JOIN locations l ON a.Location_ID = l.Location_ID
                    WHERE Customer_Name = ?
                    ORDER BY Start DESC;""";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, customerName);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                customerScheduleReport.append("No appointments for the selected contact");
            } else {

                do {
                    String appointmentId = rs.getString("a.Appointment_ID");
                    String title = rs.getString("a.Title");
                    String description = rs.getString("a.Description");
                    String type = rs.getString("a.Type");
                    String location = rs.getString("l.Location");
                    //Get database start and end time stored at UTC
                    Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.Start"));
                    Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.End"));

                    String date = startUTCTimestamp.toLocalDateTime().format(dateFormatter);
                    String start = startUTCTimestamp.toLocalDateTime().format(timeFormatter);
                    String end = endUTCTimestamp.toLocalDateTime().format(timeFormatter);

                    customerScheduleReport.append(appointmentId).append(" | ")
                            .append(date).append(" | ")
                            .append(start).append(" | ")
                            .append(end).append(" | ")
                            .append(title).append(" | ")
                            .append(description).append(" | ")
                            .append(type).append(" | ")
                            .append(location).append(System.lineSeparator());

                } while (rs.next());
            }
            return customerScheduleReport.toString();

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            System.out.println("Error getting report: " + ex.getMessage());
            return "Error";
        }

    }

    /**
     * StringBuider appends SQL query results
     *
     * @return StringBuilder.toString()
     */
    public static String generateLocationScheduleReport(String location) {
        try {
            StringBuilder locationScheduleReport = new StringBuilder();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");

            //SQL statement to get the schedule
            String sql = """
                    SELECT a.Appointment_ID, a.Title, a.Description, l.Location, a.Type, a.Start, a.End, c.Customer_Name
                    FROM appointments a
                    INNER JOIN customers c ON a.Customer_ID = c.Customer_ID
                    INNER JOIN locations l ON a.Location_ID = l.Location_ID
                    WHERE l.Location = ?
                    ORDER BY Start DESC;""";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, location);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                locationScheduleReport.append("No appointments for the selected location");
            } else {

                do {
                    String appointmentId = rs.getString("a.Appointment_ID");
                    String customerName = rs.getString("c.Customer_Name");
                    String title = rs.getString("a.Title");
                    String description = rs.getString("a.Description");
                    String type = rs.getString("a.Type");
                    //Get database start and end time stored at UTC
                    Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.Start"));
                    Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("a.End"));
                    String date = startUTCTimestamp.toLocalDateTime().format(dateFormatter);
                    String start = startUTCTimestamp.toLocalDateTime().format(timeFormatter);
                    String end = endUTCTimestamp.toLocalDateTime().format(timeFormatter);

                    locationScheduleReport.append(appointmentId).append(" | ")
                            .append(date).append(" | ")
                            .append(start).append(" | ")
                            .append(end).append(" | ")
                            .append(title).append(" | ")
                            .append(description).append(" | ")
                            .append(type).append(" | ")
                            .append(customerName).append(System.lineSeparator());

                } while (rs.next());
            }
            return locationScheduleReport.toString();

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            System.out.println("Error getting report: " + ex.getMessage());
            return "Error";
        }

    }

    public static ObservableList<Appointment> getAppointmentsForCustomer(int customerID) {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

        try {
            String sql = """
                    SELECT a.Appointment_ID, a.Title, a.Description, a.Location_ID, a.Customer_ID, a.User_ID,
                    a.Type, a.Start, a.End, a.Create_Date, a.Created_By, a.Last_Update, a.Last_Updated_By,
                    l.Location, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, c.Email, c.City_ID,
                    ci.City_Name, ci.State_Code
                    FROM appointments a
                    INNER JOIN customers c ON a.Customer_ID = c.Customer_ID
                    INNER JOIN cities ci ON c.City_ID = ci.City_ID
                    INNER JOIN locations l ON a.Location_ID = l.Location_ID
                    WHERE a.Customer_ID = ?
                    AND a.Start > now()
                    ORDER BY a.Start DESC;""";
            //create a PreparedStatement
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setInt(1, customerID);
            //execute query and get results
            ResultSet rs = ps.executeQuery();

            //go through result set, one row at a time
            while (rs.next()) {
                //get result sets
                Location location = new Location(
                        rs.getInt("Location_ID"),
                        rs.getString("Location")
                );
                UnitedStateOfAmerica state = new UnitedStateOfAmerica(
                        rs.getString("State_Code")
                );
                City city = new City(
                        rs.getInt("City_ID"),
                        rs.getString("City_Name"), state
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
                Timestamp startUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("Start"));
                Timestamp endUTCTimestamp = TZConvert.convertAtUTCToLocal(rs.getTimestamp("End"));

                Appointment appointment = new Appointment(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Type"),
                        location,
                        startUTCTimestamp.toLocalDateTime().toLocalDate(),
                        startUTCTimestamp,
                        endUTCTimestamp,
                        customer
                );
                appointmentList.add(appointment);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return appointmentList;
    }


}
