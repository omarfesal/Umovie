package com.example.omar.umovie.Detail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.omar.umovie.R;

public class DetailActvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_actvity);

        // background color of top par of actvity
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#e50e0e"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        DetailFragment detailFragment = new DetailFragment();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        detailFragment.setArguments(bundle);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fldetail, detailFragment, "").commit();

    }


}

