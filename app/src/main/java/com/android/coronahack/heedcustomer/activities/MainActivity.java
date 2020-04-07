package com.android.coronahack.heedcustomer.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.coronahack.heedcustomer.R;
import com.android.coronahack.heedcustomer.helpers.GlobalData;

public class MainActivity extends AppCompatActivity {

    public static RadioButton gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        GlobalData.name = sharedPreferences.getString("name","NULL");
        GlobalData.age = sharedPreferences.getString("age","NULL");
        GlobalData.address = sharedPreferences.getString("address","NULL");
        GlobalData.gender = sharedPreferences.getString("gender","NULL");

        if (firstStart) {
            startRegistration();
        }
    }

    private void startRegistration() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View registerView = LayoutInflater.from(this).inflate(R.layout.register_layout, viewGroup, false);

        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        final EditText name = registerView.findViewById(R.id.name);
        final EditText age = registerView.findViewById(R.id.age);
        final EditText address = registerView.findViewById(R.id.address);
        final RadioGroup radioGroup = registerView.findViewById(R.id.radioGroup);
        Button registerButton = registerView.findViewById(R.id.registerButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(registerView);
        final AlertDialog alertDialog = builder
                .setCancelable(false)
                .create();
        alertDialog.show();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName, getAge, getAddress;
                getName = name.getText().toString();
                getAge = age.getText().toString();
                getAddress = address.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                gender = registerView.findViewById(selectedId);
                if (getName.equals("") || getName.length() == 0
                        || getAge.equals("") || getAge.length() == 0
                        || getAddress.equals("") || getAddress.length() == 0 || selectedId == -1) {
                    Toast.makeText(MainActivity.this, "Enter all the credentials", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("firstStart", false);
                    editor.putString("name", getName);
                    editor.putString("age", getAge);
                    editor.putString("address", getAddress);
                    editor.putString("gender",gender.getText().toString());
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
}
