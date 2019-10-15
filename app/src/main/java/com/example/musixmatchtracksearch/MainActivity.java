/*
* Assignment - InClass07
* Filename - MainActivity.java
* Fullname - Priya Patel & Pallav Jhaveri
*
* */
package com.example.musixmatchtracksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String TAG = "demo";
    SeekBar sb;
    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText search;
    TextView sb_text;
    ListView listView;
    String sortby;
    ProgressDialog progressDialog;

    Tracks track;

    ArrayList<Tracks> track_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);


        Log.d(TAG, "onCreate: ");
        listView = findViewById(R.id.listView);


        search = findViewById(R.id.editText);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        sb_text = findViewById(R.id.tvSBVal);

        sb = findViewById(R.id.seekBar);
        sb.setMax(25);
        sb.setMin(5);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sb_text.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        if(isConnected())
        {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (search.getText().toString().matches("")) {
                        if(track_list!=null || track_list.size()!=0){
                            track_list.clear();
                            listView.setAdapter(null);
                        }
                        Toast.makeText(MainActivity.this, "Please enter some value", Toast.LENGTH_SHORT).show();
                    } else {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);

                        Log.d(TAG, "seekbar val: " + sb.getProgress() + " Radio selected " + radioButton.getText() + "search " + search.getText().toString());

                        if (radioButton.getText().equals("Track Rating")) {
                            sortby = "&s_track_rating=desc";
                        }

                        if (radioButton.getText().equals("Artist Rating")) {
                            sortby = "&s_artist_rating=desc";
                        }
                        new GetMusicData().execute("http://api.musixmatch.com/ws/1.1/track.search?apikey=f556b79ce385b8b5b25251e934e15649&q=" + search.getText().toString() + "&page_size=" + sb.getProgress() + sortby);
                        Log.d(TAG, "onClick: " + "http://api.musixmatch.com/ws/1.1/track.search?apikey=f556b79ce385b8b5b25251e934e15649&q=" + search.getText().toString() + "&page_size=" + sb.getProgress() + sortby);
                    }
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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final ArrayList<Tracks> tracks) {
            progressDialog.dismiss();
            if (tracks.size() > 0) {

                track_list = tracks;
                Log.d("DEMO", track_list.toString());

                TrackAdapter adapter = new TrackAdapter(MainActivity.this, R.layout.track_list,track_list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Tracks t =tracks.get(position);
                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.putExtra("URL", t.track_share_url);
                        i.setData(Uri.parse(t.track_share_url));
                        startActivity(i);
                    }
                });

            } else {
                Log.d("DEMO", "no data");
            }
        }

}
}
