package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by yalsaadi on 5/24/2018.
 */

public class MoviesVideosRecyclerViewAdapter extends RecyclerView.Adapter<MoviesVideosRecyclerViewAdapter.VideoViewHolder> {
    private Context context;
    JSONArray jsonArrayVideosInfo;
    private VideosAdapterOnClickHandler onClickHandler;

    public MoviesVideosRecyclerViewAdapter(Context context, JSONArray jsonArrayVideosInfo, VideosAdapterOnClickHandler onClickHandler) {
        this.context = context;
        this.jsonArrayVideosInfo = jsonArrayVideosInfo;
        this.onClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_movie_item, parent, false);
        return new MoviesVideosRecyclerViewAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        JSONObject jsonObject = jsonArrayVideosInfo.optJSONObject(position);

        String videoKey = jsonObject.optString("key");
        String url = "http://img.youtube.com/vi/" + videoKey + "/0.jpg";

        Picasso.with(context)
                .load(url)
                .into(holder.imageViewVideoItem);

    }

    @Override
    public int getItemCount() {
        return jsonArrayVideosInfo.length();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewVideoItem;

        public VideoViewHolder(View itemView) {
            super(itemView);

            imageViewVideoItem = itemView.findViewById(R.id.imageViewMovieItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            onClickHandler.onClick(jsonArrayVideosInfo.optJSONObject(adapterPosition));
        }
    }

    public interface VideosAdapterOnClickHandler {
        void onClick(JSONObject jsonObjectVideoDetails);
    }
}
