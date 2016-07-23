package com.example.materialphotogallery.ui.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.ContractFragment;

public class FavouriteFragment extends ContractFragment<FavouriteFragment.Contract>{

    public interface Contract {
        // TODO
    }

    public FavouriteFragment(){}

    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_placeholder, container, false);
        TextView tv = (TextView) view.findViewById(R.id.text_placeholder);
        tv.setText(String.format("%s fragment", getString(R.string.menu_title_favourite)));

        // hide the toolbar shadow on devices API 21+
        View toolbarShadow = view.findViewById(R.id.toolbar_shadow);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbarShadow.setVisibility(View.GONE);
        }

        return view;
    }
}
