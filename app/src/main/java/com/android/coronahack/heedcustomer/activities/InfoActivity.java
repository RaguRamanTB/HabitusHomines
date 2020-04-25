package com.android.coronahack.heedcustomer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.GridAdapter;
import com.android.coronahack.heedcustomer.helpers.GridItem;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    Handler handler;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    GridView gridView;
    GridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        handler  = new Handler();
        startRepeatingTask();

        Toolbar toolbar = findViewById(R.id.infoToolbar);
        toolbar.setTitle("Learn about COVID-19");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gridView = findViewById(R.id.landing_grid);
        gridAdapter = new GridAdapter(this, getData());
        gridView.setAdapter(gridAdapter);
    }

    private ArrayList<GridItem> getData() {
        ArrayList<GridItem> menuItems = new ArrayList<>();
        GridItem m = new GridItem();
        m.setGridPicture(R.drawable.masks);
        m.setGridText("When and how to use masks");
        menuItems.add(m);

        m = new GridItem();
        m.setGridPicture(R.drawable.myths);
        m.setGridText("Myth-busters");
        menuItems.add(m);

        m = new GridItem();
        m.setGridPicture(R.drawable.workplace);
        m.setGridText("Getting workplace ready");
        menuItems.add(m);

        m = new GridItem();
        m.setGridPicture(R.drawable.advocacy);
        m.setGridText("Advocacy");
        menuItems.add(m);

        m = new GridItem();
        m.setGridPicture(R.drawable.youtube);
        m.setGridText("Watch videos");
        menuItems.add(m);

        return menuItems;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                bluetoothAdapter.startDiscovery();
            } finally {
                handler.postDelayed(runnable, 1000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    private void stopRepeatingTask() {
        handler.removeCallbacks(runnable);
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            Log.d("Unregister", e.toString());
        }
    }

    private void startRepeatingTask() {
        runnable.run();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                Log.d("Bluetooth", name + " => "+ rssi);
                if (rssi > -68) {
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000);
                    Toast.makeText(InfoActivity.this, "Please maintain distance from others!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
