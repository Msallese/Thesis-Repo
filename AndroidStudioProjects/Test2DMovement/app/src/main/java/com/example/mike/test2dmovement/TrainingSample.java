package com.example.mike.test2dmovement;

import java.util.ArrayList;

/**
 * Created by Mike on 12/14/2014.
 */
public class TrainingSample {
    String exerciseName;
    ArrayList<Float> xValues = new ArrayList<Float>();
    ArrayList<Float> yValues = new ArrayList<Float>();

    public TrainingSample(String name){
        this.exerciseName = name;
    }

    public void setExerciseName(String name){
        exerciseName = name;
    }

    public void addX(Float xValue){
        xValues.add(xValue);
    }

    public void addY(Float yValue){
        yValues.add(yValue);
    }

    public  void clearTrainingSample(){
        xValues.clear();
        yValues.clear();
        exerciseName = null;
    }
}
