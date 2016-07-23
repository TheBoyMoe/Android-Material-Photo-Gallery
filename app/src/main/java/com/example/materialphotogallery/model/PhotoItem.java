package com.example.materialphotogallery.model;

import java.util.Locale;

public class PhotoItem {

    private long mId;
    private String mFullSizePhotoPath;
    private String mPreviewPath;
    private String mThumbnailPath;
    private boolean mFavourite;

    public boolean isFavourite() {
        return mFavourite;
    }

    public void setFavourite(boolean favourite) {
        mFavourite = favourite;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getFullSizePhotoPath() {
        return mFullSizePhotoPath;
    }

    public void setFullSizePhotoPath(String fullSizePhotoPath) {
        mFullSizePhotoPath = fullSizePhotoPath;
    }

    public String getPreviewPath() {
        return mPreviewPath;
    }

    public void setPreviewPath(String previewPath) {
        mPreviewPath = previewPath;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        mThumbnailPath = thumbnailPath;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Id: %d, path: %s", getId(), getFullSizePhotoPath());
    }
}
