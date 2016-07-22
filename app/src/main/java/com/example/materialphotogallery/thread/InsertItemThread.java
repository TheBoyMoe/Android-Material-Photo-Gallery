package com.example.materialphotogallery.thread;

import android.content.ContentValues;
import android.content.Context;
import android.os.Process;

import com.example.materialphotogallery.common.Constants;
import com.example.materialphotogallery.common.Utils;
import com.example.materialphotogallery.model.DatabaseHelper;

import timber.log.Timber;

public class InsertItemThread extends Thread{

    private ContentValues mValues;
    private Context mContext;

    public InsertItemThread(Context context, ContentValues values) {
        super();
        mValues = values;
        mContext = context;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        try {

            DatabaseHelper.getInstance(mContext).insertItem(mContext, mValues);
        } catch (Exception e) {
            Timber.e("%s: error adding item to dbase, %s", Constants.LOG_TAG, e.getMessage());
        }
        // query the dbase so as to trigger an update of the ui
        Utils.queryAllItems(mContext);
    }

}
