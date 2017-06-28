package com.somage.neri.wifianalyzer.utilities;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.somage.neri.wifianalyzer.model.WifiModel;

import java.util.ArrayList;
import java.util.List;

public class WifiScanUtils {

    private WifiManager wifiManager;

    private ArrayList<WifiModel> wifis;

    private List<ScanResult> wifiScanList;

    private Context context;

    public WifiScanUtils(Context context) {
        this.context = context;
    }

    public void setupWifiScan() {
        try{

            if(wifiManager == null) {
                wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            }

            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<WifiModel> getWifiData() {
        if(wifiManager.startScan()) {
            wifiScanList = wifiManager.getScanResults();
            wifis = new ArrayList<>();
            for (int i = 0; i < wifiScanList.size(); i++) {
                wifis.add(new WifiModel(wifiScanList.get(i).SSID,
                        wifiScanList.get(i).BSSID,
                        getQuality(wifiScanList.get(i).level),
                        wifiScanList.get(i).level,
                        wifiScanList.get(i).frequency
                ));
            }
            return wifis;
        }
        return wifis;
    }

    public String getBSSIDCurrentWifi() {
        return wifiManager.getConnectionInfo().getBSSID();
    }

    public String getIpCurrentWifi() {
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    private int getQuality(int dbm) {
        int quality = 0;

        if(dbm <= -100) {
            quality = 0;
        } else if(dbm >= -50) {
            quality = 100;
        } else {
            quality = 2 * (dbm + 100);
        }

        return quality;
    }

    public void clearListSignalLevel() {
        wifis.clear();
        wifiScanList.clear();
    }

}
