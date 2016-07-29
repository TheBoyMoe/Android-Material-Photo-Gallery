package com.example.materialphotogallery.ui.fragment;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.Constants;
import com.example.materialphotogallery.model.DatabaseHelper;
import com.example.materialphotogallery.model.PhotoItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class PhotoMapFragment extends SupportMapFragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener{

    private View mMapView;
    private List<PhotoItem> mList;
    private GoogleMap mMap;
    private LatLngBounds.Builder mBuilder = new LatLngBounds.Builder();

    public PhotoMapFragment() {}

    public static PhotoMapFragment newInstance() {
        PhotoMapFragment fragment = new PhotoMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMapView = super.onCreateView(inflater, container, savedInstanceState);

        return mMapView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getMapAsync(this); // ensure that onMapReady is called
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // add markers to the map
        mMap = googleMap;
        new QueryPhotoItemsTask().execute();

        // TODO set onClick listeners
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // TODO
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO
        return false;
    }

    private void addMarkers(GoogleMap map) {

        Marker marker = null;
        for (int i = 0; i < mList.size(); i++) {
            PhotoItem item = mList.get(i);
            if (item.getLatitude() == 0.0 && item.getLongitude() == 0.0) {
                continue;
            }
            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitude(), item.getLongitude()))
                    .title(String.format(Locale.ENGLISH, "%d/%d %s",
                            i + 1, mList.size(), item.getTitle()) )
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
            );
            // add each marker to the LatLng object
            mBuilder.include(marker.getPosition());
        }

    }


    // load database items
    private class QueryPhotoItemsTask extends AsyncTask<Void, Void, List<PhotoItem>> {

        Cursor results;
        List<PhotoItem> items = new ArrayList<>();

        @Override
        protected List<PhotoItem> doInBackground(Void... voids) {
            try {
                // query the database, returning all records
                results = DatabaseHelper.getInstance(getActivity()).loadItems(getActivity());
            } catch (Exception e) {
                Timber.e("%s: error loading items from dbase, %s", Constants.LOG_TAG, e.getMessage());
            }
            if (results != null && results.moveToFirst()) {
                do {
                    PhotoItem item = new PhotoItem();
                    item.setId(results.getLong(results.getColumnIndex(Constants.PHOTO_ID)));
                    item.setTitle(results.getString(results.getColumnIndex(Constants.PHOTO_TITLE)));
                    item.setLatitude(results.getDouble(results.getColumnIndex(Constants.PHOTO_LATITUDE)));
                    item.setLongitude(results.getDouble(results.getColumnIndex(Constants.PHOTO_LONGITUDE)));
                    item.setSmallThumbPath(results.getString(results.getColumnIndex(Constants.PHOTO_SMALL_THUMB_PATH)));
                    items.add(item);
                } while(results.moveToNext());
            }

            return items;
        }

        @Override
        protected void onPostExecute(List<PhotoItem> photoItems) {
            super.onPostExecute(photoItems);
            mList = photoItems;
            Timber.i("%s: items retrieved, count: %d", Constants.LOG_TAG, mList.size());
            addMarkers(mMap);

            // center and zoom map to encompass markers
            mMapView.post(new Runnable() {
                @Override
                public void run() {
                    CameraUpdate locations = CameraUpdateFactory.newLatLngBounds(mBuilder.build(), 128);
                    mMap.moveCamera(locations);
                }
            });
        }
    }

}