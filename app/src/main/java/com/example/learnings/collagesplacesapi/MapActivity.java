package com.example.learnings.collagesplacesapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends FragmentActivity implements GoogleMap.OnMapClickListener,
        OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    final static String connectionFailed = "Connection failed";
    final static String checkFailed = "Check failed";
    final static String locationNull = "Location undefined";
    Location myLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=YOUR_API_KEY

                Location myLocation = mMap.getMyLocation();
                if (myLocation != null) {
                    String locationString = String.valueOf(myLocation.getLatitude()) +","+ String.valueOf(myLocation.getLongitude());

                    String baseUrl = "https://maps.googleapis.com/";
                    OkHttpClient client = new OkHttpClient.Builder()
                            .readTimeout(5, TimeUnit.SECONDS)
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .client(client)
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    PlacesService service = retrofit.create(PlacesService.class);
                    Call<PlacesResponse> responseCall = service.load(locationString, 500, getString(R.string.google_maps_key));
                    responseCall.enqueue(new Callback<PlacesResponse>() {
                        @Override
                        public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                            if (response.body() != null) {
                                List<PlaceInfo> places = response.body().places;
                                if (places.size() >= 4) {
                                    ArrayList<String> picUrls = new ArrayList<>();
                                    int i = 0;
                                    while (i < places.size()) {
                                        if (places.get(i).getPicUrl() != null)
                                            picUrls.add(places.get(i).getPicUrl());
                                        i++;
                                    }

                                    if (picUrls.size() >= 4) {
                                        CollageDialog dialog = new CollageDialog();
                                        Bundle b = new Bundle();
                                        b.putStringArrayList("urls", picUrls);
                                        dialog.setArguments(b);
                                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                        ft.add(dialog, "dlg");
                                        ft.commit();
                                    }
                                    else {
                                        Toast.makeText(MapActivity.this, "Not enough places with pictures found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MapActivity.this, "Not enough places found", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(MapActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PlacesResponse> call, Throwable t) {
                            Toast.makeText(MapActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(MapActivity.this, locationNull, Toast.LENGTH_SHORT).show();
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(l);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        else {
            Toast.makeText(this, checkFailed, Toast.LENGTH_SHORT).show();
        }
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionFailed, Toast.LENGTH_SHORT).show();
    }

    //TODO: delete this method
    @Override
    public void onMapClick(LatLng latLng) {
        if (latLng != null) {
            String locationString = String.valueOf(latLng.latitude) +","+ String.valueOf(latLng.longitude);

            String baseUrl = "https://maps.googleapis.com/";
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(5, TimeUnit.SECONDS)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PlacesService service = retrofit.create(PlacesService.class);
            Call<PlacesResponse> responseCall = service.load(locationString, 500, getString(R.string.google_maps_key));
            responseCall.enqueue(new Callback<PlacesResponse>() {
                @Override
                public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                    if (response.body() != null) {
                        List<PlaceInfo> places = response.body().places;
                        if (places.size() >= 4) {
                            ArrayList<String> picUrls = new ArrayList<>();
                            int i = 0;
                            while (i < places.size()) {
                                if (places.get(i).getPicUrl() != null)
                                    picUrls.add(places.get(i).getPicUrl());
                                i++;
                            }

                            if (picUrls.size() >= 4) {
                                CollageDialog dialog = new CollageDialog();
                                Bundle b = new Bundle();
                                b.putStringArrayList("urls", picUrls);
                                dialog.setArguments(b);
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.add(dialog, "dlg");
                                ft.commit();
                            }
                            else {
                                Toast.makeText(MapActivity.this, "Not enough places with pictures found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(MapActivity.this, "Not enough places found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MapActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PlacesResponse> call, Throwable t) {
                    Toast.makeText(MapActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(MapActivity.this, locationNull, Toast.LENGTH_SHORT).show();
        }
    }
}
