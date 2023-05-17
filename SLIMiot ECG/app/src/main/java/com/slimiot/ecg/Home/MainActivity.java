package com.slimiot.ecg.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import hrv.HRVLibFacade;
import hrv.RRData;
import hrv.calc.parameter.HRVParameter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.utils.HexUtil;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.slimiot.ecg.BleAction.BluetoothConnection;
import com.slimiot.ecg.BleOperations.BleActivities;
import com.slimiot.ecg.R;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mRecieverData;
    private BroadcastReceiver mDeviceConnectionReciever;
    private LineChart chartSleep;
    private TextView readingCount , closeGraph;
    private List<Entry> entries_data;
    private LineDataSet dataSet_data;
    private LineData lineData_data;
    float x_ecg = 0;
    float ecg_y_max = 0;
    float ecg_y_min = 0;
    float ecg_y_diff;

    boolean ecg_graph_update;
    private Handler sleepHandler;
    int countECG=0;
    private boolean firstOpen = true;
    private BleDevice bleDevice;
    public static final String KEY_DATA = "key_data";
    public Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mContext = MainActivity.this;
        chartSleep = findViewById(R.id.chart_sleep);
        readingCount = findViewById(R.id.data_readings);
        closeGraph = findViewById(R.id.txtCloseGraph);
        ecg_graph_update = false;
        entries_data = new ArrayList<>();
        dataSet_data = new LineDataSet(entries_data, "Data");
        dataSet_data.setDrawCircles(false);
        dataSet_data.setCubicIntensity(2000f);
        dataSet_data.setDrawValues(false);
        dataSet_data.setLineWidth(3f);
        // on ecg
//        BleActivities.onECG();
        // off it on destroy
        //
        lineData_data = new LineData(dataSet_data);
        chartSleep.setData(lineData_data);

// *****************************************************************


//            chartECG.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);
        chartSleep.setDrawGridBackground(false);
        chartSleep.getDescription().setEnabled(false);
        chartSleep.setNoDataText("No chart data available. Use the menu to add entries and data sets!");
        XAxis xAxis = chartSleep.getXAxis();
//            xAxis.setLabelCount(500);
//            ViewPortHandler viewPortHandler = chartECG.getViewPortHandler();
//            viewPortHandler.setMinMaxScaleX(0, 500);
////            float minXRange = 10;
//            float maxXRange = 10;
//            chartECG.setVisibleXRange(minXRange, maxXRange);
        chartSleep.invalidate();
        dataSet_data = new LineDataSet(entries_data, "ECG");
        dataSet_data.setDrawCircles(false);
        dataSet_data.setCubicIntensity(2000f);
        dataSet_data.setDrawValues(false);
        dataSet_data.setLineWidth(3f);
//            chartECG.setDragDecelerationFrictionCoef(0.9f);
        lineData_data = new LineData(dataSet_data);
        chartSleep.setData(lineData_data);

        LineData data = chartSleep.getData();

        if (data == null) {
            data = new LineData();
            chartSleep.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        XAxis xAxisECG = chartSleep.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                    Log.i("READ_Aman",String.valueOf(x_ecg)+" "+value+" "+axis);
                if(value%50==0){
                    return  String.valueOf((int)value/50);
                }else{
                    return "";
                }
//                    return xValues.get((int) value % xValues.size());
            }
        });

        Random random = new Random();
        int result = random.nextInt(10) + 1;
        for(int i=0;i<1;i++){
            result = random.nextInt(10) + 1;
            entries_data.add(new Entry(x_ecg,result));
            x_ecg = x_ecg + 1f;
            Log.i("x_ecg_value_runn",String.valueOf(x_ecg));
//            dataSet_data.notifyDataSetChanged();
//            lineData_data.notifyDataChanged();
//            chartSleep.notifyDataSetChanged();
//            chartSleep.setVisibleXRangeMinimum(0);
//            chartSleep.setVisibleXRangeMaximum(500);
//            chartSleep.moveViewTo(dataSet_data.getEntryCount() - 5, 50f, YAxis.AxisDependency.LEFT);


        }


// ****************************************************************

        dataSet_data.notifyDataSetChanged();
        lineData_data.notifyDataChanged();
        chartSleep.notifyDataSetChanged();
        chartSleep.setVisibleXRangeMinimum(0);
        chartSleep.setVisibleXRangeMaximum(500);
        chartSleep.moveViewTo(x_ecg - 7, 50f, YAxis.AxisDependency.LEFT);

        sleepHandler = new Handler();

        Runnable runn =new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("READ_Prakhar_Samples", String.valueOf(countECG));
                    readingCount.setText("Data per second :"+String.valueOf(countECG / 2));
//                                            prevCountECG = countECG;
                    countECG = 0;
                    sleepHandler.postDelayed(this, 2000);
                }catch (Exception e){

                }
            }
        };
        sleepHandler.post(runn);
        closeGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
    }
    private void initData() {
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);

        // we are using BleActivities.........
        BleActivities.mBleDevice = bleDevice;
        BleActivities.setBleDevice(bleDevice);
        BleActivities.setContext(mContext);

//        BleOperations.setBleDevice(bleDevice);
//        BleOperations.setContext(mContext);

        if (bleDevice == null) {
            Toast.makeText(this, "Device not connected", Toast.LENGTH_SHORT).show();
        } else {

            BleActivities.setBleDevice(bleDevice);
            BleActivities.setContext(mContext);
            mRecieverData = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Handle the sensor data here
                    byte[] data = intent.getByteArrayExtra("data");
                    String content = HexUtil.formatHexString(data, true);
                    Log.i("READ_UI_DATA_Sl", content);
//                if(BleActivities.measureECG) {
//                    plotGraph(data);
                    plotGraph(data);
//                    updateUI_HeartRate(data);
//                    updateUI_RRView(data);
//                }
                }
            };
            IntentFilter filterData = new IntentFilter("sensor-dataECG");
            LocalBroadcastManager.getInstance(this).registerReceiver(mRecieverData, filterData);

        if(mDeviceConnectionReciever==null) {
//             Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                mDeviceConnectionReciever = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Handle the sensor data here
                        String deviceName = intent.getStringExtra("deviceName");
                        String deviceAddress = intent.getStringExtra("deviceAddress");
//                        connectBle.setText("Connect Now");
                        Toast.makeText(context, deviceName + " is disconnected", Toast.LENGTH_SHORT).show();
//                        Log.i("READ_UI_DATA_Dev", deviceName + " " + deviceAddress + " disconnected");
                    }
                };
                IntentFilter filterDeviceConnection = new IntentFilter("DEVICE_DISCONNECTED");
                LocalBroadcastManager.getInstance(this).registerReceiver(mDeviceConnectionReciever, filterDeviceConnection);
            }
        }
        try {
            BleActivities.onECG();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void plotGraph(byte[] data) {
//        String esp_data = utf8Decoder(data);
//        Log.i("READ_Prakhar Helloo", esp_data);
//        String dat = null;
//        dat = esp_data.substring(0, esp_data.length() - 1);
//        String[] datArr = dat.split(",");
//        Log.i("READ_Prakhar Helloox", Arrays.toString(datArr)+" "+datArr[0]);
//                                                 butterworth.lowPass(order,Samplingfreq,Cutoff frequ);
//                                                 butterworth.bandPass();


        // *****************

        try {

            // ************************Data coming in separated format 2 byte value *********************


            // ************************Data coming in UTF-8 comma separated format *********************
            if(BleActivities.dataFormat.equals("UTF-8 (Comma Format)")) {
                String esp_data = utf8Decoder(data);
                Log.i("READ_Prakhar Helloo", esp_data);
                String dat = null;
                dat = esp_data.substring(0, esp_data.length() - 1);
                String[] datArr = dat.split(",");
                Log.i("READ_Prakhar Helloox", Arrays.toString(datArr)+" "+datArr[0]);
                double temp;
                for (int i = 0; i < datArr.length; i++) {
//            temp = 0.0;
                    try {
                        temp = Double.parseDouble(datArr[i]);

                        Log.i("READ_Prakhar_ESPAD8232_", String.valueOf(temp));

                        if (firstOpen) {
                            ecg_y_min = (float) temp;
                            ecg_y_max = (float) temp;
                            firstOpen = false;
                        }

                        ecg_y_max = (float) Math.max(ecg_y_max, temp);
                        ecg_y_min = (float) Math.min(ecg_y_min, temp);
                        Log.i("READ_Prakhar_MAX_MIN", ecg_y_max + " " + ecg_y_min);
                        if (ecg_graph_update) {
//                                                            entries_ecg.add(0,new Entry(x_ecg, (float) temp));
//                                                            entries_ecg.remove(1250);
                            entries_data.set((int) x_ecg, new Entry(x_ecg, (float) temp));
                        } else {
                            entries_data.add(new Entry(x_ecg, (float) temp));
                        }
//                                                        Log.i("READ_PrakharECG_", String.valueOf(ecg_graph));
                        x_ecg = x_ecg + 1f;
                        countECG++;
                        if (x_ecg == 500) {
                            x_ecg = 0;
                            ecg_graph_update = true;
                            //  entries_ecg.set(0,new Entry(x_ecg, ecg_graph));
                        }
//                                                        }
                    } catch (Exception e) {

                    }
                }
            }else if(BleActivities.dataFormat.equals("2 byte form")){
                int size = data.length;
                int k = size / 2;
                double[] val = new double[k];
                int ecgValue;
                int temp = 0;
                float ecg_graph;
                for (int i = 0; i < k; i++) {
//                                                    ecgValue = (data[3 + temp] & 0xff) << 24 | (data[2 + temp] & 0xff) << 16 | (data[1 + temp] & 0xff) << 8 | (data[0 + temp] & 0xff);

                    ecgValue = (data[1 + temp] & 0xff) << 8 | (data[0 + temp] & 0xff);
//                                                    ecgValue = (data[1 + temp] & 0xff ) + (data[0 + temp]) ;
                    if ((ecgValue & 0x8000) != 0) {
//                                                        Log.i("READ_Prakhar_Hellol","yes");
                        ecgValue = -(0x10000 - ecgValue);
                    }

                    if (firstOpen) {
                        ecg_y_min = (float) ecgValue;
                        ecg_y_max = (float) ecgValue;
                        firstOpen = false;
                    }

//                                                    ecgValue = (dataF[0 + temp] & 0xff);
//                                                    ecgValue = (data[0 + temp] & 0xff);
                    ecg_graph = (float) ecgValue;
                    ecg_graph = ecg_graph / 10f;
//                    fileString = fileString + String.valueOf(ecg_graph) +"\n" ;
                    Log.i("READ_Prakhar_ECG_VALUEF",String.valueOf(ecg_graph));
                    val[i] = (double) ecg_graph;
                    temp = temp + 2;
                    ecg_graph = ecg_graph * 0.0000244140625f;
                    ecg_y_max = Math.max(ecg_y_max,ecgValue);
                    ecg_y_min = Math.min(ecg_y_min,ecgValue);
                    if(ecg_graph_update){
//                                                        entries_ecg.add(0,new Entry(x_ecg, (float) temp));
//                                                        entries_ecg.remove(1250);

                        entries_data.set((int)x_ecg,new Entry(x_ecg, ecgValue));
                    }else{
                        entries_data.add(new Entry(x_ecg, ecgValue));
                    }
                    Log.i("READ_PrakharECG_", String.valueOf(ecg_graph)+" "+entries_data.size());
//                                                entries_ecg.add(new Entry(x_ecg, ecg_graph));
                    x_ecg = x_ecg + 1f;
                    countECG++;
                    if(x_ecg==1250){
                        x_ecg = 0;
                        ecg_graph_update = true;
                        //  entries_ecg.set(0,new Entry(x_ecg, ecg_graph));
                    }
                    Log.i("READ_PrakharECG__", String.valueOf(x_ecg));
                }
            }

            // *************************

            ecg_y_diff = ecg_y_max - ecg_y_min;
            ecg_y_diff = ecg_y_diff / 0.6f;
            XAxis xAxisECG = chartSleep.getXAxis();
            YAxis yAxis = chartSleep.getAxisLeft();
            yAxis.setAxisMaximum(ecg_y_max+ecg_y_diff*0.2f); // set the maximum y-axis value
            yAxis.setAxisMinimum(ecg_y_min-ecg_y_diff*0.2f);
            YAxis yAxisRight = chartSleep.getAxisRight();
            yAxisRight.setEnabled(false);
//            yAxisRight.setAxisMaximum(ecg_y_max+ecg_y_diff*0.2f); // set the maximum y-axis value
//            yAxisRight.setAxisMinimum(ecg_y_min-ecg_y_diff*0.2f);
            xAxisECG.setAxisMinimum(0); // set the minimum X-axis value
            xAxisECG.setAxisMaximum(500);


//            final float xGridLinesStep = 250f; // set the constant X-axis grid lines step
            final float xGridLinesStep = 50f; // set the constant X-axis grid lines step
            xAxisECG.setGranularity(xGridLinesStep); // set the X-axis grid lines step
            xAxisECG.setDrawGridLines(true);


            LineData data_ecg = chartSleep.getData();

            if (data_ecg == null) {
                data_ecg = new LineData();
                chartSleep.setData(data_ecg);
            }

            ILineDataSet set = data_ecg.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data_ecg.addDataSet(set);
            }

//                                        float xValueToRemove = 10f; // the X-axis value for which you want to remove the old entries
//                                        int entryIndexToRemove = set.getEntryIndex(xValueToRemove);
//                                        if (entryIndexToRemove >= 0) {
//                                            set.removeEntry(set.getEntryForIndex(entryIndexToRemove));
//                                        }



            dataSet_data.notifyDataSetChanged();
            lineData_data.notifyDataChanged();
            chartSleep.notifyDataSetChanged();
            chartSleep.setVisibleXRangeMinimum(0);
            chartSleep.setVisibleXRangeMaximum(1249);
            chartSleep.moveViewTo(x_ecg - 7, 50f, YAxis.AxisDependency.LEFT);
            chartSleep.invalidate();

            // ***********************************
//            dataSet_data.notifyDataSetChanged();
//            lineData_data.notifyDataChanged();
//            chartSleep.notifyDataSetChanged();
//            chartSleep.setVisibleXRangeMinimum(0);
//            chartSleep.setVisibleXRangeMaximum(500);
//            chartSleep.moveViewTo(x_ecg - 7, 50f, YAxis.AxisDependency.LEFT);
//            chartSleep.invalidate();

            // *****************************

        }catch (Exception e){
            Log.i("Graph_Error_Data",e.toString());
        }
    }
    public static String utf8Decoder(byte[] bytes) {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        try {
            return decoder.decode(buffer).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);

        return set;
    }
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleActivities.offECG();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRecieverData);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeviceConnectionReciever);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();


        // showPopUp - Want to disconnect device.  if yes send back to starting screen

//        if (BleManager.getInstance().isConnected(bleDevice)&&BleActivities.mBleDevice != null) {
//            showPopUPbackPressed();
//        }else{
//            Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }


        if (BleManager.getInstance().isConnected(bleDevice)&&BleActivities.mBleDevice != null) {
            disconnectDevicePopUp();
        }else{
            Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }




//        Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);


//        Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);

//        finish();
//        disconnectDevicePopUp("Do you want to close the application?");



//        finish();
    }
    public void disconnectDevicePopUp(){
        final AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_disconnect_ble_device, null);
        d.setView(dialogView);
        final TextView ok =dialogView.findViewById(R.id.okDisconnectDevice);
        final TextView cancel=dialogView.findViewById(R.id.cancelDisconnectDevice);
        AlertDialog alertDialog = d.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setLayout(600, 400);
        alertDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
//                disconnectFromDevice();
//                unregisterServices();

                Intent intent = new Intent(getApplicationContext(), BluetoothConnection.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                alertDialog.dismiss();

//                alertDialog.dismiss();
//                if(title.equals("Do you want to close the application?")){
////                    System.exit(0);
//
//                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
    public void disconnectFromDevice() {
//        BleManager bleManager = BleManager.getInstance();
//        if (BleManager.getInstance().isConnected(bleDevice)) {
////            String deviceAddress = bleDevice.getDevice().getAddress();
//            BleManager.getInstance().disconnect(bleDevice);
//            BleManager.getInstance().disconnectAllDevice();
//
//            boolean val = BleManager.getInstance().isConnected(bleDevice);
//            Log.i("Ble connection",val+" ");
//
//
////            BluetoothDevice mBluetoothDevice = bleDevice.getDevice();
////            mBluetoothDevice.
//
//            BleActivities.mBleDevice = null;
//
////            Intent i = new Intent(getApplicationContext(),BluetoothConnection.class);
////            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////            sharedPreferences.edit().putString("previousBluetoothDeviceAddress","").apply();
////            startActivity(i);
////            finish();
    //    }
    }
}