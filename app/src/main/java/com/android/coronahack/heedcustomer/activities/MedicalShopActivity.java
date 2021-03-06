package com.android.coronahack.heedcustomer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.EnterMedAdapter;
import com.android.coronahack.heedcustomer.helpers.EnterMeds;
import com.android.coronahack.heedcustomer.helpers.GlobalData;
import com.android.coronahack.heedcustomer.helpers.UploadRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MedicalShopActivity extends AppCompatActivity {

    ImageView locateMed, addTab, removeTab, uploadPrescription;
    //    ImageView uploadedPrescription;
    public static EditText nearestMed, tabName, tabQuantity, phNum, prescription;
    Button submit;
    RecyclerView tabsRecycler;
    RecyclerView.Adapter mAdapter;
    List<EnterMeds> enterMedsList;
    Uri imageUri = null;
    StorageReference mStorageRef;
    StorageTask mUploadTask;
    DatabaseReference referencePrescription;
    ProgressBar progressBar;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Handler handler;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_shop);

        handler  = new Handler();
        startRepeatingTask();

        Toolbar toolbar = findViewById(R.id.buyMedicine);
        toolbar.setTitle("Buy Medicine");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference().child("prescriptions");
        referencePrescription = FirebaseDatabase.getInstance().getReference().child("prescriptions");

        locateMed = findViewById(R.id.medLocationButton);
        addTab = findViewById(R.id.tabPlus);
        removeTab = findViewById(R.id.tabMinus);
        uploadPrescription = findViewById(R.id.uploadPrescriptionButton);
        nearestMed = findViewById(R.id.nearestMed);
//        uploadedPrescription = findViewById(R.id.uploadedPrescriptionImage);
        progressBar = findViewById(R.id.upload_progress);
        tabName = findViewById(R.id.tabName);
        tabQuantity = findViewById(R.id.tabQuantity);
        phNum = findViewById(R.id.phNum);
        prescription = findViewById(R.id.prescription);
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
//                clickImage();
                openImage();
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

        submit.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(MedicalShopActivity.this, "Please maintain distance from others!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void openImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, 101);
    }

    private void submitRequest() {
        String nMed, nPhNum;
        nMed = nearestMed.getText().toString();
        nPhNum = phNum.getText().toString();

        if (nMed.length() == 0 || nPhNum.length() == 0 || nPhNum.equals(" ")) {
            Toast.makeText(MedicalShopActivity.this, "Selected shop and phone number are mandatory.", Toast.LENGTH_SHORT).show();
        } else if (nPhNum.length() != 10) {
            Toast.makeText(MedicalShopActivity.this, "Enter valid phone number!", Toast.LENGTH_SHORT).show();
        } else if (prescription.getText().toString().equals(" ") || prescription.getText().toString().length() == 0) {
            final AlertDialog alertDialogError;
            AlertDialog.Builder builderError = new AlertDialog.Builder(MedicalShopActivity.this);
            builderError.setMessage("You have not given the prescription, please ensure that the tablets you have entered do not require prescription. If found fraud, your request will be cancelled!")
                    .setCancelable(false)
                    .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            uploadRequest();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialogError = builderError.create();
            alertDialogError.setTitle("Reminder");
            alertDialogError.show();
        } else {
            if (enterMedsList.size() == 0) {
                Toast.makeText(MedicalShopActivity.this, "You have entered no medicines.", Toast.LENGTH_SHORT).show();
            }
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), "Song upload is already in progress", Toast.LENGTH_LONG).show();
            } else {
                uploadRequest();
            }
        }
    }

    private void uploadRequest() {
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            mUploadTask = storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UploadRequest uploadRequest = new UploadRequest(nearestMed.getText().toString(), GlobalData.name, phNum.getText().toString(), GlobalData.address, uri.toString(), enterMedsList, 0, "9:00 am to 9:30 am");
                                    String uploadId = referencePrescription.push().getKey();
                                    referencePrescription.child(uploadId).setValue(uploadRequest);
                                }
                            });
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MedicalShopActivity.this, "Request successful! Please wait for the shop to confirm and allot you a slot.", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            UploadRequest uploadRequest = new UploadRequest(nearestMed.getText().toString(), GlobalData.name, phNum.getText().toString(), GlobalData.address, enterMedsList, 0, "9:00 am to 9:30 am");
            String uploadId = referencePrescription.push().getKey();
            referencePrescription.child(uploadId)
                    .setValue(uploadRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MedicalShopActivity.this, "Request successful! Please wait for the shop to confirm and allot you a slot.", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            String fileName = getFileName(imageUri);
            prescription.setText(fileName);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getFileName(Uri imageUri) {
        String result = null;
        if (imageUri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(imageUri, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = imageUri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}
