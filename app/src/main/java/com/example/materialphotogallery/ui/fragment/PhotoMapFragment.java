package com.example.materialphotogallery.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.ContractFragment;

public class PhotoMapFragment extends ContractFragment<PhotoMapFragment.Contract>{

    public interface Contract {
        // TODO
    }

    public PhotoMapFragment(){}

    public static PhotoMapFragment newInstance() {
        return new PhotoMapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_placeholder, container, false);
        TextView tv = (TextView) view.findViewById(R.id.text_placeholder);
        tv.setText(String.format("%s fragment", getString(R.string.menu_title_map)));

        return view;
    }
}
