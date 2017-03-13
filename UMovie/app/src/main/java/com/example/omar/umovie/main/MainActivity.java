package com.example.omar.umovie.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.omar.umovie.Detail.DetailActvity;
import com.example.omar.umovie.Detail.DetailFragment;
import com.example.omar.umovie.Listener;
import com.example.omar.umovie.R;

public class MainActivity extends AppCompatActivity implements Listener {
    boolean checkTwoPane = false;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // background color of top par of actvity
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#e50e0e"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);


        MainFragment mainFragment = new MainFragment();
        mainFragment.setFilmListener(this);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.flmain, mainFragment).commit();

        if (findViewById(R.id.fldetail) != null)
            checkTwoPane = true;


    }

    @Override
    public void setData(String title, String imagePosterURL, String overView, String RealesDate, double vote_average, String id) {
        if (!checkTwoPane) {
            Intent intent = new Intent(this, DetailActvity.class);
            intent.putExtra("title", title);
            intent.putExtra("imagePosterURL", imagePosterURL);
            intent.putExtra("overView", overView);
            intent.putExtra("RealesDate", RealesDate);
            intent.putExtra("vote_average", vote_average);
            intent.putExtra("id", id);
            startActivity(intent);

        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("imagePosterURL", imagePosterURL);
            bundle.putString("overView", overView);
            bundle.putString("RealesDate", RealesDate);
            bundle.putDouble("vote_average", vote_average);
            bundle.putString("id", id);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fldetail, detailFragment, "").commit();
        }
    }


}
