package com.example.familymapclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import model.Event;
import model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap map;

    DataCache data;
    Polyline spouseLine;
    ArrayList<Polyline> lifeStoryLines;
    ArrayList<Polyline> familyTreeLines;
    private int eventColorCounter;
    private Map<String, Float> colors;

    private TextView personNameText, eventDescriptionText;

    Drawable maleIcon, femaleIcon;

    private LinearLayout personInfoSection;
    private String selectedPersonID;

    public String eventId;

    Boolean finishedDrawing;
    Event selectedEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public MapFragment(String eventID) {
        this.eventId = eventID;
    }

    public MapFragment() {
        eventId = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        data = DataCache.getInstance();

        finishedDrawing = false;

        eventColorCounter = 0;
        colors = new HashMap<>();

        personNameText = view.findViewById(R.id.mapTextPersonName);
        eventDescriptionText = view.findViewById(R.id.mapTextEventDescription);
        personInfoSection = view.findViewById(R.id.personInfoMapFragment);


        maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                colorRes(R.color.male_icon).sizeDp(40);
        femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                colorRes(R.color.female_icon).sizeDp(40);

        if (eventId == null)
            Toast.makeText(this.getActivity(), "Welcome " + data.getCurrentUser().getFirstName() + " " + data.getCurrentUser().getLastName(), Toast.LENGTH_SHORT).show();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    private float getIconColor(Event event) {
        float birthColor = BitmapDescriptorFactory.HUE_AZURE;
        float deathColor = BitmapDescriptorFactory.HUE_RED;
        float marriageColor = BitmapDescriptorFactory.HUE_YELLOW;
        float color1 = BitmapDescriptorFactory.HUE_BLUE;
        float color2 = BitmapDescriptorFactory.HUE_CYAN;
        float color3 = BitmapDescriptorFactory.HUE_GREEN;
        float color4 = BitmapDescriptorFactory.HUE_MAGENTA;
        float color5 = BitmapDescriptorFactory.HUE_ORANGE;
        float color6 = BitmapDescriptorFactory.HUE_ROSE;
        float color7 = BitmapDescriptorFactory.HUE_VIOLET;
        float[] colorsList = {color1, color2, color3, color4, color5, color6, color7};

        String eventType = event.getEventType().toLowerCase();

        if (eventType.equals("birth")) {
            return birthColor;
        } else if (eventType.equals("death")) {
            return deathColor;
        } else if (eventType.equals("marriage")) {
            return marriageColor;
        } else if (colors.containsKey(eventType)) {
            return colors.get(eventType);
        } else {
            colors.put(eventType, colorsList[eventColorCounter % 7]);
            eventColorCounter++;
            return colors.get(eventType);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);


    }

    Event getEarliestEvent(Person person) {
        int earliestDate = 10000;
        Event earliestEvent = new Event(null, null, null, null, null, null, null, null, null);

        //loop through events
        for (Map.Entry<String, Event> event : data.events.entrySet()) {
            Event currentEvent = event.getValue();

            // if this event belongs to the spouse, check if it is the earliest
            // if it is then assign it as the earliestEvent
            if (currentEvent.getPersonID().equals(person.getPersonID())) {
                if (currentEvent.getYear() < earliestDate) {
                    earliestDate = currentEvent.getYear();
                    earliestEvent = currentEvent;
                }
            }
        }

        return earliestEvent;
    }

    Object[] getSortedEvents(Person person) {
        SortedMap<Integer, Event> sortedEvents = new TreeMap<>();

        //loop through events
        for (Map.Entry<String, Event> event : data.events.entrySet()) {
            Event currentEvent = event.getValue();

            // if this event belongs to the person, add it to the sorted map
            if (currentEvent.getPersonID().equals(person.getPersonID())) {
                Integer offset = currentEvent.getYear() * 10;
                Integer counter = 1;

                if (sortedEvents.containsKey(offset)) {
                    sortedEvents.put(offset + counter, currentEvent);
                    counter++;
                } else {
                    sortedEvents.put(currentEvent.getYear() * 10, currentEvent);
                }

            }
        }

        return sortedEvents.entrySet().toArray();
    }

    Polyline drawLine(Event startEvent, Event endEvent, int color, Float width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        PolylineOptions options = new PolylineOptions().
                add(startPoint).
                add(endPoint).
                width(width).
                color(color);

        Polyline line = map.addPolyline(options);

        return line;
    }

    Polyline drawSpouseLine(boolean shouldDraw, Event selectedEvent) {

        if (shouldDraw) {

            try {
                Person selectedPerson = data.people.get(selectedEvent.getPersonID());
                Person spouse = data.people.get(selectedPerson.getSpouseID());

                assert spouse != null;

                Event earliestSpouseEvent = getEarliestEvent(spouse);

                return drawLine(selectedEvent, earliestSpouseEvent, 0xff0000ff, 5.0f);
            } catch (AssertionError e) {
                System.out.println(e.getMessage());
            }
        }

        return null;
    }

    ArrayList<Polyline> drawLifeStoryLines(boolean shouldDraw, Event selectedEvent) {


        if (shouldDraw) {

            try {
                ArrayList<Polyline> lines = new ArrayList<>();

                Person selectedPerson = data.people.get(selectedEvent.getPersonID());

                Object[] sortedEvents = getSortedEvents(selectedPerson);

                for (int i = 0; i < sortedEvents.length - 1; i++) {
                    Map.Entry<Integer, Event> firstEntry = (Map.Entry<Integer, Event>) sortedEvents[i];
                    Map.Entry<Integer, Event> secondEntry = (Map.Entry<Integer, Event>) sortedEvents[i + 1];

                    float width = (i * -5) + 25.0f;

                    Polyline newLine = drawLine(firstEntry.getValue(), secondEntry.getValue(), 0xff00ff00, width);

                    lines.add(newLine);
                }

                return lines;

            } catch (AssertionError e) {
                System.out.println(e.getMessage());
            }
        }

        return null;
    }

    ArrayList<Polyline> drawFamilyTreeLinesHelper(Person child, Float width) {
        try {
            ArrayList<Polyline> lines = new ArrayList<>();
            Person father, mother;
            father = null;
            mother = null;

            if (child.getFatherID() != null) {
                father = data.people.get(child.getFatherID());
            }

            if (child.getMotherID() != null) {
                mother = data.people.get(child.getMotherID());
            }

            if (father != null) {
                Polyline fatherLine = drawLine(getEarliestEvent(child), getEarliestEvent(father), 0xffff0000, width);
                lines.add(fatherLine);
                lines.addAll(drawFamilyTreeLinesHelper(father, width - 6));
            }

            if (mother != null) {
                Polyline motherLine = drawLine(getEarliestEvent(child), getEarliestEvent(mother), 0xffff0000, width);
                lines.add(motherLine);
                lines.addAll(drawFamilyTreeLinesHelper(mother, width - 6));
            }


            return lines;

        } catch (AssertionError e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    ArrayList<Polyline> drawFamilyTreeLines(boolean shouldDraw, Event selectedEvent) {

        if (shouldDraw) {

            try {
                ArrayList<Polyline> lines = new ArrayList<>();
                Float startWidth = 30f;

                Person selectedPerson = data.people.get(selectedEvent.getPersonID());

                if (selectedPerson.getFatherID() != null) {
                    Person father = data.people.get(selectedPerson.getFatherID());
                    Polyline fatherLine = drawLine(selectedEvent, getEarliestEvent(father), 0xffff0000, startWidth);
                    lines.add(fatherLine);
                    lines.addAll(drawFamilyTreeLinesHelper(father, startWidth - 6));
                }

                if (selectedPerson.getMotherID() != null) {
                    Person mother = data.people.get(selectedPerson.getMotherID());
                    Polyline motherLine = drawLine(selectedEvent, getEarliestEvent(mother), 0xffff0000, startWidth);
                    lines.add(motherLine);
                    lines.addAll(drawFamilyTreeLinesHelper(mother, startWidth - 6));
                }

                return lines;

            } catch (AssertionError e) {
                System.out.println(e.getMessage());
            }
        }

        return null;
    }

    public void drawEvents(boolean redraw) {

        if (finishedDrawing) {
            map.clear();

            if (eventId != null && !redraw) {
                Event event = data.events.get(eventId);

                LatLng pos = new LatLng(event.getLatitude(), event.getLongitude());

                assert event != null;

                Person person = data.people.get(event.getPersonID());
                selectedPersonID = event.getPersonID();

                assert person != null;

                String name = person.getFirstName() + " " + person.getLastName() + " (" + person.getGender() + ")";
                String description = event.getEventType().toUpperCase() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";

                personNameText.setText(name);
                eventDescriptionText.setText(description);

                map.animateCamera(CameraUpdateFactory.newLatLng(pos));
            }

            for (Map.Entry<String, Event> entry : data.events.entrySet()) {

                Event event = entry.getValue();

                LatLng pos = new LatLng(event.getLatitude(), event.getLongitude());
                if (event.getEventType().equalsIgnoreCase("death")) {
                    Marker marker = map.addMarker(new MarkerOptions().
                            position(pos).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.death_logo)));

                    assert marker != null;
                    marker.setTag(entry.getValue());
                } else if (event.getEventType().equalsIgnoreCase("birth")) {
                    Marker marker = map.addMarker(new MarkerOptions().
                            position(pos).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.birth_logo)));

                    assert marker != null;
                    marker.setTag(entry.getValue());
                } else if (event.getEventType().equalsIgnoreCase("marriage")) {
                    Marker marker = map.addMarker(new MarkerOptions().
                            position(pos).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.marriage_logo)));

                    assert marker != null;
                    marker.setTag(entry.getValue());
                } else {
                    Marker marker = map.addMarker(new MarkerOptions().
                            position(pos).
                            icon(BitmapDescriptorFactory.defaultMarker(getIconColor(event))));

                    assert marker != null;
                    marker.setTag(entry.getValue());
                }


            }

            //If spouseLine exists we need to clear it to set the new one
            if (spouseLine != null) {
                spouseLine.remove();
            }

            //clear life story lines
            if (lifeStoryLines != null) {
                for (Polyline line : lifeStoryLines) {
                    line.remove();
                }
            }

            //clear family tree lines
            if (familyTreeLines != null) {
                for (Polyline line : familyTreeLines) {
                    line.remove();
                }
            }

            //boolean here controls if the spouseLine is drawn or not
            if (selectedEvent != null) {
                spouseLine = drawSpouseLine(data.spouseLines, selectedEvent);
                lifeStoryLines = drawLifeStoryLines(data.lifeStoryLines, selectedEvent);
                familyTreeLines = drawFamilyTreeLines(data.familyTreeLines, selectedEvent);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // redraw all events
        drawEvents(true);
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.

        //reset the map
        finishedDrawing = true;

        map.clear();

        //clear the spouse line
        if (spouseLine != null) {
            spouseLine.remove();
        }

        //clear life story lines
        if (lifeStoryLines != null) {
            for (Polyline line : lifeStoryLines) {
                line.remove();
            }
        }

        //clear family tree lines
        if (lifeStoryLines != null) {
            for (Polyline line : lifeStoryLines) {
                line.remove();
            }
        }


        // Make markers for all the events in the data cache
        drawEvents(false);

        map.setOnMarkerClickListener(marker -> {

            Event event = (Event) marker.getTag();

            assert event != null;

            this.selectedEvent = event;

            Person person = data.people.get(event.getPersonID());
            selectedPersonID = event.getPersonID();

            assert person != null;

            String name = person.getFirstName() + " " + person.getLastName() + " (" + person.getGender() + ")";
            String description = event.getEventType().toUpperCase() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";

            personNameText.setText(name);
            eventDescriptionText.setText(description);
            if (person.getGender().equals("m")) {
                personNameText.setCompoundDrawables(maleIcon, null, null, null);
            } else {
                personNameText.setCompoundDrawables(femaleIcon, null, null, null);
            }

            //This resets all the markers, so no reference to selected marker below this line
            drawEvents(true);


            return false;
        });

        personInfoSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPersonID != null) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra(PersonActivity.PERSON_ID_KEY, selectedPersonID);
                    startActivity(intent);

                }
            }
        });

//        map.animateCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (eventId == null) inflater.inflate(R.menu.mapmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.app_bar_settings:
                Intent intent2 = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}