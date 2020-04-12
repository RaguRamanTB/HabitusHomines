package com.android.coronahack.heedcustomer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.text.GetChars;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.GetNotification;
import com.android.coronahack.heedcustomer.helpers.GlobalData;
import com.android.coronahack.heedcustomer.helpers.NotificationAdapter;
import com.android.coronahack.heedcustomer.helpers.UploadRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView notificationRecycler;
    RecyclerView.Adapter mAdapter;
    DatabaseReference reference, referenceGrocery;
    ValueEventListener valueEventListener, valueEventListenerGrocery;
    List<GetNotification> getNotifications;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        handler  = new Handler();
        startRepeatingTask();

        Toolbar toolbar = findViewById(R.id.showNotifications);
        toolbar.setTitle("Your Requests");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notificationRecycler = findViewById(R.id.notificationRecycler);
        getNotifications = new ArrayList<>();
        notificationRecycler.setHasFixedSize(true);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new NotificationAdapter(NotificationActivity.this, getNotifications);
        notificationRecycler.setAdapter(mAdapter);

        reference = FirebaseDatabase.getInstance().getReference().child("prescriptions");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getNotifications.clear();
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    UploadRequest uploadRequest = dss.getValue(UploadRequest.class);
                    assert uploadRequest != null;
                    if (uploadRequest.customerName.equals(GlobalData.name)) {
                        if (uploadRequest.mKey == 0) {
                            getNotifications.add(new GetNotification("MEDICINE", uploadRequest.shopName, "NOT APPROVED", "NA"));
//                            mAdapter = new NotificationAdapter(NotificationActivity.this, getNotifications);
//                            notificationRecycler.setAdapter(mAdapter);
                        } else {
                            getNotifications.add(new GetNotification("MEDICINE", uploadRequest.shopName, "APPROVED", uploadRequest.timeSlot));
//                            mAdapter = new NotificationAdapter(NotificationActivity.this, getNotifications);
//                            notificationRecycler.setAdapter(mAdapter);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        reference.addValueEventListener(valueEventListener);

        referenceGrocery = FirebaseDatabase.getInstance().getReference().child("groceries");
        valueEventListenerGrocery = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    UploadRequest uploadRequest2 = data.getValue(UploadRequest.class);
                    assert uploadRequest2 != null;
                    if (uploadRequest2.customerName.equals(GlobalData.name)) {
                        if (uploadRequest2.mKey == 0) {
                            getNotifications.add(new GetNotification("GROCERY", uploadRequest2.shopName, "NOT APPROVED", "NA"));
//                            mAdapter = new NotificationAdapter(NotificationActivity.this, getNotifications);
//                            notificationRecycler.setAdapter(mAdapter);
                        } else {
                            getNotifications.add(new GetNotification("GROCERY", uploadRequest2.shopName, "APPROVED", uploadRequest2.timeSlot));
//                            mAdapter = new NotificationAdapter(NotificationActivity.this, getNotifications);
//                            notificationRecycler.setAdapter(mAdapter);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        referenceGrocery.addValueEventListener(valueEventListenerGrocery);
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
                    Toast.makeText(NotificationActivity.this, "Please maintain distance from others!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
