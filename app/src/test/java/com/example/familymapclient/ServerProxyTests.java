package com.example.familymapclient;

import static com.example.familymapclient.ServerProxy.getEvents;
import static com.example.familymapclient.ServerProxy.getPeople;
import static com.example.familymapclient.ServerProxy.login;
import static com.example.familymapclient.ServerProxy.register;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.security.SecureRandom;

import result.EventsResponse;
import result.LoginResponse;
import result.PersonResponse;
import result.PersonsResponse;
import result.RegisterResponse;


public class ServerProxyTests {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();


    private final String host = "localhost";
    private final String port = "8080";

    private static String username, password;

    public static String generateRandomString(int length) {
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            randomString.append(CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length())));
        }
        return randomString.toString();
    }

    @BeforeClass
    public static void setUp() {
        username = generateRandomString(5);
        password = generateRandomString(5);
    }
    @Test
    public void testLogin(){
        LoginResponse res = login(host, port, "sheila", "parker");
        assertEquals(res.isSuccess(), true);
    }

    @Test
    public void testLoginFail(){

        LoginResponse res = login(host, port, username, password);
        assertEquals(res.isSuccess(), false);
    }

    @Test
    public void testRegister(){
        String username = generateRandomString(5);
        String password = generateRandomString(5);
        String firstName = generateRandomString(5);
        String lastName = generateRandomString(5);
        String mail = generateRandomString(5);

        RegisterResponse res = register(host, port, username, password, firstName, lastName, mail, "f");
        assertEquals(res.isSuccess(), true);

        LoginResponse res2 = login(host, port, username, password);
        assertEquals(res2.isSuccess(), true);
    }

    @Test
    public void testRegisterFail(){
        RegisterResponse res = register(host, port, username, password, "shee", "parks", "teddy@mail", "f");
        assertEquals(res.isSuccess(), false);
    }

    @Test
    public void testPersons(){
        String username = generateRandomString(5);
        String password = generateRandomString(5);
        String firstName = generateRandomString(5);
        String lastName = generateRandomString(5);
        String mail = generateRandomString(5);

        RegisterResponse res = register(host, port, username, password, firstName, lastName, mail, "f");
        assertEquals(res.isSuccess(), true);


        PersonsResponse res2 = getPeople(host, port, res.getAuthtoken());
        assertEquals(res2.isSuccess(), true);
    }

    @Test
    public void testPersonsFail(){

        PersonsResponse res = getPeople(host, port, "authtoken");
        assertNull(res);
    }

    @Test
    public void testEvents(){
        String username = generateRandomString(5);
        String password = generateRandomString(5);
        String firstName = generateRandomString(5);
        String lastName = generateRandomString(5);
        String mail = generateRandomString(5);

        RegisterResponse res = register(host, port, username, password, firstName, lastName, mail, "f");
        assertEquals(res.isSuccess(), true);


        EventsResponse res2 = getEvents(host, port, res.getAuthtoken());
        assertEquals(res2.isSuccess(), true);
    }

    @Test
    public void testEventsFail(){

        EventsResponse res = getEvents(host, port, "authtoken");
        assertEquals(res.isSuccess(), false);
    }

}