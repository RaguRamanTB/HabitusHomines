package com.android.coronahack.heedcustomer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.EnterMedAdapter;
import com.android.coronahack.heedcustomer.helpers.EnterMeds;
import com.android.coronahack.heedcustomer.helpers.GlobalData;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GroceryStoreActivity extends AppCompatActivity {

    ImageView locateG, addG, removeG;
    public static EditText nearestG, gName, gQuantity;
    Button submitG;
    RecyclerView gRecycler;
    RecyclerView.Adapter gAdapter;
    List<EnterMeds> enterGList;

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

        locateG = findViewById(R.id.gLocationButton);
        addG = findViewById(R.id.gPlus);
        removeG = findViewById(R.id.gMinus);
        nearestG = findViewById(R.id.nearestG);
        gName = findViewById(R.id.gName);
        gQuantity = findViewById(R.id.gQuantity);
        submitG = findViewById(R.id.submitGrocery);
        gRecycler = findViewById(R.id.gRecycler);

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
