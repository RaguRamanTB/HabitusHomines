package com.android.coronahack.heedcustomer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.GetChars;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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
}
