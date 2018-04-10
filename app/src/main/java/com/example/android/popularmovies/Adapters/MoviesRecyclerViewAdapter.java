package com.example.android.popularmovies.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by yalsaadi on 3/31/2018.
 */

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder> {
    private Context context;
    private JSONArray jsonArrayMoviesInfo;
    private MoviesAdapterOnClickHandler onClickHandler;

    public MoviesRecyclerViewAdapter(Context context, JSONArray jsonArrayMoviesInfo, MoviesAdapterOnClickHandler onClickHandler) {
        this.context = context;
        this.jsonArrayMoviesInfo = jsonArrayMoviesInfo;
        this.onClickHandler = onClickHandler;
    }

    public interface MoviesAdapterOnClickHandler
    {
        void onClick(JSONObject jsonObjectMovieDetails);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        JSONObject jsonObject = jsonArrayMoviesInfo.optJSONObject(position);
        String strPosterPath = jsonObject.optString("poster_path");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        holder.imageViewMovieItem.getLayoutParams().width = displayMetrics.widthPixels / 2;
        holder.imageViewMovieItem.getLayoutParams().height = displayMetrics.heightPixels / 2;

        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185" + strPosterPath)
                .fit()
                .centerCrop()
                .into(holder.imageViewMovieItem);
    }

    @Override
    public int getItemCount() {
        return jsonArrayMoviesInfo.length();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageViewMovieItem;

        public MovieViewHolder(View itemView) {
            super(itemView);

            imageViewMovieItem = itemView.findViewById(R.id.imageViewMovieItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            onClickHandler.onClick(jsonArrayMoviesInfo.optJSONObject(adapterPosition));
        }
    }

}
