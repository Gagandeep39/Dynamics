package com.example.test.nuvoco3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class ChangelogActivity extends AppCompatActivity {
    TextView mChangeLog;
    String mChangelogString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelog);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChangeLog = findViewById(R.id.textView);
        mChangelogString = "Version 0.82\n\n";
        mChangelogString += "Added a Changelog Tab in Navigation Drawer...its better than writing all changes in Github Before which i Actually forget whats happening\n";
        mChangelogString += "Date in General Market Info is Current Date and Cannot be Changed\n";
        mChangelogString += "Complaint is now displayed Department wise\n";
        mChangelogString += "Create JCP can no longer have date before current date\n";
        mChangelogString += "Version 0.85\nFixed IP Address\nRenamed JCP calender to 'Visit Overview\nRenamed JCP Viit to'Visit Overview'\nCalender View now Shows data for current data on opening activity\nVisit Overview now shows toda's visit on opening activity";
        mChangelogString += "Version 0.86\nRemoved specific domain requirement";
        mChangelogString += "Version 0.87\nFixed the 'co.in' requiremnet";
        mChangeLog.setText(mChangelogString);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
