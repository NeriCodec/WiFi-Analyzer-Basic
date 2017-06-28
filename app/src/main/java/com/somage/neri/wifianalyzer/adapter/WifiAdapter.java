package com.somage.neri.wifianalyzer.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.somage.neri.wifianalyzer.R;
import com.somage.neri.wifianalyzer.model.WifiModel;
import com.somage.neri.wifianalyzer.utilities.WifiScanUtils;

import java.util.ArrayList;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder> {

    private ArrayList<WifiModel> wifis;

    public WifiAdapter(ArrayList<WifiModel> wifis) {
        this.wifis = wifis;
    }

    private WifiScanUtils wifiScanUtils;

    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.wifi_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutIdForListItem, parent, false);

        wifiScanUtils = new WifiScanUtils(context);
        wifiScanUtils.setupWifiScan();

        return new WifiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WifiViewHolder holder, int position) {

        String bssidCurrent = wifiScanUtils.getBSSIDCurrentWifi();

        if(bssidCurrent != null) {
            if (bssidCurrent.equals(wifis.get(position).getBssid())) {
                holder.showIpTextView.setVisibility(View.VISIBLE);
                holder.showConnectedTextView.setVisibility(View.VISIBLE);
                holder.showSsidTextView.setText(wifis.get(position).getSsid());
                holder.showIpTextView.setText(wifiScanUtils.getIpCurrentWifi());
            } else {
                holder.showIpTextView.setVisibility(View.GONE);
                holder.showConnectedTextView.setVisibility(View.GONE);
                holder.showSsidTextView.setText(wifis.get(position).getSsid());
            }
        }

        holder.showMac.setText(wifis.get(position).getBssid().toUpperCase());
        holder.showDbm.setText(String.valueOf(wifis.get(position).getDbm()));
        holder.showFrequency.setText(String.valueOf(wifis.get(position).getFrequency()));
        holder.showPorcentageTextView.setText(String.valueOf(wifis.get(position).getQuality()));
    }

    @Override
    public int getItemCount() {
        return wifis.size();
    }

    class WifiViewHolder extends RecyclerView.ViewHolder {

        private TextView showSsidTextView;

        private TextView showMac;

        private TextView showDbm;

        private TextView showFrequency;

        private TextView showPorcentageTextView;

        private TextView showConnectedTextView;

        private TextView showIpTextView;

        public WifiViewHolder(View itemView) {
            super(itemView);

            showSsidTextView = (TextView) itemView.findViewById(R.id.tv_ssid);
            showMac = (TextView) itemView.findViewById(R.id.tv_mac);
            showDbm = (TextView) itemView.findViewById(R.id.tv_dbm);
            showFrequency = (TextView) itemView.findViewById(R.id.tv_frequency);
            showPorcentageTextView = (TextView) itemView.findViewById(R.id.tv_porcentage);
            showConnectedTextView = (TextView) itemView.findViewById(R.id.tv_connected);
            showIpTextView = (TextView) itemView.findViewById(R.id.tv_ip);
        }
    }
}
