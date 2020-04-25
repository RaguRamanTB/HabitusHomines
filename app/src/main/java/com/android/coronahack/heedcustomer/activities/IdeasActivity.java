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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.GetIdeas;
import com.android.coronahack.heedcustomer.helpers.GetIdeasAdapter;
import com.android.coronahack.heedcustomer.helpers.GlobalData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IdeasActivity extends AppCompatActivity {

    EditText title, content;
    Button submitIdea;
    List<GetIdeas> getIdeasList;
    DatabaseReference referenceIdeas;
    ValueEventListener valueEventListener;
    RecyclerView ideasRecycler;
    RecyclerView.Adapter mAdapter;

    Handler handler;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ideas);

        Toolbar toolbar = findViewById(R.id.submitIdea);
        toolbar.setTitle("Give your idea");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handler = new Handler();
        startRepeatingTask();

        title = findViewById(R.id.titleIdea);
        content = findViewById(R.id.contentIdea);
        submitIdea = findViewById(R.id.submitIdeaButton);
        ideasRecycler = findViewById(R.id.ideasRecycler);

        getIdeasList = new ArrayList<>();
        ideasRecycler.setHasFixedSize(true);
        ideasRecycler.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new GetIdeasAdapter(IdeasActivity.this, getIdeasList);
        ideasRecycler.setAdapter(mAdapter);

        referenceIdeas = FirebaseDatabase.getInstance().getReference().child("ideas");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getIdeasList.clear();
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    GetIdeas getIdeas = dss.getValue(GetIdeas.class);
                    assert getIdeas != null;
                    getIdeasList.add(new GetIdeas(getIdeas.getTitleIdea(), getIdeas.getContentIdea(), getIdeas.getIdeaBy()));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        referenceIdeas.addValueEventListener(valueEventListener);

        submitIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTitle, getContent;
                getTitle = title.getText().toString();
                getContent = content.getText().toString();
                if (getTitle.equals(" ") || getTitle.length() == 0 || getContent.equals(" ") || getContent.length() == 0) {
                    Toast.makeText(IdeasActivity.this, "Enter both title and the content of the idea", Toast.LENGTH_SHORT).show();
                } else {
                    GetIdeas gi = new GetIdeas(getTitle, getContent, GlobalData.name);
                    String uploadId = referenceIdeas.push().getKey();
                    referenceIdeas.child(uploadId)
                            .setValue(gi)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(IdeasActivity.this, "Idea suggested to the team! We'll try to implement if your idea is worth it."+"\nThank you!",Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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
    }

    private void startRepeatingTask() {
        runnable.run();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                Log.d("Bluetooth", name + " => " + rssi);
                if (rssi > -68) {
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000);
                    Toast.makeText(IdeasActivity.this, "Please maintain distance from others!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
