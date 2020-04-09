package com.android.coronahack.heedcustomer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MedicalShopActivity extends AppCompatActivity {

    ImageView locateMed, addTab, removeTab, uploadPrescription, uploadedPrescription;
    public static EditText nearestMed, tabName, tabQuantity;
    Button submit;
    RecyclerView tabsRecycler;
    RecyclerView.Adapter mAdapter;
    List<EnterMeds> enterMedsList;
    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1855;

    @SuppressLint("SetTextI18n")
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
        uploadedPrescription = findViewById(R.id.uploadedPrescriptionImage);
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

        uploadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImage();
            }
        });

        locateMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicalShopActivity.this, MapsActivity.class);
                intent.putExtra("type", "medicine");
                startActivity(intent);
            }
        });
    }

    private void clickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, RESULT_LOAD_IMAGE);
            } else {
                Toast.makeText(this, "Permission Denied! Please restart the app to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == RESULT_LOAD_IMAGE) && (resultCode == Activity.RESULT_OK)) {
            assert data != null;
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            uploadedPrescription.setImageBitmap(photo);
        }
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
