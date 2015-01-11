package com.example.mike.test2dmovement;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    boolean captureAccel = false;
    boolean recognize = false;
    List<List<Float>> trainedArray = new ArrayList<List<Float>>();
    ArrayList<Float> xRecog = new ArrayList<Float>();
    ArrayList<Float> yRecog = new ArrayList<Float>();
    ArrayList<TrainingSample> trainingSamplesArray = new ArrayList<TrainingSample>();
    int dataCount = 0;
    TrainingSample tempTrain = new TrainingSample("tempTrain");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        TextView xData = (TextView)findViewById(R.id.txtX);
        TextView yData = (TextView)findViewById(R.id.txtY);

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            String xString = String.valueOf(x);
            String yString = String.valueOf(y);
            String zString = String.valueOf(z);

            Log.d("AccData", xString);
            Log.d("AccData", yString);
            Log.d("AccData", zString);

            xData.setText("X: " + xString);
            yData.setText("Y: " + yString);


            if (captureAccel == true && dataCount <10){
                tempTrain.addX(x);
                tempTrain.addY(y);
                dataCount++;
            }
            if(dataCount >= 9){
                //Disable capturing of accelerometer data
                captureAccel = false;
                recognize = false;
                //Set the exercise name to the size of the list of training samples for reference
                tempTrain.setExerciseName(Integer.toString(trainingSamplesArray.size()));
                //Add the temp training sample to the list
                trainingSamplesArray.add(tempTrain);
                //Clear the temp training sample so it can be reused
                tempTrain = new TrainingSample("tempTrain");
                dataCount = 0;
            }
        if (recognize == true && xRecog.size() < 10){
            xRecog.add(x);
            yRecog.add(y);
            dataCount++;
        }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startTraining(View view){
        if (captureAccel == false){
            captureAccel = true;
        }

    }

    public void printX(View view){
//        TextView txtAccData = (TextView)findViewById(R.id.txtAccData);
//        txtAccData.setText(trainedArray.get(0).toString());

        ListView listAccData = (ListView)findViewById(R.id.listViewAccData);
        ArrayAdapter<Float> adapter = new ArrayAdapter<Float>(this,android.R.layout.simple_list_item_1,trainingSamplesArray.get(0).xValues);
        listAccData.setAdapter(adapter);
    }

    public void printY(View view){
//        TextView txtAccData = (TextView)findViewById(R.id.txtAccData);
//        txtAccData.setText(trainedArray.get(0).toString());

        ListView listAccData = (ListView)findViewById(R.id.listViewAccData);
        ArrayAdapter<Float> adapter = new ArrayAdapter<Float>(this,android.R.layout.simple_list_item_1,trainingSamplesArray.get(0).yValues);
        listAccData.setAdapter(adapter);
    }

    public void trainRecognizeMovement(View view){
        recognize = true;
        dataCount = 0;
    }

    public void recognizeMovement(View view){

    }
}