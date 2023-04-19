package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import model.Event;
import model.Person;

public class EventActivity extends AppCompatActivity {

    public static final String EVENT_ID_KEY = "personID";

    Event event;

    DataCache data;
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        data = DataCache.getInstance();

        Intent intent = getIntent();

        event = data.events.get(intent.getStringExtra(EVENT_ID_KEY));
        person = data.people.get(event.getPersonID());

        FragmentManager fm = getSupportFragmentManager();
        MapFragment fragment = (MapFragment) fm.findFragmentById(R.id.eventActivityFragmentFrameLayout);
        if(fragment == null) {
            fragment = createMapFragment(event.getEventID());

            fm.beginTransaction()
                    .add(R.id.eventActivityFragmentFrameLayout, fragment)
                    .commit();
        }

    }

    private MapFragment createMapFragment(String eventId) {
        MapFragment fragment = new MapFragment(eventId);
//        fragment.registerListener(this);
        return fragment;
    }

    // back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        return true;
    }

}