/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manuel.utilities.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Manuel Fuentes
 */
public class StringValidator {

    public static boolean customerNameRegex(String str){
        String regex = "^[a-zA-Z0-9\\s-]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    public static boolean customerAddressRegex(String str){
        String regex = "^[a-zA-Z0-9\\s.,#\\-]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    public static boolean customerPostalCodeRegex(String str){
        String regex = "^[a-zA-Z0-9\\s\\-]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    public static boolean customerPhoneRegex(String str){
        String regex = "^[0-9\\s().\\-+]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean customerEmailRegex(String str){
        String regex = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    public static boolean appointmentTitleRegex(String str){
        String regex = "^[a-zA-Z0-9\\s-]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    public static boolean appointmentDescriptionRegex(String str){
        String regex = "^[a-zA-Z0-9\\s.,#\\-'?!]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

}
