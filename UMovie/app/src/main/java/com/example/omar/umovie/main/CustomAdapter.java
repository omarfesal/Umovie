package com.example.omar.umovie.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.omar.umovie.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by omar on 31/10/2016.
 */
public class CustomAdapter extends ArrayAdapter {
    private ArrayList<movie> movieArrayList ;
    Context context;

    CustomAdapter(Context context , ArrayList<movie> movieArrayList){
        super(context,0,movieArrayList);
        this.movieArrayList = movieArrayList;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate( R.layout.content_of_gridview  , parent, false);
        }


        Picasso
                .with(getContext())
                .load(movieArrayList.get(position).getImagePosterURL())
                .fit()
                .into((ImageView) listItemView);


        return listItemView;

    }


}
