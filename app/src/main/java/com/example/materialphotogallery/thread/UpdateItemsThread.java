package com.example.materialphotogallery.thread;

import android.content.ContentValues;
import android.content.Context;
import android.os.Process;

import com.example.materialphotogallery.common.Constants;
import com.example.materialphotogallery.model.DatabaseHelper;

import timber.log.Timber;

public class UpdateItemsThread extends Thread{

    private ContentValues[] mValues;
    private Context mContext;

    public UpdateItemsThread(Context context, ContentValues[] values) {
        mContext = context;
        mValues = values;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        try {
            DatabaseHelper.getInstance(mContext).updateItems(mContext, mValues);
        } catch (Exception e) {
            Timber.e("%s: error updating records in the database, %s", Constants.LOG_TAG, e.getMessage());
        }

    }
}
