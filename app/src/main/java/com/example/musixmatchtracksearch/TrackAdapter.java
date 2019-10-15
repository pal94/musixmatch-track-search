package com.example.musixmatchtracksearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends ArrayAdapter<Tracks> {

    public TrackAdapter(@NonNull Context context, int resource, @NonNull List<Tracks> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Tracks track = getItem(position);
            ViewHolder viewHolder;

           if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_list,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.textViewTrack = convertView.findViewById(R.id.textView4);
                viewHolder.textViewArtist = convertView.findViewById(R.id.textViewArtistValue);
                viewHolder.textViewAlbum = convertView.findViewById(R.id.textViewAlbumValue);
                viewHolder.textViewDate = convertView.findViewById(R.id.textView10);

               convertView.setTag(viewHolder);
            } else {
               viewHolder = (ViewHolder) convertView.getTag();
            }

           viewHolder.textViewTrack.setText(track.track_name);
           viewHolder.textViewArtist.setText(track.artist_name);
           viewHolder.textViewAlbum.setText(track.album_name);
           viewHolder.textViewDate.setText(track.updated_time);

           return convertView;
   }


        public static class ViewHolder{
            TextView textViewTrack;
            TextView textViewArtist;
            TextView textViewAlbum;
            TextView textViewDate;

        }


}
