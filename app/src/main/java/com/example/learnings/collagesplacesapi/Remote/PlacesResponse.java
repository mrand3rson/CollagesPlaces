package com.example.learnings.collagesplacesapi.Remote;

import com.example.learnings.collagesplacesapi.Remote.PlaceInfo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class PlacesResponse {

    @SerializedName("results")
    public ArrayList<PlaceInfo> places;
}
