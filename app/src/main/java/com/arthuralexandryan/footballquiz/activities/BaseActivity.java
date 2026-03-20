package com.arthuralexandryan.footballquiz.activities;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arthuralexandryan.footballquiz.R;
import com.arthuralexandryan.footballquiz.utils.Constants;
import com.arthuralexandryan.footballquiz.utils.Prefer;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    protected int n, category_Score, category_answer_count;
    protected String categoryType, categoryName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(Prefer.getStringPreference(this, Constants.INSTANCE.getLocalization(),"ru"));
    }

    public void setToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

    public void setLocale(String localeName) {
        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
