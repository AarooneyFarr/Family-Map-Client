package com.example.familymapclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import model.Event;
import model.Person;

public class DataCache {

    private static final DataCache instance = new DataCache();

    public static DataCache getInstance(){
        return instance;
    }

    private DataCache() {
        people = new HashMap<>();
        currentUser = new Person("", "", "", " ","","","","");
        fathersSidePeople = new HashMap<>();
        mothersSidePeople = new HashMap<>();
        otherPeople = new HashMap<>();

        events = new HashMap<>();
        allEvents = new HashMap<>();
        fatherSideEvents = new HashMap<>();
        motherSideEvents = new HashMap<>();

        lifeStoryLines = false;
        familyTreeLines = false;
        spouseLines = false;
        fatherFilter = true;
        motherFilter = true;
        maleFilter = true;
        femaleFilter = true;
    }

    Map<String, Person> people;
    Map<String, Person> mothersSidePeople;
    Map<String, Person> fathersSidePeople;

    Map<String, Person> otherPeople;

    Person currentUser;
    Map<String, Event> events;
    Map<String, Event> allEvents;

    Map<String, Event> fatherSideEvents;

    Map<String, Event> motherSideEvents;

    Boolean lifeStoryLines, familyTreeLines, spouseLines, fatherFilter, motherFilter, maleFilter, femaleFilter;

    public Person getCurrentUser() {
        return currentUser;
    }

    public void addPeople(Person[] peopleArray){
        for(Person person : peopleArray){
            people.put(person.getPersonID(), person);
        }

        fillPeopleSides();
    }

    public void addEvents(Event[] eventArray){
        for(Event event : eventArray){
            events.put(event.getEventID(), event);
            allEvents.put(event.getEventID(), event);
        }

        fillEventSides();
    }

    public void fillPeopleSides(){
        if(currentUser.getFatherID() != null){
            Person father = people.get(currentUser.getFatherID());
            fathersSidePeople.put(father.getPersonID(), father);
            fathersSidePeople.putAll(addRelatives(father));
        }

        if(currentUser.getMotherID() != null){
            Person mother = people.get(currentUser.getMotherID());
            mothersSidePeople.put(mother.getPersonID(), mother);
            mothersSidePeople.putAll(addRelatives(mother));
        }
    }

    public Map<String, Person> addRelatives(Person child){
        Map<String, Person> relatives = new HashMap<>();

        if(child.getFatherID() != null){
            Person father = people.get(child.getFatherID());
            relatives.put(father.getPersonID(), father);
            relatives.putAll(addRelatives(father));
        }

        if(child.getMotherID() != null){
            Person mother = people.get(child.getMotherID());
            relatives.put(mother.getPersonID(), mother);
            relatives.putAll(addRelatives(mother));
        }

        return relatives;
    }

    private void fillEventSides(){
        for(Map.Entry<String, Event> entry : allEvents.entrySet()){
            if(mothersSidePeople.containsKey(entry.getValue().getPersonID())){
                motherSideEvents.put(entry.getKey(), entry.getValue());
            }
            else{
                fatherSideEvents.put(entry.getKey(), entry.getValue());
            }

            if(fathersSidePeople.containsKey(entry.getValue().getPersonID())){
                fatherSideEvents.put(entry.getKey(), entry.getValue());
            }
            else{
                motherSideEvents.put(entry.getKey(), entry.getValue());
            }

            if(entry.getValue().getPersonID().equals(currentUser.getPersonID())){
                fatherSideEvents.put(entry.getKey(), entry.getValue());
                motherSideEvents.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public String getRelationString(Person mainPerson, Person relatedPerson){
        if(mainPerson.getMotherID() != null && mainPerson.getMotherID().equals(relatedPerson.getPersonID())){
            return "Mother";
        }
        else if(mainPerson.getFatherID() != null && mainPerson.getFatherID().equals(relatedPerson.getPersonID())){
            return "Father";
        }
        else if(mainPerson.getSpouseID() != null && mainPerson.getSpouseID().equals(relatedPerson.getPersonID())){
            return "Spouse";
        }
        else {
            return "Child";
        }
    }


    public void setCurrentUser(Person person){
        currentUser = person;
    }

    public void clear(){
        people = new HashMap<>();
        currentUser = new Person("", "", "", " ","","","","");
        fathersSidePeople = new HashMap<>();
        mothersSidePeople = new HashMap<>();
        otherPeople = new HashMap<>();

        events = new HashMap<>();
        allEvents = new HashMap<>();
        fatherSideEvents = new HashMap<>();
        motherSideEvents = new HashMap<>();

        lifeStoryLines = false;
        familyTreeLines = false;
        spouseLines = false;
        fatherFilter = true;
        motherFilter = true;
        maleFilter = true;
        femaleFilter = true;
    }

    public void setFilters(Boolean maleFilterOn, Boolean femaleFilterOn, Boolean fatherFilterOn, Boolean motherFilterOn){
        events.clear();

        if(fatherFilterOn){
            events.putAll(fatherSideEvents);
        }

        if(motherFilterOn){
            events.putAll(motherSideEvents);
        }

        for(Map.Entry<String, Event> entry : allEvents.entrySet()){
            Person person = people.get(entry.getValue().getPersonID());

            if(!maleFilterOn && person.getGender().equals("m")){
                events.remove(entry.getKey());
            }

            if(!femaleFilterOn && person.getGender().equals("f")){
                events.remove(entry.getKey());
            }
        }


    }

    public void setLifeStoryLines(Boolean lifeStoryLines) {
        this.lifeStoryLines = lifeStoryLines;
    }

    public void setFamilyTreeLines(Boolean familyTreeLines) {
        this.familyTreeLines = familyTreeLines;
    }

    public void setSpouseLines(Boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public void setFatherFilter(Boolean fatherFilter) {
        this.fatherFilter = fatherFilter;
    }

    public void setMotherFilter(Boolean motherFilter) {
        this.motherFilter = motherFilter;
    }

    public void setMaleFilter(Boolean maleFilter) {
        this.maleFilter = maleFilter;
    }

    public void setFemaleFilter(Boolean femaleFilter) {
        this.femaleFilter = femaleFilter;
    }

    public List<Person> getPeopleList(String searchTerm){
        List<Person> peopleList = new ArrayList<>();
        if(searchTerm.equals(" ") || searchTerm.equals("")) return peopleList;
        String term = searchTerm.toLowerCase();

        for(Map.Entry<String, Person> entry : people.entrySet()){
            Person tPerson = entry.getValue();
            if(tPerson.getFirstName().toLowerCase().contains(term) ||
                    tPerson.getLastName().toLowerCase().contains(term)) {
                peopleList.add(entry.getValue());
            }
        }

        return peopleList;
    }

    public List<Person> getFamilyList(String personId){
        List<Person> peopleList = new ArrayList<>();
        Person person = people.get(personId);

        //add spouse and children
        for(Map.Entry<String, Person> entry : people.entrySet()){
            Person tPerson = entry.getValue();
            if(tPerson.getFatherID() != null){
                if(tPerson.getFatherID().equals(personId)){
                    peopleList.add(entry.getValue());
                }
            }
            if(tPerson.getMotherID() != null){
                if(tPerson.getMotherID().equals(personId)){
                    peopleList.add(entry.getValue());
                }
            }
            if(tPerson.getSpouseID() != null){
                if(tPerson.getSpouseID().equals(personId)){
                    peopleList.add(entry.getValue());
                }
            }

        }

        //add parents
        if(person.getFatherID() != null) {
            peopleList.add(people.get(person.getFatherID()));
        }

        if(person.getMotherID() != null) {
            peopleList.add(people.get(person.getMotherID()));
        }

        return peopleList;
    }

    public List<Event> getEventList(String searchTerm){
        List<Event> eventList = new ArrayList<>();
        if(searchTerm.equals(" ") || searchTerm.equals("")) return eventList;
        String term = searchTerm.toLowerCase();

        for(Map.Entry<String, Event> entry : events.entrySet()){
            Event event = entry.getValue();
            if(event.getEventType().toLowerCase().contains(term) ||
                    event.getCountry().toLowerCase().contains(term) ||
                    event.getCity().toLowerCase().contains(term) ||
                    event.getEventType().toLowerCase().contains(term)){

                eventList.add(entry.getValue());
            }
        }

        return eventList;
    }

    public List<Event> getPersonEvents(String personId){
        List<Event> eventList = new ArrayList<>();

        if(people.get(personId) == null) return eventList;


        Person selectedPerson = people.get(personId);

        Object[] sortedEvents =  getSortedEvents(selectedPerson);

        for (int i = 0; i < sortedEvents.length; i++) {
            Map.Entry<Integer, Event> firstEntry = (Map.Entry<Integer, Event>) sortedEvents[i];
            Event event = firstEntry.getValue();

            eventList.add(event);
        }

        return eventList;
    }

    Object[] getSortedEvents(Person person){
        SortedMap<Integer, Event> sortedEvents = new TreeMap<>();

        //loop through events
        for (Map.Entry<String, Event> event : events.entrySet()) {
            Event currentEvent = event.getValue();

            // if this event belongs to the person, add it to the sorted map
            if (currentEvent.getPersonID().equals(person.getPersonID())) {
                Integer offset = currentEvent.getYear() * 10;
                Integer counter = 1;

                if(sortedEvents.containsKey(offset)){
                    sortedEvents.put(offset + counter, currentEvent);
                    counter++;
                }
                else{
                    sortedEvents.put(currentEvent.getYear() * 10, currentEvent);
                }

            }
        }

        return sortedEvents.entrySet().toArray();
    }
}
