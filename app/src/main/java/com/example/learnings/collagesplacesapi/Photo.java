package com.example.learnings.collagesplacesapi;

import com.google.gson.annotations.SerializedName;


public class Photo {
    public String getPhotoRef() {
        return photoRef;
    }
    public String getWidth() {
        return width;
    }
    public String getHeight() {
        return height;
    }

    @SerializedName("photo_reference")
    private String photoRef;

    @SerializedName("width")
    private String width;

    @SerializedName("height")
    private String height;
}
