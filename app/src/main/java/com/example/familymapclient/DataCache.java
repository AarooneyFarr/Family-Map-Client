package com.example.familymapclient;

import java.util.Map;

import model.Event;
import model.Person;

public class DataCache {

    private static DataCache instance = new DataCache();

    public static DataCache getInstance(){
        return instance;
    }

    private DataCache() {

    }

//    List<Person> people;
//    List<Event> events;

    Map<String, Person> people;
    Map<String, Event> events;


}
