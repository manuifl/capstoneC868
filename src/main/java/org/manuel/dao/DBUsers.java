/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.manuel.models.Customer;
import org.manuel.models.User;

import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//import java.time.LocalDateTime;
//import java.time.LocalDate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Manuel Fuentes
 */
public class DBUsers {

    private static final Connection conn = DBConnection.getConnection();

    // private constructor for non-instantiable utility class
    private DBUsers() {
        throw new AssertionError();
    }

    public static boolean validUserName(String username){
        Connection conn = DBConnection.getConnection();
        try{
            String sql = "SELECT * FROM users WHERE User_Name = ?;";
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, username);
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

    public static User secureLogin(String username, String hash) {
        User newUser = new User();
        String sql = "SELECT * FROM users WHERE User_Name = ? AND Hash = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, hash);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                newUser.setUserName(resultSet.getString("User_Name"));
                newUser.setPassword(resultSet.getString("Hash"));
                newUser.setId(resultSet.getInt("User_ID"));

            } else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
        return newUser;
    }


    /**
     * Gets user salt to generate and compare hash
     * @param userName String
     * @return byte[]
     */
    public static byte[] getUserSalt(String userName){
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT Hash, Salt FROM users WHERE User_Name = ?";
        byte[] byteSalt = null;
        try {
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, userName);
            ps.execute();
            ResultSet rs = ps.executeQuery();
            //String hashedPassword = null;
            if (rs.next()) {
                String salt = rs.getString("Salt");
                byteSalt = toByteArray(salt);
            }
        } catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        return byteSalt;
    }

    public static String getPasswordHash(String passwordToHash, byte[] salt) {
        byte[] bytes = null;
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //Add the salt to the message digest
            md.update(salt);
            //Digest the password
            bytes = md.digest(passwordToHash.getBytes());
            //Reset Message Digest
            md.reset();
            //Convert it to hexadecimal format
        } catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
        }
        if (bytes != null) {
            return toStringHex(bytes);
        } else {
            throw new NullPointerException();
        }

    }

    public static String toStringHex(byte[] bytes){
        String hexString;
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        //Get complete hashed password in hex format
        hexString = sb.toString();
        return hexString;

    }

    public static byte[] toByteArray(String hex) {
        //Create byte array with half of the hex string length
        byte[] binary = new byte[hex.length() / 2];
        for(int i = 0; i < binary.length; i++){
            //Parse 2 chars from base 16 to 2
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        //return the byte array
        return binary;
    }





}
