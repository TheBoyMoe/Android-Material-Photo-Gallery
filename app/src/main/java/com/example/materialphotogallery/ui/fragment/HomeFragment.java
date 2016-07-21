package com.example.materialphotogallery.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.ContractFragment;


public class HomeFragment extends ContractFragment<HomeFragment.Contract>{

    public interface Contract {
        // TODO
    }

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main, container, false);
    }

}
