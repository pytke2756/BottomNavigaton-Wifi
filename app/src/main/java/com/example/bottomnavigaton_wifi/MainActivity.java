package com.example.bottomnavigaton_wifi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private TextView txtInfo;
    private BottomNavigationView bottomNavigationView;
    private WifiInfo wifiInfo;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.wifi_on:
                        //Android 10-től (API 29-től) nem lehet ki be kapcsolni a wifit
                        //Ezért meg kell néznünk az Andorid verzióját
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            txtInfo.setText("Nincs jogosultságod a wifi állapot módosítására");
                            //Megnyitjuk a panelt
                            Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                            //csak wifi panel
                            // ACTION_INTERNET_CONNECTIVITY itt mobilnetet is bekapcsolhatsz
                            startActivityForResult(panelIntent, 0);
                        }else{
                            wifiManager.setWifiEnabled(true);
                            txtInfo.setText("Wifi bekapcsolva");
                        }


                        break;
                    case R.id.wifi_info:
                        ConnectivityManager connectivityManager =
                                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (networkInfo.isConnected()){
                            //andorid.text.format.Formatter!!!!!! nem a java.utils
                            int ip_number = wifiInfo.getIpAddress();
                            String ipConverted = Formatter.formatIpAddress(ip_number);
                            txtInfo.setText("IP cím: " + ipConverted);
                        }else{
                            txtInfo.setText("Nem csatlakoztál wifihez");
                        }
                        break;
                    case R.id.wifi_off:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            txtInfo.setText("Nincs jogosultságod a wifi állapot módosítására");
                            Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                            startActivityForResult(panelIntent, 0);
                        }else{
                            wifiManager.setWifiEnabled(false);
                            txtInfo.setText("Wifi kikapcsolva");
                        }
                        break;
                }

                return true;
            }
        });

    }

    private void init(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        txtInfo = findViewById(R.id.txt_info);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //ACCES_WIFI_STATE
        wifiInfo = wifiManager.getConnectionInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED ||
                    wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING){
                txtInfo.setText("Wifi bekapcsolva");
            } else if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED ||
                    wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING){
                txtInfo.setText("Wifi kikapcsolva");
            }
        }
    }
   
}