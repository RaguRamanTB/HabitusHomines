package com.android.coronahack.heedcustomer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

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

        final EditText name = registerView.findViewById(R.id.name);
        final EditText age = registerView.findViewById(R.id.age);
        final EditText address = registerView.findViewById(R.id.address);
        final RadioButton male = registerView.findViewById(R.id.maleRadioButton);
        final RadioButton female = registerView.findViewById(R.id.femaleRadioButton);
        final RadioButton not = registerView.findViewById(R.id.notToSayRadioButton);
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
                if (getName.equals("") || getName.length() == 0
                        || getAge.equals("") || getAge.length() == 0
                        || getAddress.equals("") || getAddress.length() == 0 ||
                        (!male.isChecked() && !female.isChecked() && !not.isChecked())) {
                    Toast.makeText(MainActivity.this, "Enter all the credentials", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.cancel();
                }
            }
        });
    }
}
