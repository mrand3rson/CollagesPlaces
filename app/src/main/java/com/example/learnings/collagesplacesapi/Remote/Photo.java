package com.example.learnings.collagesplacesapi.Remote;

import com.google.gson.annotations.SerializedName;


class Photo {
    String getPhotoRef() {
        return photoRef;
    }
    String getWidth() {
        return width;
    }
    String getHeight() {
        return height;
    }

    @SerializedName("photo_reference")
    private String photoRef;

    @SerializedName("width")
    private String width;

    @SerializedName("height")
    private String height;
}
