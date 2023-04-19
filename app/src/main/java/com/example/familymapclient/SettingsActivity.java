package com.example.familymapclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableRow;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    TableRow logoutRow;
    DataCache data;

    Switch lifeStorySwitch, familyTreeSwitch, spouseSwitch, fatherFilterSwitch, motherFilterSwitch, maleFilterSwitch, femaleFilterSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        data = DataCache.getInstance();

        //Switch declarations
         lifeStorySwitch = findViewById(R.id.lifeStorySwitch);
         familyTreeSwitch = findViewById(R.id.familyTreeSwitch);
         spouseSwitch = findViewById(R.id.spouseSwitch);
         fatherFilterSwitch = findViewById(R.id.fatherSideSwitch);
         motherFilterSwitch = findViewById(R.id.motherSideSwitch);
         maleFilterSwitch = findViewById(R.id.maleEventsSwitch);
         femaleFilterSwitch = findViewById(R.id.femaleEventsSwitch);

        // Set state to match data cache
        lifeStorySwitch.setChecked(data.lifeStoryLines);
        familyTreeSwitch.setChecked(data.familyTreeLines);
        spouseSwitch.setChecked(data.spouseLines);
        fatherFilterSwitch.setChecked(data.fatherFilter);
        motherFilterSwitch.setChecked(data.motherFilter);
        maleFilterSwitch.setChecked(data.maleFilter);
        femaleFilterSwitch.setChecked(data.femaleFilter);

         //Switch listeners
        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                data.setLifeStoryLines(isChecked);
            }
        });
        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                data.setFamilyTreeLines(isChecked);
            }
        });
        spouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                data.setSpouseLines(isChecked);
            }
        });
        fatherFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                data.setFatherFilter(isChecked);
                data.setFilters(data.maleFilter, data.femaleFilter, data.fatherFilter, data.motherFilter);
            }
        });
        motherFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                data.setMotherFilter(isChecked);
                data.setFilters(data.maleFilter, data.femaleFilter, data.fatherFilter, data.motherFilter);
            }
        });
        maleFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                data.setMaleFilter(isChecked);
                data.setFilters(data.maleFilter, data.femaleFilter, data.fatherFilter, data.motherFilter);
            }
        });
        femaleFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setFemaleFilter(isChecked);
                data.setFilters(data.maleFilter, data.femaleFilter, data.fatherFilter, data.motherFilter);
            }
        });

        //logout button
        logoutRow = findViewById(R.id.logoutRow);
        logoutRow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                DataCache data = DataCache.getInstance();
                data.clear();

                startActivity(intent);
            }
        });


    }

    // back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        }
        return true;
    }


}