package com.android.coronahack.heedcustomer.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.GetNearbyShop;
import com.android.coronahack.heedcustomer.helpers.GlobalData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLng;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findMedicalShops();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Location not found!", Toast.LENGTH_SHORT).show();
        } else {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(update);
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            options.title("Current location");
            mMap.addMarker(options);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setInterval(50000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void findMedicalShops() {
        mMap.setOnMarkerClickListener(this);

        StringBuilder stringBuilder =  new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLng.latitude+","+latLng.longitude);
        stringBuilder.append("&radius="+3000); //3kms radius = 3000m
        stringBuilder.append("&keyword="+"medicalshop");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));
        String url = stringBuilder.toString();

        Object[] dataTransfer = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyShop getNearbyMedical = new GetNearbyShop();
        getNearbyMedical.execute(dataTransfer);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onMarkerClick(Marker marker) {
        GlobalData.medicalShopName = marker.getTitle();
        GlobalData.medicalShopAddress = marker.getSnippet();
        MedicalShopActivity.nearestMed.setText(GlobalData.medicalShopName + "\n" + GlobalData.medicalShopAddress);
        AlertDialog alertDialogError;
        AlertDialog.Builder builderError = new AlertDialog.Builder(MapsActivity.this);
        builderError.setMessage("Please press back once you have selected your nearest shop!")
                .setCancelable(true);
        alertDialogError = builderError.create();
        alertDialogError.setTitle("Reminder");
        alertDialogError.show();
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
