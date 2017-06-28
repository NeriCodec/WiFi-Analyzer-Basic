package com.somage.neri.wifianalyzer.model;

public class WifiModel {

    private String ssid;

    private String bssid;

    private int quality;

    private double dbm;

    private double frequency;

    public WifiModel(String ssid, String bssid, int quality, double dbm, double frequency) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.dbm = dbm;
        this.quality = quality;
        this.frequency = frequency;
    }

    public int getQuality() {
        return quality;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public double getDbm() {
        return dbm;
    }

    public double getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "SSID: " + ssid + " BSSID: " + bssid + " dBm: " + dbm + " frequency: " + frequency;
    }
}
