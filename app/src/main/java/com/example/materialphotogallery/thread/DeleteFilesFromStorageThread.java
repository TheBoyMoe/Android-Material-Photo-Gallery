package com.example.materialphotogallery.thread;


import android.content.Context;
import android.database.Cursor;
import android.os.Process;

import com.example.materialphotogallery.common.Constants;
import com.example.materialphotogallery.model.DatabaseHelper;

import java.io.File;

import timber.log.Timber;

public class DeleteFilesFromStorageThread extends Thread{

    private Context mContext;
    private String[] mItems;

    public DeleteFilesFromStorageThread(Context context, String[] items) {
        mContext = context;
        mItems = items;
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Cursor cursor = null;
        try {
            cursor = DatabaseHelper.getInstance(mContext).loadItems(mContext);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(Constants.PHOTO_ID));
                    String filePath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_FILE_PATH));
                    String previewPath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_PREVIEW_PATH));
                    String thumbnailPath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_THUMBNAIL_PATH));
                    for (String mItem : mItems) {
                        if (mItem.equals(String.valueOf(id))) {
                            if (filePath != null && !filePath.isEmpty()) {
                                Timber.i("%s: filePath: %s", Constants.LOG_TAG, filePath);
                                deleteFile(filePath);
                            }
                            if (previewPath != null && !previewPath.isEmpty()) {
                                Timber.i("%s: previewPath: %s", Constants.LOG_TAG, previewPath);
                                deleteFile(previewPath);
                            }
                            if (thumbnailPath != null && !thumbnailPath.isEmpty()) {
                                Timber.i("%s: thumbnailPath: %s", Constants.LOG_TAG, thumbnailPath);
                                deleteFile(thumbnailPath);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Timber.e("%s: error deleting file from external storage, %s", Constants.LOG_TAG, e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteFile(String filePath) {
        File temp = new File(filePath);
        temp.delete();
    }

}
