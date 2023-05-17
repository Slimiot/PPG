package com.slimiot.ecg.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.slimiot.ecg.BleAction.BluetoothConnection;
import com.slimiot.ecg.BleOperations.BleActivities;
import com.slimiot.ecg.R;

import java.util.Calendar;

public class UserDetail extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button nextButton;

    private String genderValue = null , dobValue=null ;
    private SharedPreferences sharedPreferences;

    String[] spinValues;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        getSupportActionBar().hide();
        nextButton = findViewById(R.id.nextButton);
        spinValues = new String[]{"UTF-8 (Comma Format)", "2 byte form"};
        spinner = findViewById(R.id.dataFormatSpinner);
        sharedPreferences = this.getSharedPreferences("com.slimiot.ecg", MODE_PRIVATE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ecgAnalysis();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(mContext, spinValues[i], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinValues);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);
    }
    private void ecgAnalysis() {

        String spinValue = spinner.getSelectedItem().toString();
        if(spinValue.equals("UTF-8 (Comma Format)")){
            BleActivities.dataFormat = "UTF-8 (Comma Format)";
        }else if(spinValue.equals("2 byte form")){
            BleActivities.dataFormat = "2 byte form";
        }
        startActivity(new Intent(getApplicationContext(), BluetoothConnection.class));
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}