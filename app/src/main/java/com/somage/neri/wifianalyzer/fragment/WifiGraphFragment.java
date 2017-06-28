package com.somage.neri.wifianalyzer.fragment;

/*
 * Copyright (C) Copyright 2016 Philipp Jahoda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.somage.neri.wifianalyzer.R;
import com.somage.neri.wifianalyzer.model.WifiModel;
import com.somage.neri.wifianalyzer.utilities.WifiScanUtils;

import java.util.ArrayList;

public class WifiGraphFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Runnable runnable = null;

    private String timeMilliseconds = "";

    private String mediumSignal = "";

    private String maxSignal = "";

    private boolean activeLineMediumSignal = true;

    private boolean activeLineMaxSignal = true;

    private Handler handler;

    private BarChart wifiBarChart;

    private WifiScanUtils wifiScanUtils;

    ArrayList<WifiModel> wifis;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent =  inflater.inflate(R.layout.wifi_graph_fragment_main, container, false);
        wifiBarChart = (BarChart) parent.findViewById(R.id.barChart);

        setupWifiScanUtils();
        setupSharedPreferences();
        setupBarChart();
        runWifiScanGraph();

        return parent;
    }

    private void setupWifiScanUtils() {
        wifiScanUtils = new WifiScanUtils(getContext());
        wifiScanUtils.setupWifiScan();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        timeMilliseconds = sharedPreferences.getString(getResources().getString(R.string.key_wifi_time_interval),
                getResources().getString(R.string.time_interval_default));

        mediumSignal = sharedPreferences.getString(getResources().getString(R.string.key_wifi_signal_medium),
                getResources().getString(R.string.signal_medium_default));

        maxSignal = sharedPreferences.getString(getResources().getString(R.string.key_wifi_signal_max),
                getResources().getString(R.string.signal_max_default));

        activeLineMediumSignal = sharedPreferences.getBoolean(getResources().getString(R.string.key_graph_active_line_medium),
                getResources().getBoolean(R.bool.line_medium_default));

        activeLineMaxSignal = sharedPreferences.getBoolean(getResources().getString(R.string.key_graph_active_line_max),
                getResources().getBoolean(R.bool.line_max_default));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        runWifiScanGraph();

    }

    @Override
    public void onStop() {
        super.onStop();
        if(wifiScanUtils != null) {
            killWifiThread();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    public void killWifiThread() {
        if(runnable!=null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void runWifiScanGraph() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                setDataGraph();
                handler.postDelayed(runnable, Integer.parseInt(timeMilliseconds));
            }
        };

        runnable.run();
    }

    private void setDataGraph() {
        wifis = wifiScanUtils.getWifiData();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        int size = wifis.size();
        String[] ssids = new String[size + 1];
        if(size > 0) {

            for (int i = 0; i < size; i++) {
                yVals.add(new BarEntry(i + 1, wifis.get(i).getQuality()));
                ssids[i + 1] = wifis.get(i).getSsid();
            }

            BarDataSet set1 = new BarDataSet(yVals, "WiFi");
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);

            data.setValueTextSize(10f);
            data.setBarWidth(0.4f);
            data.notifyDataChanged();

            wifiBarChart.getXAxis().setValueFormatter(
                    new IndexAxisValueFormatter(ssids));

            wifiBarChart.notifyDataSetChanged();
            wifiBarChart.setData(data);
            wifiBarChart.invalidate();

            wifiScanUtils.clearListSignalLevel();
        }
    }

    private void setupBarChart() {
        wifiBarChart.getAxisRight().setEnabled(false);
        wifiBarChart.getDescription().setEnabled(false);
        wifiBarChart.setMaxVisibleValueCount(3);

        YAxis left = wifiBarChart.getAxisLeft();
        left.setSpaceTop(25f);
        left.setSpaceBottom(25f);
        left.setTextSize(13f);
        left.setLabelCount(5);
        left.setGranularity(1f);

        XAxis xAxis = wifiBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8f);
        xAxis.setLabelCount(5);
        xAxis.setGranularity(1f);

        LimitLine ll = new LimitLine(Float.parseFloat(maxSignal),
                getResources().getString(R.string.title_graph_line_signal_max));
        LimitLine ll1 = new LimitLine(Float.parseFloat(mediumSignal),
                getResources().getString(R.string.title_graph_line_signal_medium));

        setupLimitLineGraph(left, ll, ll1);
        setupLegendGraph();


    }

    private void setupLimitLineGraph(YAxis left, LimitLine ll, LimitLine ll1) {
        ll.setLineColor(Color.RED);
        ll.setLineWidth(0.5f);
        ll.setTextColor(Color.GRAY);
        ll.setTextSize(12f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll.setEnabled(activeLineMaxSignal);
        left.addLimitLine(ll);

        ll1.setLineColor(Color.GRAY);
        ll1.setLineWidth(1f);
        ll1.setTextColor(Color.GRAY);
        ll1.setTextSize(12f);
        ll1.setEnabled(activeLineMediumSignal);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        left.addLimitLine(ll1);
    }

    private void setupLegendGraph() {
        Legend l = wifiBarChart.getLegend();
        l.setFormSize(10f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextSize(12f);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_wifi_signal_medium))) {
            mediumSignal = sharedPreferences.getString(key,
                    getResources().getString(R.string.signal_medium_default));
        } else if (key.equals(getString(R.string.key_wifi_time_interval))) {
            timeMilliseconds = sharedPreferences.getString(key,
                    getResources().getString(R.string.time_interval_default));
        } else if (key.equals(getString(R.string.key_wifi_signal_max))) {
            maxSignal = sharedPreferences.getString(key,
                    getResources().getString(R.string.signal_max_default));
        } else if (key.equals(getString(R.string.key_graph_active_line_medium))) {
            activeLineMediumSignal = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.line_medium_default));
        } else if (key.equals(getString(R.string.key_graph_active_line_max))) {
            activeLineMaxSignal = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.line_max_default));
        }
    }
}
