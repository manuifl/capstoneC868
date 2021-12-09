package org.manuel.dao;

import org.junit.jupiter.api.*;
import org.manuel.models.User;

import static org.junit.jupiter.api.Assertions.*;

class DBUserTest {
    User user;
    String hash;
    byte[] salt;

    @BeforeAll
    static void setUpClass() {
        DBConnection.startConnection();
    }
    @AfterAll
    public static void tearDownClass(){
      DBConnection.closeConnection();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserName("test");
        user.setPassword("testPass");
        hash = "503477f60a1472520eb96c56916421ba0bc4ef9a769fea7ca3210292ae6256a6";
        salt = new byte[]{-93, 6, -20, -105, 91, -67, -95, -80, 51, -83, 79, 66, 86, -118, 85, 55};
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void validUserNameTest() {
        assertTrue(DBUsers.validUserName(user.getUserName()));
    }

    @Test
    void secureLoginTest() {
        String expected = "test";
        User actual = DBUsers.secureLogin(user.getUserName(), hash);
        if (actual == null) throw new AssertionError();
        assertEquals(expected, actual.getUserName());
    }

    @Test
    void getUserSaltTest() {
        byte[] expected = salt;
        byte[] actual = DBUsers.getUserSalt(user.getUserName());
        assertArrayEquals(expected, actual);
    }

    @Test
    void getPasswordHashTest() {
        String expected = hash;
        String actual = DBUsers.getPasswordHash(user.getPassword(), salt);
        assertEquals(expected, actual);
    }

//    @Test
//    void bytesToStringHex() {
////        String expected = "a306ec975bbda1b033ad4f42568a5537";
//        String expected = saltString;
//        String actual = DBUsers.toStringHex(salt);
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void toByteArray() {
////        byte[] expected = {-93, 6, -20, -105, 91, -67, -95, -80, 51, -83, 79, 66, 86, -118, 85, 55};
//        byte[] expected = salt;
//        byte[] actual = DBUsers.toByteArray(saltString);
//        assertArrayEquals(expected, actual);
//    }
}