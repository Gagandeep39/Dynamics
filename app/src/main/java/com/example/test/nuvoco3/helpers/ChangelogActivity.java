package com.example.test.nuvoco3.helpers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.test.nuvoco3.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ChangelogActivity extends AppCompatActivity {

    TextView mChangeLogTextView;
    String mChangelogString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelog);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChangeLogTextView = findViewById(R.id.textView);
        mChangelogString = readChangelogFile();
        mChangeLogTextView.setText(mChangelogString);
    }

    private String readChangelogFile() {
        String mTextFile = "";
        InputStream mInputStream = null;
        try {
            mInputStream = this.getAssets().open("changelog.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));
            String strLine;
            while ((strLine = reader.readLine()) != null) {
                mTextFile = mTextFile + strLine + "\n";
            }
            reader.close();
            mInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mTextFile;
    }
}
