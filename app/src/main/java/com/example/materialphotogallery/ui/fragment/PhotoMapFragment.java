package com.example.materialphotogallery.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PhotoMapFragment extends SupportMapFragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener{

    private View mMapView;

    public PhotoMapFragment() {}

    public static PhotoMapFragment newInstance() {
        PhotoMapFragment fragment = new PhotoMapFragment();
        Bundle args = new Bundle();
        // TODO
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMapView = super.onCreateView(inflater, container, savedInstanceState);

        return mMapView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // TODO add markers to the map


        // TODO center and zoom map to encompass markers


        // TODO set onClick listeners
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


}