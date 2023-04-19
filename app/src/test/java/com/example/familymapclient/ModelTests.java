package com.example.familymapclient;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import model.Person;
import model.Event;


public class ModelTests {
    private static Person child, father, mother;
    private static Event childEvent, childEvent2, childEvent3, fatherEvent, motherEvent;

    private static DataCache data;

    @BeforeClass
    public static void setUp(){
         child = new Person("id", "timmy", " tom", "johna", "m", "id2", "id3", null);
         father = new Person("id2", "timmy", " tommy", " johna", "m", null, null, "jule");
         mother = new Person("id3", "timmy", " tina", " johna", "f", null, null, "jule");

         childEvent = new Event("childEvent", "timmy", "id", 2f,2f, "US", "provo", "birth", 1970);
         childEvent2 = new Event("childEvent2", "timmy", "id", 2f,2f, "US", "provo", "marriage", 1980);
         childEvent3 = new Event("childEvent3", "timmy", "id", 2f,2f, "US", "provo", "death", 1990);
         fatherEvent = new Event("fatherEvent", "timmy", "id2", 2f,2f, "US", "provo", "death", 1990);
         motherEvent = new Event("motherEvent", "timmy", "id3", 2f,2f, "US", "provo", "death", 1990);

         data = DataCache.getInstance();

         data.setCurrentUser(child);
         data.addPeople(new Person[]{child, mother, father});
         data.addEvents(new Event[]{childEvent, childEvent2, childEvent3, motherEvent, fatherEvent});

    }

    @Test
    public void testRelationships(){
        String relation = data.getRelationString(child, father);
        assertEquals(relation, "Father");
    }

    @Test
    public void testRelationshipsFail(){
        String relation = data.getRelationString(child, child);
        assertEquals(relation, "Child");
    }

    @Test
    public void testFilters(){
        data.setFilters(true, false,true, true);

        assertNotNull(data.events.get("fatherEvent"));
        assertNotNull(data.events.get("childEvent"));
        assertNull(data.events.get("motherEvent"));
    }

    @Test
    public void testFiltersFail(){
        data.setFilters(false, false,true, true);

        assertNull(data.events.get("fatherEvent"));
        assertNull(data.events.get("childEvent"));
        assertNull(data.events.get("motherEvent"));
    }

    @Test
    public void testSorting(){
        data.setFilters(true, true,true, true);

        List<Event> events = data.getPersonEvents("id");
        int lastEventYear = 0;
        for(Event event : events){
            assertTrue((lastEventYear < event.getYear()));
            lastEventYear = event.getYear();
        }

    }

    @Test
    public void testSortingFail(){
        data.setFilters(true, true,true, true);

        List<Event> events = data.getPersonEvents("id5");
        assertTrue(events.isEmpty());
    }

    @Test
    public void testSearch(){
        data.setFilters(true, true,true, true);


        List<Person> people = data.getPeopleList("om");
        List<Event> events = data.getEventList("de");


        assertTrue(events.size() == 3);
        assertTrue(people.size() == 2);

    }

    @Test
    public void testSearchFail(){
        data.setFilters(true, true,true, true);


        List<Person> people = data.getPeopleList("id5");
        List<Event> events = data.getEventList("id5");
        assertTrue(events.isEmpty());
        assertTrue(people.isEmpty());
    }
}
