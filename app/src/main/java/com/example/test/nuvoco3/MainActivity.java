package com.example.test.nuvoco3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.nuvoco3.fragments.FragmentCustomer;
import com.example.test.nuvoco3.fragments.FragmentLead;
import com.example.test.nuvoco3.fragments.FragmentMarket;
import com.example.test.nuvoco3.fragments.FragmentVisit;
import com.example.test.nuvoco3.helpers.ChangelogActivity;
import com.example.test.nuvoco3.helpers.RecordManagementActivity;
import com.example.test.nuvoco3.helpers.UserInfoHelper;
import com.example.test.nuvoco3.signup.LoginActivity;
import com.example.test.nuvoco3.signup.ObjectSerializer;
import com.example.test.nuvoco3.signup.UserAccountActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentMarket.OnFragmentInteractionListener,
        FragmentCustomer.OnFragmentInteractionListener,
        FragmentVisit.OnFragmentInteractionListener,
        FragmentLead.OnFragmentInteractionListener{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    android.support.design.widget.TabLayout tabLayout;


    public static final String TAG = "MAIN ACTIVITY";
    String mName, mEmail;
    TextView mTextViewName, mTextViewEmail;
    String mUserDepartment;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    //Build info
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    TextView mTextViewBuildInfo;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewBuildInfo = findViewById(R.id.textViewBuildInfo);
        mTextViewBuildInfo.append("\nVersion " + versionName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabs);

        if (Build.VERSION.SDK_INT >= 21)
            tabLayout.setBackground(ContextCompat.getDrawable(this, R.xml.tab_selector_color));


        String htmlString = "<p><font color= #263238><Strong>Sales<Strong></font> <strong><font color= #18945A>DYNAMICS</font></strong></p>";
        String htmlString2 = "<p><font color= #263238><Strong>SALES<Strong></span> <strong><font color= #18945A;\">Dynamics</span></strong></p>";
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 24)
            getSupportActionBar().setTitle(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT));
        else
            getSupportActionBar().setTitle(Html.fromHtml(htmlString));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        getUserId(header);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    //Creates Option Menu at Top Left (3 Dots)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        if (!mUserDepartment.equals(getString(R.string.technical_verify))) {

//            menu.getItem(1).setVisible(false);
//        }
        return true;
    }

    //Allows Selection of Items in Options Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.action_product:
                startActivity(new Intent(getApplicationContext(), RecordManagementActivity.class));
                break;
            default:
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    //Allows Selection OF Items in Navigation Menu
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.iconUsers:
                startActivity(new Intent(this, UserAccountActivity.class));
                break;
            case R.id.startLead:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.startCustomer:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.startMarket:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.startVisit:
                mViewPager.setCurrentItem(3);
                break;
            case R.id.iconChangelog:
                startActivity(new Intent(this, ChangelogActivity.class));
                break;
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

    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    private void getUserId(View header) {
        ArrayList<String> newArralist = new ArrayList<>();
        // Creates a shared preferences variable to retrieve the logeed in users IDs and store it in Updated By Section
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.test.nuvoco3", Context.MODE_PRIVATE);

        newArralist = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("CustomerData", ObjectSerializer.serialize(new ArrayList<String>())));

        mTextViewName = header.findViewById(R.id.textViewName);
        mTextViewEmail = header.findViewById(R.id.textViewEmail);

        mName = newArralist.get(7);
        mEmail = newArralist.get(5);

        mTextViewName.setText(newArralist.get(7));
        mTextViewEmail.setText(newArralist.get(5));
        mUserDepartment = newArralist.get(4);
//        if (!mUserDepartment.equals(getString(R.string.technical_verify))) {
//            invalidateOptionsMenu();
//        }

    }

    //Starts a Fragment Activity on Selecting One of The Fragments
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        String data = new UserInfoHelper(MainActivity.this).getUserDepartment();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
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

        //Keeps Count of total Number of Fragments available on Swipe
        @Override
        public int getCount() {
            return 4;
        }
    }


}
