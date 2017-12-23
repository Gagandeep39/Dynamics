package com.example.test.nuvoco3;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.test.nuvoco3.customerdata.CustomerContract.CustomerEntry;
import com.example.test.nuvoco3.customerdata.CustomerDbHelper;
import com.example.test.nuvoco3.signup.LoginActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.test.nuvoco3.signup.LoginActivity.LOGIN_KEY;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "NUVOCO";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Set up the ViewPager with the sections adapter.
        mViewPager =  findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout =  findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            insertDummyData();
            return true;
        }
        switch (id) {
            case R.id.action_settings:
                insertDummyData();
                break;
            case R.id.action_logout:
                SharedPreferences.Editor editor = getSharedPreferences("LoginCode", MODE_PRIVATE).edit();
                editor.putInt(LOGIN_KEY, 0);
                editor.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            default:
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData() {
        //Inserting data Customer Master Table
        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_CUSTOMER_NAME, "Toto");
        values.put(CustomerEntry.COLUMN_CUSTOMER_TYPE, "Actual");
        values.put(CustomerEntry.COLUMN_CUSTOMER_CATEGORY, "Dealer");
        values.put(CustomerEntry.COLUMN_CUSTOMER_ADDRESS, "Nahar Amrit Shakti, Powai, Mumbai");
        values.put(CustomerEntry.COLUMN_CUSTOMER_AREA, "Powai");
        values.put(CustomerEntry.COLUMN_CUSTOMER_DISTRICT, "Mumbai");
        values.put(CustomerEntry.COLUMN_CUSTOMER_STATE, "Maharashtra");
        values.put(CustomerEntry.COLUMN_CUSTOMER_PHONE_NO, 1234567890);
        values.put(CustomerEntry.COLUMN_CUSTOMER_EMAIL_ID, "test.1234@gmail.com");
        values.put(CustomerEntry.COLUMN_CUSTOMER_STATUS, 1);
        values.put(CustomerEntry.COLUMN_CUSTOMER_CREATED_ON, getDateTime());
        values.put(CustomerEntry.COLUMN_CUSTOMER_CREATED_BY, "Pokemno");
        values.put(CustomerEntry.COLUMN_CUSTOMER_UPDATED_ON, getDateTime());
        values.put(CustomerEntry.COLUMN_CUSTOMER_UPDATED_BY, "Pkemon");
        CustomerDbHelper helper = new CustomerDbHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.insert(CustomerEntry.TABLE_NAME, null, values);
        Log.i(TAG, "insertNewCustomer: Successfully Inserted Customer");

        //Inserting Dummy Data in Customer Contact Table
        ContentValues contentValues = new ContentValues();
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_NAME, "Dodo");
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_CONTACT_NAME, "Testing");
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_CONTACT_PHONE_NO, 987654321);
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_CONTACT_EMAIL_ID, "testContact@gmail.com");
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_CONTACT_DOB, getDateTime());
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_CONTACT_DOA, getDateTime());
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_CREATED_BY, "Jojo");
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_CREATED_ON, getDateTime());
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_UPDATED_BY, "Jojo");
        contentValues.put(CustomerEntry.COLUMN_CUSTOMER_UPDATED_ON, getDateTime());
        CustomerDbHelper helperContact = new CustomerDbHelper(this);
        helperContact.insertData(CustomerEntry.TABLE_NAME_CONTACT, contentValues);
        SQLiteDatabase databaseContact = helper.getWritableDatabase();
        databaseContact.insert(CustomerEntry.TABLE_NAME_CONTACT, null, contentValues);

    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.iconSetting:
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragmentlead_layout, container, false);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new FragmentLead();
                case 1:
                    return new FragmentCustomer();
                case 2:
                    return new FragmentMarket();
                case 3:
                    return new FragmentVisit();
                    default:
                        return new FragmentLead();


            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }


}
