package com.slimiot.ppg.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.slimiot.ppg.BleAction.BluetoothConnection;
import com.slimiot.ppg.BleOperations.BleActivities;
import com.slimiot.ppg.R;

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
        spinValues = new String[]{"4 byte form","UTF-8 (Comma Format)"};
        spinner = findViewById(R.id.dataFormatSpinner);
        sharedPreferences = this.getSharedPreferences("com.slimiot.ppg", MODE_PRIVATE);
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
        }else if(spinValue.equals("4 byte form")){
            BleActivities.dataFormat = "4 byte form";
        }
        startActivity(new Intent(getApplicationContext(), BluetoothConnection.class));
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}