package com.example.learnings.collagesplacesapi.Remote;

import com.example.learnings.collagesplacesapi.Remote.Photo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlaceInfo {

    public String getPlaceName() {
        return placeName;
    }
    public String getPicUrl() {
        if (photos != null)
            return photos.get(0).getPhotoRef();
        return null;
    }
    public Photo getPhotoInfo() {
        if (photos != null)
            return photos.get(0);
        return null;
    }

    @SerializedName("name")
    private String placeName;

    @SerializedName("photos")
    private ArrayList<Photo> photos;
}
