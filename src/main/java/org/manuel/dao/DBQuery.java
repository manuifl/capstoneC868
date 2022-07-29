/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class for setting and getting PreparedStatements
 * @author Manuel Fuentes
 */
public class DBQuery {
    
    private static PreparedStatement preparedStatement;


    private static CallableStatement callableStatement;

    /**
     * Creates a PreparedStatement 
     * @param conn Connection
     * @param sqlStatement String
     * @throws SQLException
     */
    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
       preparedStatement = conn.prepareStatement(sqlStatement);
    }

    /**
     * Gets PreparedStatement
     * @return PreparedStatement
     */
    public static PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }


    public static CallableStatement getCallableStatement() {
        return callableStatement;
    }

    public static void setCallableStatement(Connection conn, String sqlStatement) throws SQLException {
        callableStatement = conn.prepareCall(sqlStatement);
        //{call insertR(?,?)}
    }

}
