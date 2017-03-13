package com.example.omar.umovie.main;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.omar.umovie.Listener;
import com.example.omar.umovie.R;
import com.example.omar.umovie.setting;

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
import java.util.HashSet;
import java.util.Set;

/**
 * this is fragment of main Actvivty , it hold images of movies
 */
public class MainFragment extends Fragment {

    CustomAdapter customAdapter;
    Listener listener;
    String sortType;
    String FavoriteIDS = null;
    GridView favGridView;
    ArrayList<movie> movieArrayLists = new ArrayList<>();

    public void setFilmListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    /* start method for popup menu */

    // it handles Popup menu for oppening setting actvity

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.setting_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.setting) {
            Intent intent = new Intent(getActivity(), setting.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* End method for hidden menu */


    // it made for execute subclass of asynctask "FetchForeCast" automatically when app opened & load data from movie API
    @Override
    public void onStart() {
        super.onStart();

        // shared prefrence to get sirt Type
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortType = sharedPrefs.getString(
                getString(R.string.key),
                getString(R.string.defaultvalue));


        FavoriteIDS = sharedPrefs.getString("idofmovie", "no data");


        if (sortType.equals("favorite")) {

            new FetchTopRated().execute();
            new FetchPopular().execute();
        } else {
            new FetchForeCast().execute();
        }

    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // for attach layout of fragment to fragment.java
        View v = inflater.inflate(R.layout.gridview_fragment, container, false);


        return v;

    }

    // asynctask for fetch movie data
    public class FetchForeCast extends AsyncTask<String, Void, ArrayList<movie>> {

        private final String LOG_TAG = FetchForeCast.class.getSimpleName();


        @Override
        protected ArrayList<movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(sortType)
                        .appendQueryParameter("api_key", "774c9627467113733738773c9c96c8c8").build();

                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                forecastJsonStr = buffer.toString();


            } catch (Exception e) {

                Log.e(LOG_TAG, "Error ", e);
                System.out.println(e.getMessage());

                // If the code didn't successfully get the movie data, there's no point in attemping to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                try {
                    return getWeatherDataFromJson(forecastJsonStr);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            return null;
        }


        /* Start code of Parsing JSON */

        private ArrayList<movie> getWeatherDataFromJson(String forecastJsonStr) throws JSONException {

            ArrayList<movie> arrayListofMovies = new ArrayList<>();

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieforecast = movieArray.getJSONObject(i);
                String title = movieforecast.getString("title");
                String posterurl = "http://image.tmdb.org/t/p/w185/" + movieforecast.getString("poster_path");
                String overview = movieforecast.getString("overview");
                String release_date = movieforecast.getString("release_date");
                Double vote_count = movieforecast.getDouble("vote_count");
                String id = movieforecast.getString("id");

                System.out.println("release date" + release_date);

                arrayListofMovies.add(new movie(title, posterurl, overview, release_date, vote_count, id));
            }

            return arrayListofMovies;

        }

        /* End code of Parsing JSON */


        @Override
        protected void onPostExecute(final ArrayList<movie> imageArrayList) {

            try {
                GridView mainGrid = (GridView) getView().findViewById(R.id.gridView);

                if (imageArrayList != null) {
                    customAdapter = new CustomAdapter(getContext(), imageArrayList);
                }
                mainGrid.setAdapter(customAdapter);

                mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (listener != null) {
                            listener.setData(imageArrayList.get(position).getTitle(),
                                    imageArrayList.get(position).getImagePosterURL(),
                                    imageArrayList.get(position).getOverView(),
                                    imageArrayList.get(position).getRealesDate(),
                                    imageArrayList.get(position).getVote_average(),
                                    imageArrayList.get(position).getId());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (imageArrayList == null) {
                    Toast.makeText(getActivity(), "sorry there is no internet ! ", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    public class FetchTopRated extends AsyncTask<String, Void, ArrayList<movie>> {

        private final String LOG_TAG = FetchTopRated.class.getSimpleName();


        @Override
        protected ArrayList<movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try {
                //http://api.themoviedb.org/3/movie/popular?api_key=774c9627467113733738773c9c96c8c8
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("top_rated")
                        .appendQueryParameter("api_key", "774c9627467113733738773c9c96c8c8").build();

                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();


            } catch (Exception e) {

                Log.e(LOG_TAG, "Error ", e);
                System.out.println(e.getMessage());

                // If the code didn't successfully get the movie data, there's no point in attemping to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                try {
                    return getWeatherDataFromJson(forecastJsonStr);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            return null;
        }


        /* Start code of Parsing JSON */

        private ArrayList<movie> getWeatherDataFromJson(String forecastJsonStr) throws JSONException {

            ArrayList<movie> arrayListofMovies = new ArrayList<>();

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieforecast = movieArray.getJSONObject(i);
                String id = movieforecast.getString("id");
                if (FavoriteIDS.contains(id)) {
                    String title = movieforecast.getString("title");
                    String posterurl = "http://image.tmdb.org/t/p/w185/" + movieforecast.getString("poster_path");
                    String overview = movieforecast.getString("overview");
                    String release_date = movieforecast.getString("release_date");
                    Double vote_count = movieforecast.getDouble("vote_count");

                    arrayListofMovies.add(new movie(title, posterurl, overview, release_date, vote_count, id));
                }

            }

            return arrayListofMovies;

        }

        /* End code of Parsing JSON */


        @Override
        protected void onPostExecute(final ArrayList<movie> movieArrayList) {

            try {
                movieArrayLists = movieArrayList;
                favGridView = (GridView) getView().findViewById(R.id.gridView);
                if (movieArrayLists != null) {
                    customAdapter = new CustomAdapter(getContext(), movieArrayLists);
                }
                favGridView.setAdapter(customAdapter);

                favGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        listener.setData(movieArrayLists.get(position).getTitle(),
                                movieArrayLists.get(position).getImagePosterURL(),
                                movieArrayLists.get(position).getOverView(),
                                movieArrayLists.get(position).getRealesDate(),
                                movieArrayLists.get(position).getVote_average(),
                                movieArrayLists.get(position).getId());
                    }
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }


    }

    public class FetchPopular extends AsyncTask<String, Void, ArrayList<movie>> {

        private final String LOG_TAG = FetchForeCast.class.getSimpleName();


        @Override
        protected ArrayList<movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("popular")
                        .appendQueryParameter("api_key", "774c9627467113733738773c9c96c8c8").build();

                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();


            } catch (Exception e) {

                Log.e(LOG_TAG, "Error ", e);
                System.out.println(e.getMessage());

                // If the code didn't successfully get the movie data, there's no point in attemping to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                try {
                    return getWeatherDataFromJson(forecastJsonStr);
                } catch (Exception e) {
                    System.out.println(e.getMessage());

                }
            }

            return null;
        }


        /* Start code of Parsing JSON */

        private ArrayList<movie> getWeatherDataFromJson(String forecastJsonStr) throws JSONException {

            ArrayList<movie> arrayListofMovies = new ArrayList<>();

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieforecast = movieArray.getJSONObject(i);
                String id = movieforecast.getString("id");
                if (FavoriteIDS.contains(id)) {
                    String title = movieforecast.getString("title");
                    String posterurl = "http://image.tmdb.org/t/p/w185/" + movieforecast.getString("poster_path");
                    String overview = movieforecast.getString("overview");
                    String release_date = movieforecast.getString("release_date");
                    Double vote_count = movieforecast.getDouble("vote_count");

                    arrayListofMovies.add(new movie(title, posterurl, overview, release_date, vote_count, id));
                }
            }

            return arrayListofMovies;

        }

        /* End code of Parsing JSON */


        @Override
        protected void onPostExecute(final ArrayList<movie> movieArrayList) {

            try {
                favGridView = (GridView) getView().findViewById(R.id.gridView);
                movieArrayLists.addAll(movieArrayList);

                // create HashSet to remove Duplicate elements
                Set<movie> hashSet = new HashSet<>();
                hashSet.addAll(movieArrayList);
                hashSet.clear();
                movieArrayList.addAll(hashSet);

                if (movieArrayLists != null) {
                    customAdapter = new CustomAdapter(getContext(), movieArrayLists);
                }
                favGridView.setAdapter(customAdapter);

                favGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        listener.setData(movieArrayLists.get(position).getTitle(),
                                movieArrayLists.get(position).getImagePosterURL(),
                                movieArrayLists.get(position).getOverView(),
                                movieArrayLists.get(position).getRealesDate(),
                                movieArrayLists.get(position).getVote_average(),
                                movieArrayLists.get(position).getId());
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (movieArrayLists == null)
                    Toast.makeText(getActivity(), "sorry there is no internet ! ", Toast.LENGTH_SHORT).show();
            }

        }


    }


}
