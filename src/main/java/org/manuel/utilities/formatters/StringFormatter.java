/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.utilities.formatters;

/**
 *
 * @author Manuel Fuentes
 */
public class StringFormatter {
    
    public static String toUpperFirst(String str){
                //String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
                return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
}
