package com.arthuralexandryan.footballquiz.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.arthuralexandryan.footballquiz.R;
import com.arthuralexandryan.footballquiz.models.PlaceModel;
import com.arthuralexandryan.footballquiz.models.PlaceModelInterface;

public class TestActivity extends AppCompatActivity implements PlaceModelInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        new PlaceModel.PlaceModelBuilder(findViewById(R.id.testImage))
                .setImage(R.drawable.barcelona_logo)
                .setVersusFirst("Barcelona")
                .setVersusSecond("Welcome!")
                .setListener(this)
                .build();
    }

    @Override
    public void onClickPlace() {
        startActivity(new Intent(this, TouchActivity.class));
    }
}
