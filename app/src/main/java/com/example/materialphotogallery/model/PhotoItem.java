package com.example.materialphotogallery.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class PhotoItem implements Parcelable {

    private long mId;
    private String mTitle;
    private String mDescription;
    private String mFullSizePhotoPath;
    private String mPreviewPath;
    private String mThumbnailPath;
    private int mFavourite;
    private double mLatitude;
    private double mLongitude;

    public PhotoItem() { }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getFavourite() {
        return mFavourite;
    }

    public void setFavourite(int favourite) {
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
        return String.format(Locale.ENGLISH, "Id: %d, previewPath: %s", getId(), getPreviewPath());
    }

    // impl Parcelable interface (id, title, description and previewPath only)
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDescription);
        dest.writeString(this.mFullSizePhotoPath);
        dest.writeString(this.mPreviewPath);
        dest.writeString(this.mThumbnailPath);
        dest.writeInt(this.mFavourite);
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
    }

    protected PhotoItem(Parcel in) {
        this.mId = in.readLong();
        this.mTitle = in.readString();
        this.mDescription = in.readString();
        this.mFullSizePhotoPath = in.readString();
        this.mPreviewPath = in.readString();
        this.mThumbnailPath = in.readString();
        this.mFavourite = in.readInt();
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel source) {
            return new PhotoItem(source);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };


}
