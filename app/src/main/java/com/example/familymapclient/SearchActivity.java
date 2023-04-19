package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;
import java.util.Map;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    Drawable maleIcon, femaleIcon, eventIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.resultsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        DataCache data = DataCache.getInstance();

        maleIcon =  new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).
                colorRes(R.color.male_icon).sizeDp(40);
        femaleIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).
                colorRes(R.color.female_icon).sizeDp(40);
        eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).
                colorRes(R.color.black).sizeDp(40);

        SearchAdapter adapter = new SearchAdapter(data.getPeopleList(" "), data.getEventList(" "));
        recyclerView.setAdapter(adapter);

        SearchView search = findViewById(R.id.search_view);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                SearchAdapter adapter = new SearchAdapter(data.getPeopleList(query), data.getEventList(query));
                recyclerView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text change
                SearchAdapter adapter = new SearchAdapter(data.getPeopleList(newText), data.getEventList(newText));
                recyclerView.setAdapter(adapter);
                return false;
            }
        });
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

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

        private final List<Event> events;
        private final List<Person> people;

        SearchAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @Override
        public int getItemCount() {
            return events.size() + people.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title, owner;

        private final int viewType;
        private Event event;
        private Person person;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                title = itemView.findViewById(R.id.personItemText);
                owner = null;
            } else {
                title = itemView.findViewById(R.id.eventItemText);
                owner = itemView.findViewById(R.id.eventItemPersonText);
            }
        }

        private void bind(Person person) {
            this.person = person;
            String titleString = person.getFirstName() + " " + person.getLastName() + " (" + person.getGender() + ")";
            title.setText(titleString);

            if(person.getGender().equals("m")){
                title.setCompoundDrawables(maleIcon,null,null,null);
            }
            else {
                title.setCompoundDrawables(femaleIcon,null,null,null);
            }
        }

        private void bind(Event event) {
            this.event = event;

            String description = event.getEventType().toUpperCase() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            title.setText(description);
            title.setCompoundDrawables(eventIcon,null,null,null);

            DataCache data = DataCache.getInstance();
            Person ownerPerson = data.people.get(event.getPersonID());
            String name = ownerPerson.getFirstName() + " " + ownerPerson.getLastName();
            owner.setText(name);




        }

        @Override
        public void onClick(View view) {
            Intent intent;
            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                // This is were we could pass the skiResort to a ski resort detail activity
                intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PersonActivity.PERSON_ID_KEY, person.getPersonID());


            } else {
                // This is were we could pass the hikingTrail to a hiking trail detail activity

                intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(EventActivity.EVENT_ID_KEY, event.getEventID());
            }
            startActivity(intent);
        }
    }
}