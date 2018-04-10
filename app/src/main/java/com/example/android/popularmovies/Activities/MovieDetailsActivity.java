package com.example.android.popularmovies.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity {

    TextView textViewTitle, textViewOverallRating, textViewReleaseDate, textViewOverview;
    ImageView imageViewPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        String movieDetails = intent.getStringExtra("movieDetails");

        textViewTitle = findViewById(R.id.textViewTitle);
        imageViewPoster = findViewById(R.id.imageViewMoviePoster);
        textViewOverallRating = findViewById(R.id.textViewOverallRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);

        bindMovieDetailsData(movieDetails);
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
}
