package com.example.materialphotogallery.custom;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.Constants;
import com.example.materialphotogallery.common.Utils;
import com.example.materialphotogallery.thread.InsertItemThread;

public class CustomDialogFragment extends DialogFragment implements View.OnClickListener{

    private EditText mTitle;
    private String mFullFilePath;

    public CustomDialogFragment() {}

    public static CustomDialogFragment newInstance(String fullFilePath) {
        CustomDialogFragment fragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PHOTO_FILE_PATH, fullFilePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog, container, false);
        getDialog().setTitle(R.string.dialog_title);
        mFullFilePath = getArguments().getString(Constants.PHOTO_FILE_PATH);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitle = (EditText) view.findViewById(R.id.dialog_user_input);
        TextView positiveBtn = (TextView) view.findViewById(R.id.dialog_positive_btn);
        TextView negativeBtn = (TextView) view.findViewById(R.id.dialog_negative_btn);
        positiveBtn.setOnClickListener(this);
        negativeBtn.setOnClickListener(this);
        mTitle.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    @Override
    public void onClick(View view) {
        String titleText = mTitle.getText().toString();
        switch (view.getId()) {
            case R.id.dialog_positive_btn:
                saveItemToDisk(titleText);
                break;
            case R.id.dialog_negative_btn:
                saveItemToDisk("");
                break;
        }
        dismiss();
    }

    private void saveItemToDisk(String title) {
        String description = "";
        // generate scaled versions of the photo
        String previewPath = Utils.generatePreviewImage(mFullFilePath, 1400, 1400); //FIXME
        String thumbnailPath = Utils.generateThumbnailImage(mFullFilePath, 300, 300);
        // insert record into database
        ContentValues cv = Utils.setContentValues(
                Utils.generateCustomId(),
                title,
                description,
                mFullFilePath,
                previewPath,
                thumbnailPath,
                0 // sqlite does not accept booleans, use 0 for false, 1 for true
        );
        new InsertItemThread(getActivity(), cv).start();
    }

}
