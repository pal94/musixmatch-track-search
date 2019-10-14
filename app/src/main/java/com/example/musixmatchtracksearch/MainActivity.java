package com.example.musixmatchtracksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Tracks track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isConnected())
        {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            //new GetAsyncData().execute("https://newsapi.org/v2/sources?apiKey=f820982a73524d00bf2c0870568c7706");
        }
        else
        {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
        }


    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private class GetMusicData extends AsyncTask<String, Void, ArrayList<Tracks>>{

        @Override
        protected ArrayList<Tracks> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            ArrayList<Tracks> result = new ArrayList<>();
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray sources = root.getJSONArray("track");

                    for (int i=0;i<sources.length();i++) {

                        JSONObject sourceJson = sources.getJSONObject(i);

                        track = new Tracks();
                        track.track_name=sourceJson.getString("track_name");
                        track.album_name=sourceJson.getString("album_name");
                        track.artist_name=sourceJson.getString("artist_name");
                        track.updated_time=sourceJson.getString("updated_time");
                        track.track_share_url=sourceJson.getString("track_share_url");
                        result.add(track);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
        }

}
