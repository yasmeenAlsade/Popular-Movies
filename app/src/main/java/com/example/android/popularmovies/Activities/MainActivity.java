package com.example.android.popularmovies.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


import com.example.android.popularmovies.Adapters.MoviesRecyclerViewAdapter;
import com.example.android.popularmovies.BackgroundTasks.AsyncTaskFetchMovies;
import com.example.android.popularmovies.R;


public class MainActivity extends AppCompatActivity implements MoviesRecyclerViewAdapter.MoviesAdapterOnClickHandler {


    private RecyclerView recyclerViewMovies;
    private JSONArray jsonArrayMoviesInfo;
    private MoviesRecyclerViewAdapter moviesRecyclerViewAdapter;
    private boolean isScrolling = false;
    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerViewMovies = findViewById(R.id.recyclerViewMovies);


        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerViewMovies.setHasFixedSize(true);
        recyclerViewMovies.setLayoutManager(gridLayoutManager);


        jsonArrayMoviesInfo = new JSONArray();
        moviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(MainActivity.this, jsonArrayMoviesInfo, this);
        recyclerViewMovies.setAdapter(moviesRecyclerViewAdapter);


        recyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentItems = gridLayoutManager.getChildCount();
                int totalItems = gridLayoutManager.getItemCount();
                int scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();
                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    currentPage++;
                    if (isInternetConnectionExist(getApplicationContext()) == true) {
                        setUpSharedPreferencesSettings(currentPage);
                    } else {
                        Toast.makeText(getApplicationContext(), "Check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (isInternetConnectionExist(getApplicationContext()) == true) {
            setUpSharedPreferencesSettings(currentPage);
        } else {
            Toast.makeText(getApplicationContext(), "Check your internet connection!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
        }


        return true;
    }

    public class FetchMoviesTaskCompleteListener implements AsyncTaskFetchMovies.AsyncTaskCompleteListener<String> {
        @Override
        public void onTaskComplete(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObjectMovieInfo = new JSONObject(result);
                    JSONArray moreMoviesInfo = jsonObjectMovieInfo.optJSONArray("results");


                    if (jsonArrayMoviesInfo == null) {
                        jsonArrayMoviesInfo = moreMoviesInfo;
                    } else {
                        for (int i = 0; i < moreMoviesInfo.length(); i++) {
                            jsonArrayMoviesInfo.put(moreMoviesInfo.get(i));
                        }
                    }


                    moviesRecyclerViewAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setUpSharedPreferencesSettings(int currentPage) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        if (sharedPreferences.getString(getString(R.string.pref_movie_display_options_key), getString(R.string.pref_movie_display_options_value_popular)).equals("popular")) {
            new AsyncTaskFetchMovies(getApplicationContext(), new FetchMoviesTaskCompleteListener()).execute(String.valueOf(currentPage), "popular", jsonArrayMoviesInfo.toString());
        } else if (sharedPreferences.getString(getString(R.string.pref_movie_display_options_key), getString(R.string.pref_movie_display_options_value_top_rated)).equals("topRated")) {
            new AsyncTaskFetchMovies(getApplicationContext(), new FetchMoviesTaskCompleteListener()).execute(String.valueOf(currentPage), "top_rated", jsonArrayMoviesInfo.toString());
        }
    }


    @Override
    public void onClick(JSONObject jsonObjectMovieDetails) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra("movieDetails", jsonObjectMovieDetails.toString());
        startActivity(intent);
    }


    private boolean isInternetConnectionExist(Context context) {
        NetworkInfo activeNetworkInfo = getNetworkInfo(context);


        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = null;
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
        }


        return null;
    }
}


