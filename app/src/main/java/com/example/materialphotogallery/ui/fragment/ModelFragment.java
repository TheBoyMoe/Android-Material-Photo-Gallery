package com.example.materialphotogallery.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.materialphotogallery.common.Utils;

/**
 * Headless fragment responsible for interacting with the database
 */
public class ModelFragment extends Fragment{

    private Context mApp = null;

    public ModelFragment() {}

    public static ModelFragment newInstance() {
        return new ModelFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mApp == null) {
            // use the application context to prevent a memory leak
            mApp = context.getApplicationContext();
            // query database on start
            new LoadItemsThread().start();
        }
    }

    // query sqlite database
    class LoadItemsThread extends Thread {

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            // query the database and post the results to the bus
            Utils.queryAllItems(mApp);
        }
    }

}
