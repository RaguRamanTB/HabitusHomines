package com.android.coronahack.heedcustomer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.EnterMedAdapter;
import com.android.coronahack.heedcustomer.helpers.EnterMeds;

import java.util.ArrayList;
import java.util.List;

public class MedicalShopActivity extends AppCompatActivity {

    ImageView locateMed, addTab, removeTab, uploadPrescription;
    EditText nearestMed, uploadedPrescription, tabName, tabQuantity;
    Button submit;
    RecyclerView tabsRecycler;
    RecyclerView.Adapter mAdapter;
    List<EnterMeds> enterMedsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_shop);

        Toolbar toolbar = findViewById(R.id.buyMedicine);
        toolbar.setTitle("Buy Medicine");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        locateMed = findViewById(R.id.medLocationButton);
        addTab = findViewById(R.id.tabPlus);
        removeTab = findViewById(R.id.tabMinus);
        uploadPrescription = findViewById(R.id.uploadPrescriptionButton);
        nearestMed = findViewById(R.id.nearestMed);
        uploadedPrescription = findViewById(R.id.uploadPrescription);
        tabName = findViewById(R.id.tabName);
        tabQuantity = findViewById(R.id.tabQuantity);
        submit = findViewById(R.id.submit);
        tabsRecycler = findViewById(R.id.tabsRecycler);

        enterMedsList = new ArrayList<>();
        tabsRecycler.setLayoutManager(new LinearLayoutManager(this));

        addTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTablets();
            }
        });

        removeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTablets();
            }
        });
    }

    private void removeTablets() {
        if (enterMedsList.size() > 0) {
            enterMedsList.remove(enterMedsList.size() - 1);
            mAdapter = new EnterMedAdapter(this, enterMedsList);
            tabsRecycler.setAdapter(mAdapter);
        }
    }

    private void addTablets() {
        String getTabName, getTabQuantity;
        getTabName = tabName.getText().toString();
        getTabQuantity = tabQuantity.getText().toString();

        if (getTabName.equals("") || getTabName.length() == 0
                || getTabQuantity.equals("") || getTabQuantity.length() == 0) {
            Toast.makeText(MedicalShopActivity.this, "Enter both tablet name and quantity", Toast.LENGTH_SHORT).show();
        } else {
            enterMedsList.add(new EnterMeds(getTabName, getTabQuantity));
            mAdapter = new EnterMedAdapter(this, enterMedsList);
            tabsRecycler.setAdapter(mAdapter);
            tabName.setText("");
            tabQuantity.setText("");
        }
    }
}
