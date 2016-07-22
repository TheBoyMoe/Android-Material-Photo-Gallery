package com.example.materialphotogallery.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.Constants;
import com.example.materialphotogallery.common.ContractFragment;
import com.example.materialphotogallery.event.ModelLoadedEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;


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
        View view = inflater.inflate(R.layout.content_main, container, false);
        TextView dummyContent = (TextView) view.findViewById(R.id.dummy_content);
        dummyContent.setText(R.string.dummy_text);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        // showHideEmpty();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(ModelLoadedEvent event) {
        // DEBUG
        Cursor cursor = event.getModel();
        if (cursor != null) {
            while(cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(Constants.PHOTO_ID));
                String filePath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_FILE_PATH));
                Timber.i("%s: id: %d, filePath: %s", Constants.LOG_TAG, id, filePath);
            }
        }

        // pass the retrieved cursor to the adapter

        // TODO instantiate the adapter
        // mAdapter.changeCursor(event.getModel());

        // TODO hide recycler view when empty
        // showHideEmpty();
    }

}
