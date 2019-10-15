/*
 * Assignment - InClass07
 * Filename - Tracks.java
 * Fullname - Priya Patel & Pallav Jhaveri
 *
 * */
package com.example.musixmatchtracksearch;

public class Tracks {
    String track_name, album_name, artist_name, updated_time, track_share_url;

    @Override
    public String toString() {
        return track_name + track_share_url+album_name+artist_name+updated_time;
    }

}
