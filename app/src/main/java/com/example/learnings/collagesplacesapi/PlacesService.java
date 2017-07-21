package com.example.learnings.collagesplacesapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface PlacesService {
    @GET("maps/api/place/nearbysearch/json")
    Call<PlacesResponse> load(@Query("location") String location, @Query("radius") int radius, @Query("key") String key);
}