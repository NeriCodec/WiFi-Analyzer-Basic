package com.somage.neri.wifianalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.somage.neri.wifianalyzer.fragment.WifiFragment;
import com.somage.neri.wifianalyzer.fragment.WifiGraphFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    private ActionBar actionBar;

    private Menu menu;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_graph:
                    visibleButtonSettingsMenu(false);
                    setupWifiGraphFragment();
                    return true;
                case R.id.navigation_wifi:
                    visibleButtonSettingsMenu(true);
                    setupWifiFragment();
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        actionBar = this.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getString(R.string.action_bar_title_graph_wifi));
        }
        fragmentManager = getSupportFragmentManager();

        setupWifiGraphFragment();

    }

    private void setupWifiFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WifiFragment wifiFragment = new WifiFragment();
        actionBar.setTitle(getString(R.string.action_bar_title_list_wifi));
        fragmentTransaction.replace(R.id.fl_content, wifiFragment).commit();
    }

    private void setupWifiGraphFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WifiGraphFragment wifiGraphFragment = new WifiGraphFragment();
        actionBar.setTitle(getString(R.string.action_bar_title_graph_wifi));
        fragmentTransaction.replace(R.id.fl_content, wifiGraphFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_wifi, menu);
        visibleButtonSettingsMenu(false);
        return true;
    }

    private void visibleButtonSettingsMenu(boolean status) {
        menu.findItem(R.id.settings_wifi).setVisible(status);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_wifi) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
