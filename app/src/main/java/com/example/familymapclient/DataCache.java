package com.example.familymapclient;

import java.util.HashMap;
import java.util.Map;

import model.Event;
import model.Person;

public class DataCache {

    private static DataCache instance = new DataCache();

    public static DataCache getInstance(){
        return instance;
    }

    private DataCache() {
        people = new HashMap<>();
        events = new HashMap<>();
        currentUser = new Person("", "", "", " ","","","","");
    }

//    List<Person> people;
//    List<Event> events;

    Map<String, Person> people;
    Map<String, Event> events;

    public Person getCurrentUser() {
        return currentUser;
    }

    Person currentUser;

    public void addPeople(Person[] peopleArray){
        for(Person person : peopleArray){
            people.put(person.getPersonID(), person);
        }
    }

    public void addEvents(Event[] eventArray){
        for(Event event : eventArray){
            events.put(event.getEventID(), event);
        }
    }

    public void setCurrentUser(Person person){
        currentUser = person;
    }

}
