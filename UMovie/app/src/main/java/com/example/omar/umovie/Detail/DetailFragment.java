package com.example.omar.umovie.Detail;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omar.umovie.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    String id;
    ArrayList<String> keylist;
    List<Trailer> TrailerWord;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        Bundle bundle = getArguments();
        String title = bundle.getString("title");
        String posterurl = bundle.getString("imagePosterURL");
        String release_date = bundle.getString("RealesDate");
        String overview = bundle.getString("overView");
        int vote_count = (int) bundle.getDouble("vote_average", 100);
        id = bundle.getString("id");



        keylist = new ArrayList<>();

        ImageView posterImage = (ImageView) v.findViewById(R.id.posterofdetail);
        Picasso.with(getActivity()).load(posterurl).into(posterImage);

        TextView titleView = (TextView) v.findViewById(R.id.title);
        titleView.setText(title);

        TextView detailView = (TextView) v.findViewById(R.id.description);
        detailView.setText("Description : " + overview);

        TextView RealseView = (TextView) v.findViewById(R.id.release_date);
        RealseView.setText("Release Date : " + release_date);

        TextView voteView = (TextView) v.findViewById(R.id.vote_count);
        voteView.setText("Vote counts : " + vote_count);


        new FetchTrailer().execute();

        new FetchReview().execute();


        final Button favBtn = (Button) v.findViewById(R.id.favBtn);


        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addId(Integer.parseInt(id));
            }
        });


        return v;
    }


    public void addId(int idValue){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor editor = prefs.edit();
        String ID = prefs.getString("idofmovie" , "empty");


        if( ID == "empty"){
            ID =idValue + ",";
            editor.putString("idofmovie" , ID).commit();
            System.out.println(ID + "IDss");
            Toast.makeText(getActivity(),"Done!"  , Toast.LENGTH_SHORT).show();
        }else {

            if(!(prefs.getString("idofmovie", "empty").contains(idValue+""))){
                ID +=idValue+",";
                Toast.makeText(getActivity(),"Done!"  , Toast.LENGTH_SHORT).show();
                editor.putString("idofmovie" , ID).commit();

            }else{
                Toast.makeText(getActivity(),"movie is already Exist"  , Toast.LENGTH_SHORT).show();;
            }
        }
        System.out.println(ID);
    }


    // asynctask for fetch trailer data

    public class FetchTrailer extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(String... params) {

            // trying to connect to the api of movie trialer
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(id)
                        .appendPath("videos")
                        .appendQueryParameter("api_key", "774c9627467113733738773c9c96c8c8").build();

                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // input stream is null and will return null cause no stream
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // append json lines to buffer
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // the buffer is empty and will return null
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            } finally {

                // disconnect the api connection after making process and get json
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                // close the reader
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            try {

                return getWeatherDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        /* Start method of Parsing JSON */

        private List<Trailer> getWeatherDataFromJson(String forecastJsonStr) throws JSONException {
            TrailerWord = new ArrayList<>();

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray Trailerarray = forecastJson.getJSONArray("results");

            for (int i = 0; i < Trailerarray.length(); i++) {
                JSONObject movieforecast = Trailerarray.getJSONObject(i);
                String Key = movieforecast.getString("key");
                keylist.add(Key);
            }
            System.out.println(keylist.size());
            if(keylist !=null)
            for (int i = 0; i < keylist.size(); i++) {
                TrailerWord.add(new Trailer(i + 1));

            }

            return TrailerWord;

        }

        /* End method of Parsing JSON */


        @Override
        protected void onPostExecute(List<Trailer> TrailerArrayList) {

            trailerCustomAdapter customAdapter = new trailerCustomAdapter(getActivity(), TrailerArrayList);
            ListView listViewofTrailer  = (ListView) getView().findViewById(R.id.trailerListView);
            listViewofTrailer.setAdapter(customAdapter);
            listViewofTrailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch")
                            .buildUpon()
                            .appendQueryParameter("v", keylist.get(i))
                            .build()));
                }
            });

        }


    }

    // asynctask for fetch review data
    public class FetchReview extends AsyncTask<String, Void, List<review>> {

        @Override
        protected List<review> doInBackground(String... params) {

            // trying to connect to the api of movie review
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(id)
                        .appendPath("reviews")
                        .appendQueryParameter("api_key", "774c9627467113733738773c9c96c8c8").build();

                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // input stream is null and will return null cause no stream
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // append json lines to buffer
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // the buffer is empty and will return null
                    return null;
                }
                forecastJsonStr = buffer.toString();

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            } finally {

                // disconnect the api connection after making process and get json
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                // close the reader
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forecastJsonStr);
            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
            return null;
        }

        /* Start method of Parsing JSON */

        private List<review> getWeatherDataFromJson(String forecastJsonStr) throws JSONException {
            List<review> reviewList = new ArrayList<>();
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray reviewarray = forecastJson.getJSONArray("results");

            for (int i = 0; i < reviewarray.length(); i++) {
                JSONObject movieforecast = reviewarray.getJSONObject(i);
                String author = movieforecast.getString("author");
                String content = movieforecast.getString("content");
                reviewList.add(new review(author , content));
                System.out.println("ok bye" + author + content);
            }
            return reviewList;
        }

        /* End method of Parsing JSON */


        @Override
        protected void onPostExecute(List<review> reviewArrayList) {
            try {

                reviewCustomAdapter customAdapter = new reviewCustomAdapter(getActivity(), reviewArrayList);
                ListView listViewofreview  = (ListView) getView().findViewById(R.id.Review_listView);
                listViewofreview.setAdapter(customAdapter);

            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }


}

