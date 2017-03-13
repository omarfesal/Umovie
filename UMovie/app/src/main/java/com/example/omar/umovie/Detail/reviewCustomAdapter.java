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
 * Created by omar on 01/12/2016.
 */
public class reviewCustomAdapter extends ArrayAdapter {
    private List<review> reviewArrayList;
    Context context;

    reviewCustomAdapter(Context context, List<review> reviewArrayList) {
        super(context, 0, reviewArrayList);
        this.reviewArrayList = reviewArrayList;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.review_item, parent, false);
        }


        String author = reviewArrayList.get(position).getAuthor();
        String content = reviewArrayList.get(position).getContent();

        TextView authorTexrt = (TextView) convertView.findViewById(R.id.author);
        TextView contentText = (TextView) convertView.findViewById(R.id.review);

        authorTexrt.setText(author);
        contentText.setText(content);


        return convertView;

    }



}
