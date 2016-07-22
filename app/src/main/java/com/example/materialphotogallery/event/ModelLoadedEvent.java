package com.example.materialphotogallery.event;

import android.database.Cursor;

public class ModelLoadedEvent extends BaseEvent{

    private Cursor mModel;

    public ModelLoadedEvent(Cursor model) {
        mModel = model;
    }

    public Cursor getModel() {
        return mModel;
    }

}
