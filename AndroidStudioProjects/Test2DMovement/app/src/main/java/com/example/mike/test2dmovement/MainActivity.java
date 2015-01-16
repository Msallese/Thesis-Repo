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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    boolean captureAccel = false;
    List<List<Float>> trainedArray = new ArrayList<List<Float>>();
    ArrayList<Float> xRecog = new ArrayList<Float>();
    ArrayList<Float> yRecog = new ArrayList<Float>();
    ArrayList<TrainingSample> trainingSamplesArray = new ArrayList<TrainingSample>();
    int dataCount = 0;
    TrainingSample tempTrain = new TrainingSample("tempTrain");
    EditText movementName;
    int movementIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);
        movementName = (EditText)findViewById(R.id.editTextMovementName);
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


            if (captureAccel == true){
                tempTrain.addX(x);
                tempTrain.addY(y);
                dataCount++;
            }
            if(dataCount > 249){
                //Disable capturing of accelerometer data
                captureAccel = false;
                //Set the exercise name to the size of the list of training samples for reference
                tempTrain.setExerciseName(movementName.getText().toString());
                //Make sure recognizeMovement only has one element
                if (findMovementByName("movementToRecognize") == true && movementName.getText().toString().equals("movementToRecognize")){
                    trainingSamplesArray.get(movementIndex).xValues = tempTrain.xValues;
                    trainingSamplesArray.get(movementIndex).yValues = tempTrain.yValues;
                    trainingSamplesArray.get(movementIndex).exerciseName = tempTrain.exerciseName;
                }
                else{
                    //Add the temp training sample to the list
                    trainingSamplesArray.add(tempTrain);
                }
                //Clear the temp training sample so it can be reused
                tempTrain = new TrainingSample("tempTrain");
                dataCount = 0;
                Toast.makeText(getApplicationContext(), "Movement recorded",Toast.LENGTH_SHORT).show();
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
        if (findMovementByName(movementName.getText().toString())){
            ListView listAccData = (ListView)findViewById(R.id.listViewAccData);
            ArrayAdapter<Float> adapter = new ArrayAdapter<Float>(this,android.R.layout.simple_list_item_1,trainingSamplesArray.get(movementIndex).xValues);
            listAccData.setAdapter(adapter);
        }
        else{
            Toast.makeText(getApplicationContext(), "Name doesn't exist",Toast.LENGTH_SHORT).show();
        }



    }

    public void printY(View view){
        if (findMovementByName(movementName.getText().toString())){
            ListView listAccData = (ListView) findViewById(R.id.listViewAccData);
            ArrayAdapter<Float> adapter = new ArrayAdapter<Float>(this, android.R.layout.simple_list_item_1, trainingSamplesArray.get(movementIndex).yValues);
            listAccData.setAdapter(adapter);
        }
        else{
            Toast.makeText(getApplicationContext(), "Name doesn't exist",Toast.LENGTH_SHORT).show();
        }

    }

    public void trainRecognizeMovement(View view){
        if (captureAccel == false){
            captureAccel = true;
            movementName.setText("movementToRecognize");
        }
    }

    public void recognizeMovement(View view){
//        TrainingSample known = trainingSamplesArray.get(0);
//        TrainingSample knew = trainingSamplesArray.get(1);
//        generateTally(known,knew);
//        Log.d("Euc", String.valueOf(0.00));
        findMovementByName("movementToRecognize");
        TrainingSample newMovement = trainingSamplesArray.get(movementIndex);
        for (int i=0;i<trainingSamplesArray.size();i++){
            trainingSamplesArray.get(i).distanceTally = 0.0;
            generateTally(trainingSamplesArray.get(i),newMovement);
        }
        Log.d("Euc", String.valueOf(0.00));
        String recognizedMovement = KNN(trainingSamplesArray);
        Toast.makeText(getApplicationContext(), recognizedMovement,Toast.LENGTH_SHORT).show();

    }

    public boolean findMovementByName(String movementName){
        if (trainingSamplesArray.size() > 0){
            TrainingSample temp;
            for (int i = 0;i < trainingSamplesArray.size();i++){
                temp = trainingSamplesArray.get(i);
                if (temp.exerciseName.equals(movementName)){
                    movementIndex = i;
                    return true;
                }
            }
        }
        return false;
    }

    public double euclideanDistance(Float x1,Float y1,Float x2,Float y2){
        double  xDiff = (double) x1-x2;
        double  xSqr  = Math.pow(xDiff, 2);
        double yDiff = (double) y1-y2;
        double ySqr = Math.pow(yDiff, 2);
        double output   = Math.sqrt(xSqr + ySqr);
        return output;
    }

    public void generateTally(TrainingSample knownMovement, TrainingSample newMovement){
        for (int i=0;i<knownMovement.xValues.size();i++){
            knownMovement.updateTally(euclideanDistance(knownMovement.xValues.get(i), knownMovement.yValues.get(i), newMovement.xValues.get(i), newMovement.yValues.get(i)));
        }
    }

    public String KNN(ArrayList<TrainingSample> trainingSampleArray){
        //Sorts the arraylist by tally
        Collections.sort(trainingSampleArray,new CustomComparator());
        String mostOccurringName = "";
        int currentMostOccurringName = 0;
        int occurrences = 0;
        int k = 3;
        ArrayList<String> knnArray = new ArrayList<>();
        //Add the top k items to a new array
        for (int i=1;i<k+1;i++){
           knnArray.add(trainingSampleArray.get(i).exerciseName);
        }
        for (int i=0;i<knnArray.size();i++){
            occurrences = Collections.frequency(knnArray, knnArray.get(i));
            if (currentMostOccurringName <= occurrences){
                currentMostOccurringName = occurrences;
                mostOccurringName = knnArray.get(i);
            }
        }
        return mostOccurringName;
    }
}