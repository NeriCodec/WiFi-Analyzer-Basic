package com.somage.neri.wifianalyzer.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.somage.neri.wifianalyzer.R;
import com.somage.neri.wifianalyzer.adapter.WifiAdapter;
import com.somage.neri.wifianalyzer.model.WifiModel;
import com.somage.neri.wifianalyzer.utilities.WifiScanUtils;

import java.util.ArrayList;


public class WifiFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    private RecyclerView wifiRecyclerView;

    private Runnable runnable = null;

    private Handler handler;

    private String timeMilliseconds = "";

    private WifiScanUtils wifiScan;

    RecyclerView.Adapter adapter;

    ArrayList<WifiModel> wifis = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.wifi_fragment_main, container, false);
        wifiRecyclerView = (RecyclerView) parent.findViewById(R.id.rv_wifis);

        wifiScan = new WifiScanUtils(getContext());
        wifiScan.setupWifiScan();

        setupSharedPreferences();
        setupRecyclerView();
        runWifiScan();

        return parent;
    }


    private void setupSharedPreferences() {
        Context context = getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        timeMilliseconds = sharedPreferences.getString(getResources().getString(R.string.key_wifi_time_interval),
                getResources().getString(R.string.time_interval_default));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        runWifiScan();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Context context = getContext();
        PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(wifiScan != null) {
            killWifiThread();
            wifiScan.clearListSignalLevel();
        }
    }


    private void setupRecyclerView() {
        Context context = getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        wifiRecyclerView.setLayoutManager(layoutManager);
        wifiRecyclerView.setHasFixedSize(true);


        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(wifiRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        wifiRecyclerView.addItemDecoration(mDividerItemDecoration);


        adapter = new WifiAdapter(wifis);
        wifiRecyclerView.setAdapter(adapter);

    }

    private void runWifiScan() {
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                executeWifiScan();
                handler.postDelayed(runnable, Integer.parseInt(timeMilliseconds));
            }
        };

        runnable.run();
    }

    public void executeWifiScan() {
        wifis = wifiScan.getWifiData();
        adapter = new WifiAdapter(wifis);
        wifiRecyclerView.setAdapter(adapter);
    }

    public void killWifiThread() {
        if(runnable!=null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getResources().getString(R.string.key_wifi_time_interval))) {
            timeMilliseconds = sharedPreferences.getString(key,
                    getResources().getString(R.string.time_interval_default));
        }
    }
}
