package com.example.materialphotogallery.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.BaseFragment;

public class SettingsFragment extends BaseFragment {

    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextPlaceholder.setText(String.format("%s fragment", getString(R.string.menu_title_settings)));
    }


}
