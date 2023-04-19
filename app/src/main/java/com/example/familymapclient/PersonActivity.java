package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    public static final String PERSON_ID_KEY = "personID";
    Person person;

    Drawable maleIcon, femaleIcon, eventIcon;
    DataCache data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        data = DataCache.getInstance();

        maleIcon =  new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                colorRes(R.color.male_icon).sizeDp(40);
        femaleIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                colorRes(R.color.female_icon).sizeDp(40);
        eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).
                colorRes(R.color.black).sizeDp(40);

        Intent intent = getIntent();

        person = data.people.get(intent.getStringExtra(PERSON_ID_KEY));

        TextView firstNameText = findViewById(R.id.personActivityFirstName);
        firstNameText.setText(person.getFirstName());

        TextView lastNameText = findViewById(R.id.personActivityLastName);
        lastNameText.setText(person.getLastName());

        TextView genderText = findViewById(R.id.personActivityGender);
        genderText.setText((person.getGender().equals("f")) ? "Female" : "Male");



        ExpandableListView expandView = findViewById(R.id.eventsExpandableListView);

        List<Person> people = data.getFamilyList(person.getPersonID());
        List<Event> events = data.getPersonEvents(person.getPersonID());


        expandView.setAdapter(new ExpandableListAdapter(people, events));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Event> events;
        private final List<Person> people;

        ExpandableListAdapter(List<Person> people, List<Event> events) {
            this.events = events;
            this.people = people;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.size();
                case FAMILY_GROUP_POSITION:
                    return people.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            // Not used
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // Not used
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.eventsTitle);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.peopleTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(View eventItemView, final int childPosition) {
            TextView eventTitle = eventItemView.findViewById(R.id.eventItemText);
            Event event = events.get(childPosition);
            String description = event.getEventType().toUpperCase() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            eventTitle.setText(description);


            eventTitle.setCompoundDrawables(eventIcon,null,null,null);


            TextView ownerText = eventItemView.findViewById(R.id.eventItemPersonText);

            DataCache data = DataCache.getInstance();
            Person ownerPerson = data.people.get(event.getPersonID());
            String name = ownerPerson.getFirstName() + " " + ownerPerson.getLastName();

            ownerText.setText(name);

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra(EventActivity.EVENT_ID_KEY, event.getEventID());
                    startActivity(intent);
                    }
            });
        }

        private void initializePersonView(View personView, final int childPosition) {
            TextView personNameText = personView.findViewById(R.id.personItemText);
            Person currentListPerson = people.get(childPosition);
            String titleString = currentListPerson.getFirstName() + " " + currentListPerson.getLastName() + " (" + currentListPerson.getGender() + ")";
            personNameText.setText(titleString);

            if(currentListPerson.getGender().equals("m")){
                personNameText.setCompoundDrawables(maleIcon,null,null,null);
            }
            else {
                personNameText.setCompoundDrawables(femaleIcon,null,null,null);
            }

            TextView relationText = personView.findViewById(R.id.personItemExtraText);



            relationText.setText(data.getRelationString(person, currentListPerson));



            personView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra(PersonActivity.PERSON_ID_KEY, currentListPerson.getPersonID());
                    startActivity(intent);
}
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
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