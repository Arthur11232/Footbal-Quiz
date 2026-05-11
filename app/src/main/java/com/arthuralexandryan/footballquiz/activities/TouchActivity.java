package com.arthuralexandryan.footballquiz.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.arthuralexandryan.footballquiz.R;

import java.util.ArrayList;
import java.util.List;

public class TouchActivity extends AppCompatActivity {

    List<Float> setX;
    List<Float> setY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e(getClass().getName(), "Touch Down");
            setX = new ArrayList<>();
            setY = new ArrayList<>();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            setX.add(event.getX());
            setY.add(event.getY());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.e(getClass().getName(), "Touch Up: " + setX.toString() + "\n " + setY.toString());
        }
        return super.onTouchEvent(event);
    }
}
