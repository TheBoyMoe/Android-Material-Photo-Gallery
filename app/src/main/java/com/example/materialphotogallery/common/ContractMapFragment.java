package com.example.materialphotogallery.common;


import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.SupportMapFragment;

public class ContractMapFragment<T> extends SupportMapFragment{


    private T mContract;

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            mContract = (T) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    activity.getClass().getSimpleName()
                            + " does not implement contract interface for "
                            + getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContract = null;
    }

    public final T getContract() {
        return mContract;
    }

}
