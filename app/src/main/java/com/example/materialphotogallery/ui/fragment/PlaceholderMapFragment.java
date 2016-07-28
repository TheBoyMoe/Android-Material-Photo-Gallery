package com.example.materialphotogallery.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.materialphotogallery.R;

public class PlaceholderMapFragment extends Fragment{

    public PlaceholderMapFragment(){}

    public static PlaceholderMapFragment newInstance() {
        return new PlaceholderMapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_placeholder, container, false);
        TextView tv = (TextView) view.findViewById(R.id.text_placeholder);
        tv.setText(getString(R.string.maps_not_supported));

        // hide the toolbar shadow on devices API 21+
        View toolbarShadow = view.findViewById(R.id.toolbar_shadow);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbarShadow.setVisibility(View.GONE);
        }
        return view;
    }


}
