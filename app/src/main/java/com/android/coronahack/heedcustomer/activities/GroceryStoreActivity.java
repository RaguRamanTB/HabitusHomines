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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.EnterMedAdapter;
import com.android.coronahack.heedcustomer.helpers.EnterMeds;
import com.android.coronahack.heedcustomer.helpers.GlobalData;
import com.android.coronahack.heedcustomer.helpers.UploadRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GroceryStoreActivity extends AppCompatActivity {

    ImageView locateG, addG, removeG;
    public static EditText nearestG, gName, gQuantity, gPhNum;
    Button submitG;
    RecyclerView gRecycler;
    RecyclerView.Adapter gAdapter;
    List<EnterMeds> enterGList;
    DatabaseReference referencePrescription;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_store);

        Toolbar toolbar = findViewById(R.id.buyGrocery);
        toolbar.setTitle("Buy Grocery");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handler  = new Handler();
        startRepeatingTask();

        locateG = findViewById(R.id.gLocationButton);
        addG = findViewById(R.id.gPlus);
        removeG = findViewById(R.id.gMinus);
        nearestG = findViewById(R.id.nearestG);
        gName = findViewById(R.id.gName);
        gQuantity = findViewById(R.id.gQuantity);
        gPhNum = findViewById(R.id.gPhNum);
        submitG = findViewById(R.id.submitGrocery);
        gRecycler = findViewById(R.id.gRecycler);
        referencePrescription = FirebaseDatabase.getInstance().getReference().child("groceries");

        enterGList = new ArrayList<>();
        gRecycler.setLayoutManager(new LinearLayoutManager(this));

        addG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroceries();
            }
        });

        removeG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGroceries();
            }
        });

        locateG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroceryStoreActivity.this, MapsActivity.class);
                intent.putExtra("type", "grocery");
                startActivity(intent);
            }
        });

        submitG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });
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
                    Toast.makeText(GroceryStoreActivity.this, "Please maintain distance from others!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void submitRequest() {
        String gName, gPh;
        gName = nearestG.getText().toString();
        gPh = gPhNum.getText().toString();

        if (gName.length() == 0 || gPh.equals(" ") || gPh.length() == 0) {
            Toast.makeText(GroceryStoreActivity.this, "Selected shop and phone number are mandatory.", Toast.LENGTH_SHORT).show();
        } else {
            if (enterGList.size() == 0) {
                Toast.makeText(GroceryStoreActivity.this, "Your grocery list is empty!", Toast.LENGTH_SHORT).show();
            } else {
                UploadRequest uploadRequest = new UploadRequest(nearestG.getText().toString(), GlobalData.name, gPhNum.getText().toString(), GlobalData.address, enterGList, 0, "9:00 am to 9:30 am");
                String uploadId = referencePrescription.push().getKey();
                referencePrescription.child(uploadId)
                        .setValue(uploadRequest)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(GroceryStoreActivity.this, "Request successful! Please wait for the shop to confirm and allot you a slot.", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }

    private void removeGroceries() {
        if (enterGList.size() > 0) {
            enterGList.remove(enterGList.size() - 1);
            gAdapter = new EnterMedAdapter(this, enterGList);
            gRecycler.setAdapter(gAdapter);
        }
    }

    private void addGroceries() {
        String getGName, getGQuantity;
        getGName = gName.getText().toString();
        getGQuantity = gQuantity.getText().toString();

        if (getGName.equals("") || getGName.length() == 0
                || getGQuantity.equals("") || getGQuantity.length() == 0) {
            Toast.makeText(GroceryStoreActivity.this, "Enter both item name and quantity", Toast.LENGTH_SHORT).show();
        } else {
            enterGList.add(new EnterMeds(getGName, getGQuantity));
            gAdapter = new EnterMedAdapter(this, enterGList);
            gRecycler.setAdapter(gAdapter);
            gName.setText("");
            gQuantity.setText("");
        }
    }
}
