package com.example.omar.umovie.Detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.omar.umovie.R;

import java.util.List;

/**
 * Created by omar on 25/11/2016.
 */
public class trailerCustomAdapter extends ArrayAdapter {
    private List<Trailer> movieArrayList;
    Context context;

    trailerCustomAdapter(Context context, List<Trailer> movieArrayList) {
        super(context, 0, movieArrayList);
        this.movieArrayList = movieArrayList;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.trailer_item, parent, false);
        }


        String key = movieArrayList.get(position).getKey();
        TextView t = (TextView) convertView.findViewById(R.id.textoftrailer);
        t.setText(key);


        return convertView;

    }


}
