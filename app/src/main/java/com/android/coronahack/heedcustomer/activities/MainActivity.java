package com.android.coronahack.heedcustomer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.FetchAddressIntentService;
import com.android.coronahack.heedcustomer.helpers.GlobalData;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static RadioButton gender;
    @SuppressLint("StaticFieldLeak")
    public static EditText address;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    ResultReceiver resultReceiver;
    ProgressBar progressBar;
    ImageView medicalShop, groceryStore;
    TextView welcomeText;

//    Button bluetooth;
//    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    AlertDialog.Builder builder;
    AlertDialog alertDialog = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcome_text);
        medicalShop = findViewById(R.id.medicineButton);
        groceryStore = findViewById(R.id.groceriesButton);
//        bluetooth = findViewById(R.id.bt);

//        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        GlobalData.name = sharedPreferences.getString("name", "GUEST");
        GlobalData.age = sharedPreferences.getString("age", "NULL");
        GlobalData.address = sharedPreferences.getString("address", "NULL");
        GlobalData.gender = sharedPreferences.getString("gender", "NULL");
        GlobalData.isVolunteer = sharedPreferences.getString("isVolunteer", "0");

        welcomeText.setText("Welcome, "+GlobalData.name.toUpperCase()+" !");

        if (firstStart) {
            startRegistration();
        }

        if (!isConnected()) {
            AlertDialog alertDialogError;
            AlertDialog.Builder builderError = new AlertDialog.Builder(MainActivity.this);
            builderError.setMessage("Please switch on your internet connection and restart your app for hassle-free usage!")
                    .setCancelable(false);
            alertDialogError = builderError.create();
            alertDialogError.setTitle("Connectivity Issue");
            alertDialogError.show();
        }

        medicalShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MedicalShopActivity.class);
                startActivity(intent);
            }
        });

        groceryStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GroceryStoreActivity.class);
                startActivity(intent);
            }
        });

//        bluetooth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bluetoothAdapter.startDiscovery();
//            }
//        });
    }

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
//                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
//                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
//                TextView rssi_msg = (TextView) findViewById(R.id.btText);
//                rssi_msg.setText(rssi_msg.getText() + name + " => " + rssi + "dBm\n");
//                Log.d("Bluetooth", name + " => "+ rssi);
//            }
//        }
//    };

    private void startRegistration() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View registerView = LayoutInflater.from(this).inflate(R.layout.register_layout, viewGroup, false);

        if (!isConnected()) {
            AlertDialog alertDialogError;
            AlertDialog.Builder builderError = new AlertDialog.Builder(MainActivity.this);
            builderError.setMessage("Please switch on your internet connection and restart your app for hassle-free usage!")
                    .setCancelable(false);
            alertDialogError = builderError.create();
            alertDialogError.setTitle("Connectivity Issue");
            alertDialogError.show();
        } else {
            builder = new AlertDialog.Builder(this);
            builder.setView(registerView);
            alertDialog = builder
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        }

        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        final EditText name = registerView.findViewById(R.id.name);
        final EditText age = registerView.findViewById(R.id.age);
        address = registerView.findViewById(R.id.address);
        final RadioGroup radioGroup = registerView.findViewById(R.id.radioGroup);
        Button registerButton = registerView.findViewById(R.id.registerButton);
        ImageView getLocation = registerView.findViewById(R.id.locationButton);
        final CheckBox volunteerCheckbox = registerView.findViewById(R.id.volunteerCheckbox);
        progressBar = registerView.findViewById(R.id.progressBar);

        resultReceiver = new AddressResultReceiver(new Handler());

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    getCurrentLocation();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName, getAge, getAddress, isVolunteer = "0";
                getName = name.getText().toString();
                getAge = age.getText().toString();
                getAddress = address.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                gender = registerView.findViewById(selectedId);
                if (volunteerCheckbox.isChecked()) {
                    isVolunteer = "1";
                }
                if (getName.equals("") || getName.length() == 0
                        || getAge.equals("") || getAge.length() == 0
                        || getAddress.equals("") || getAddress.length() == 0 || selectedId == -1) {
                    Toast.makeText(MainActivity.this, "Enter all the credentials", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("firstStart", false);
                    editor.putString("name", getName);
                    editor.putString("age", getAge);
                    editor.putString("address", getAddress);
                    editor.putString("gender", gender.getText().toString());
                    editor.putString("isVolunteer", isVolunteer);
                    editor.apply();
                    alertDialog.cancel();
                }
            }
        });
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied! Please restart the app to access this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {

        progressBar.setVisibility(View.VISIBLE);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            GlobalData.latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            GlobalData.longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            Location location = new Location("providerNA");
                            location.setLatitude(GlobalData.latitude);
                            location.setLongitude(GlobalData.longitude);
                            fetchAddressFromLatLong(location);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void fetchAddressFromLatLong(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(GlobalData.RECEIVER, resultReceiver);
        intent.putExtra(GlobalData.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == GlobalData.SUCCESS_RESULT) {
                address.setText(resultData.getString(GlobalData.RESULT_DATA_KEY));
            } else {
                Toast.makeText(MainActivity.this, resultData.getString(GlobalData.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

}
