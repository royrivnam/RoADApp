package com.mcnrg.akash.safestreet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WifiDetector {

    private WifiManager mainWifi;
    private WifiScanReceiver receiverWifi;
    private Context context;

    private RandomAccessFile wififile;
    private Thread wifiThread=null;

    private String output;

    public WifiDetector(String path, Context c)
    {
        context=c;
        output=path;
    }

    public void startDetecting()
    {
        wifiThread=new Thread(new Runnable() {
            @Override
            public void run() {
                DetectWifi();
            }
        },"Wifi Detector Thread");
        wifiThread.start();
    }

    public void stopDetecting()
    {
        try {
            wififile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.unregisterReceiver(receiverWifi);
        wifiThread=null;
    }

    private void DetectWifi()
    {
        mainWifi=(WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try{
            wififile=new RandomAccessFile(output,"rw");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        receiverWifi=new WifiScanReceiver();
        context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {

            List wifiList = mainWifi.getScanResults();
            for (int i = 0; i < wifiList.size(); i++) {
                ScanResult scanResult = (ScanResult) wifiList.get(i);
                try {
                    long systemTimeInMilli = (new Date()).getTime();
                    String timestampFormatted = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(systemTimeInMilli));
                    wififile.seek(wififile.length());
                    wififile.write((scanResult.BSSID + "," + scanResult.SSID + "," + scanResult.level + "," + timestampFormatted + "\n").getBytes());
                } catch (Exception e) {

                }
            }
        }
    }

}
