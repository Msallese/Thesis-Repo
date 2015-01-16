package com.example.mike.test2dmovement;

import java.util.Comparator;

/**
 * Created by Snowbird on 1/15/2015.
 */
public class CustomComparator implements Comparator<TrainingSample> {
    @Override
    public int compare(TrainingSample t1, TrainingSample t2) {
        return t1.distanceTally.compareTo(t2.distanceTally);
    }
}
