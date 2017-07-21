package com.example.learnings.collagesplacesapi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class PlacesResponse {

    @SerializedName("results")
    public ArrayList<PlaceInfo> places;
}
