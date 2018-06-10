package com.example.android.popularmovies.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapters.MoviesVideosRecyclerViewAdapter;
import com.example.android.popularmovies.BackgroundTasks.AsyncTaskFetchMovies;
import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.Data.FavouriteContract;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView textViewTitle, textViewOverallRating, textViewReleaseDate, textViewOverview;
    private ImageView imageViewPoster;
    private RecyclerView recyclerViewMoviesVideos;
    private MoviesVideosRecyclerViewAdapter moviesVideosRecyclerViewAdapter;
    private TextView textViewReviews;
    private ImageView imageViewFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        final String movieDetails = intent.getStringExtra("movieDetails");

        textViewTitle = findViewById(R.id.textViewTitle);
        imageViewPoster = findViewById(R.id.imageViewMoviePoster);
        textViewOverallRating = findViewById(R.id.textViewOverallRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        recyclerViewMoviesVideos = findViewById(R.id.recyclerViewMoviesVideos);
        textViewReviews = findViewById(R.id.textViewReviews);
        imageViewFavourite = findViewById(R.id.imageViewFavourite);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMoviesVideos.setHasFixedSize(true);
        recyclerViewMoviesVideos.setLayoutManager(linearLayoutManager);

        bindMovieDetailsData(movieDetails);

        final Cursor cursor = getContentResolver().query(FavouriteContract.FavouriteEntry.CONTENT_URI, null, FavouriteContract.FavouriteEntry.COLUMN_NAME + " = ?", new String[]{textViewTitle.getText().toString()}, null);

        if (cursor.getCount() > 0) {
            imageViewFavourite.setBackground(getResources().getDrawable(R.drawable.favourite_icon_filled));
        } else {
            imageViewFavourite.setBackground(getResources().getDrawable(R.drawable.favourite_icon_border));
        }

        JSONObject jsonObjectMovieDetails = null;
        int movieId = 0;
        try {
            jsonObjectMovieDetails = new JSONObject(movieDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObjectMovieDetails != null) {
            movieId = jsonObjectMovieDetails.optInt("id");
        }

        URL url = buildURL(String.valueOf(movieId));
        URL urlReviews = buildURLReviews(String.valueOf(movieId));

        new AsyncTaskFetchMovies(getApplicationContext(), new FetchMoviesVideoCompleteListener()).execute(url.toString());
        new AsyncTaskFetchMovies(getApplicationContext(), new FetchMoviesReviewsCompleteListener()).execute(urlReviews.toString());

        imageViewFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cursor.getCount() > 0) {
                    imageViewFavourite.setBackground(getResources().getDrawable(R.drawable.favourite_icon_border));

                    int favouriteMoviesDeleted = getContentResolver().delete(FavouriteContract.FavouriteEntry.CONTENT_URI, FavouriteContract.FavouriteEntry.COLUMN_NAME + " = ?", new String[]{textViewTitle.getText().toString()});
                    getContentResolver().delete(FavouriteContract.FavouriteEntry.CONTENT_URI, FavouriteContract.FavouriteEntry.COLUMN_DETAILS + " = ?", new String[]{movieDetails});

                    if (favouriteMoviesDeleted > 0) {
                        Toast.makeText(MovieDetailsActivity.this,"Deleted!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    imageViewFavourite.setBackground(getResources().getDrawable(R.drawable.favourite_icon_filled));
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_NAME, textViewTitle.getText().toString());
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_DETAILS, movieDetails);

                    Uri uri = getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI, contentValues);

                    if (uri != null) {
                        Toast.makeText(MovieDetailsActivity.this, "Added Successfully!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public class FetchMoviesVideoCompleteListener implements AsyncTaskFetchMovies.AsyncTaskCompleteListener<String>, MoviesVideosRecyclerViewAdapter.VideosAdapterOnClickHandler {
        @Override
        public void onTaskComplete(String result) {
            if (result.isEmpty() == false) {

                JSONObject jsonObjectResult = null;
                try {
                    jsonObjectResult = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray jsonArray = jsonObjectResult.optJSONArray("results");

                moviesVideosRecyclerViewAdapter = new MoviesVideosRecyclerViewAdapter(MovieDetailsActivity.this, jsonArray, this);
                recyclerViewMoviesVideos.setAdapter(moviesVideosRecyclerViewAdapter);
                moviesVideosRecyclerViewAdapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onClick(JSONObject jsonObjectVideoDetails) {
            String videoKey = jsonObjectVideoDetails.optString("key");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoKey)));
        }
    }

    public class FetchMoviesReviewsCompleteListener implements AsyncTaskFetchMovies.AsyncTaskCompleteListener<String> {
        @Override
        public void onTaskComplete(String result) {
            if (result.isEmpty() == false) {

                JSONObject jsonObjectResult = null;
                try {
                    jsonObjectResult = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray jsonArray = jsonObjectResult.optJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectReviewInfo = jsonArray.optJSONObject(i);
                    String authorName = jsonObjectReviewInfo.optString("author");
                    String content = jsonObjectReviewInfo.optString("content");
                    textViewReviews.append("\n" + authorName + "\n\n" + content + "\n" + "-------------");
                }

            }
        }
    }

    private void bindMovieDetailsData(String movieDetails) {
        JSONObject jsonObjectMovieDetails = null;
        try {
            jsonObjectMovieDetails = new JSONObject(movieDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObjectMovieDetails != null) {
            String title = jsonObjectMovieDetails.optString("title");
            String posterUrl = jsonObjectMovieDetails.optString("poster_path");
            String voteAverage = jsonObjectMovieDetails.optString("vote_average");
            String releaseDate = jsonObjectMovieDetails.optString("release_date");
            String overview = jsonObjectMovieDetails.optString("overview");

            textViewTitle.setText(title);
            textViewOverallRating.setText(getResources().getString(R.string.user_rating) + " " + voteAverage);
            textViewReleaseDate.setText(getResources().getString(R.string.release_date) + " " + releaseDate);
            textViewOverview.setText(overview);
            Picasso.with(getApplicationContext())
                    .load("http://image.tmdb.org/t/p/w185" + posterUrl)
                    .into(imageViewPoster);
        }
    }

    private URL buildURL(String movieId) {
        Uri.Builder builder = new Uri.Builder();
        URL url = null;


        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .appendQueryParameter("language", "en-US");


        String strUrl = builder.build().toString();
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private URL buildURLReviews(String movieId) {
        Uri.Builder builder = new Uri.Builder();
        URL url = null;


        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter("api_key", BuildConfig.API_KEY)
                .appendQueryParameter("language", "en-US");


        String strUrl = builder.build().toString();
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
