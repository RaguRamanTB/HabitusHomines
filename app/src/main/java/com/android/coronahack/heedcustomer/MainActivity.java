package com.android.coronahack.heedcustomer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRegistration();

    }

    private void startRegistration() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View registerView = LayoutInflater.from(this).inflate(R.layout.register_layout, viewGroup, false);
        new AlertDialog.Builder(this)
                .setView(registerView)
                .setCancelable(false)
                .create().show();
    }
}
