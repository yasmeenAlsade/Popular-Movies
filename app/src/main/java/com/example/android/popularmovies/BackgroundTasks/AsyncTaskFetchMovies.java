package com.example.android.popularmovies.BackgroundTasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by yalsaadi on 4/10/2018.
 */

public class AsyncTaskFetchMovies extends AsyncTask<String, Void, String> {


    private Context context = null;
    private AsyncTaskCompleteListener<String> asyncTaskCompleteListener;


    public AsyncTaskFetchMovies(Context context, AsyncTaskCompleteListener<String> asyncTaskCompleteListener) {

        this.context = context;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
    }


    @Override
    protected String doInBackground(String... strings) {


        StringBuilder result = new StringBuilder();
        String line = "";
        URL url = null;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        if (url != null) {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }


        return result.toString();
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        asyncTaskCompleteListener.onTaskComplete(s);
    }

    public interface AsyncTaskCompleteListener<T> {
        public void onTaskComplete(T result);
    }
}
