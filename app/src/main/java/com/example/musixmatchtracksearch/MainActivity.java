package com.example.musixmatchtracksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
    String TAG = "demo";
    SeekBar sb;
    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText search;
    ListView listView;

    Tracks track;

    ArrayList<Tracks> track_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);


        search = findViewById(R.id.editText);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);

        sb = findViewById(R.id.seekBar);
        sb.setMax(25);
        sb.setMin(5);

        if(isConnected())
        {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(selectedId);

                    Log.d(TAG, "seekbar val: "+sb.getProgress()+" Radio selected "+radioButton.getText() + "search "+search.getText().toString());

                    new GetMusicData().execute("https://newsapi.org/v2/sources?apiKey=f820982a73524d00bf2c0870568c7706");


                }
            });
        }
        else
        {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
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

    private class GetMusicData extends AsyncTask<String, Void, ArrayList<Tracks>> {

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
                    JSONObject message = root.getJSONObject("message");
                    JSONObject body = message.getJSONObject("body");
                    JSONArray trackslist = body.getJSONArray("track_list");


                    for (int i = 0; i < trackslist.length(); i++) {

                        JSONObject sourceJson = trackslist.getJSONObject(i);
                        JSONObject trackobject = sourceJson.getJSONObject("track");

                        track = new Tracks();
                        track.track_name = trackobject.getString("track_name");
                        track.album_name = trackobject.getString("album_name");
                        track.artist_name = trackobject.getString("artist_name");
                        track.updated_time = trackobject.getString("updated_time");
                        track.track_share_url = trackobject.getString("track_share_url");
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

        @Override
        protected void onPostExecute(ArrayList<Tracks> tracks) {

            if (tracks.size() > 0) {
                track_list = tracks;
                Log.d("DEMO", track_list.toString());

                TrackAdapter adapter = new TrackAdapter(MainActivity.this, R.layout.track_list,track_list);
                listView.setAdapter(adapter);

            } else {
                Log.d("DEMO", "no data");
            }
        }

}
}
