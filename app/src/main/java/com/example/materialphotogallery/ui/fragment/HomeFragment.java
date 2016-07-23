package com.example.materialphotogallery.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.Constants;
import com.example.materialphotogallery.common.ContractFragment;
import com.example.materialphotogallery.custom.CustomImageView;
import com.example.materialphotogallery.custom.CustomItemDecoration;
import com.example.materialphotogallery.custom.CustomMultiChoiceCursorRecyclerViewAdapter;
import com.example.materialphotogallery.custom.CustomRecyclerView;
import com.example.materialphotogallery.custom.MultiChoiceModeListener;
import com.example.materialphotogallery.event.ModelLoadedEvent;
import com.example.materialphotogallery.thread.DeleteFilesFromStorageThread;
import com.example.materialphotogallery.thread.DeleteItemsThread;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class HomeFragment extends ContractFragment<HomeFragment.Contract>
        implements MultiChoiceModeListener{


    // MultiChoiceModeListener impl
    @Override
    public void onItemSelectionChanged(ActionMode mode, int position, boolean selected) {
        mode.setTitle(mAdapter.getSelectedCount() + " selected");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, MenuInflater inflater, Menu menu) {
        inflater.inflate(R.menu.menu_context_action_bar, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode() {
        // no-op
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        final SparseBooleanArray selectedItems = mAdapter.getSelectedPositions();
        switch (item.getItemId()) {
            case R.id.action_delete:
                new MaterialDialog.Builder(getActivity())
                    .title("Delete selection")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Cursor cursor = mAdapter.getCursor();
                            if (cursor != null) {
                                String[] selectedIds = getPhotosForDeletion(cursor, selectedItems);
                                // delete files from external storage
                                new DeleteFilesFromStorageThread(getActivity(), selectedIds).start();
                                // delete records from the database
                                new DeleteItemsThread(getActivity(), selectedIds).start();
                            }
                            mode.finish();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mode.finish();
                        }
                    })
                    .positiveText("Agree")
                    .negativeText("Cancel")
                    .show();

                return true;
            case R.id.action_favourite:
                new MaterialDialog.Builder(getActivity())
                    .title("Save selected")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Cursor cursor = mAdapter.getCursor();
                            if (cursor != null) {
                                List<Long> selectedIds = getFavouritePhotos(cursor, selectedItems);
                                // TODO update database with fav

                            }

                            mode.finish();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mode.finish();
                        }
                    })
                    .positiveText("Agree")
                    .negativeText("Cancel")
                    .show();

                return true;
        }
        return true;
    }
    // END

    public interface Contract {
        // handle ViewHolder.OnClick()
        void onHomeItemClick(long id);
    }

    public void onEnterAnimationComplete() {
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scheduleLayoutAnimation();
    }

    private Cursor mCursor;
    private CustomRecyclerView mRecyclerView;
    private CustomRecyclerViewAdapter mAdapter;
    private TextView mEmptyView;

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);

        // hide the toolbar shadow on devices API 21+
        View toolbarShadow = view.findViewById(R.id.toolbar_shadow);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbarShadow.setVisibility(View.GONE);
        }
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mRecyclerView = (CustomRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a 3-col grid on screens >= 540dp
        GridLayoutManager layoutManager = null;
        Configuration config = getResources().getConfiguration();
        if (config.screenWidthDp >= 540) {
            layoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new CustomItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.grid_item_space),
                getResources().getDimensionPixelSize(R.dimen.grid_item_space)));
        mAdapter = new CustomRecyclerViewAdapter(getActivity(), mCursor);
        mAdapter.setMultiChoiceModeListener((AppCompatActivity)getActivity(), this); // FIXME
        if (isAdded()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mRecyclerView.setAdapter(mAdapter);
            } else {
                onEnterAnimationComplete();
            }
        }
        if (savedInstanceState != null) {
            mAdapter.restoreInstanceState(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.saveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        showHideEmpty();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(ModelLoadedEvent event) {
        // DEBUG
//        Cursor cursor = event.getModel();
//        if (cursor != null) {
//            while(cursor.moveToNext()) {
//                long id = cursor.getLong(cursor.getColumnIndex(Constants.PHOTO_ID));
//                String filePath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_FILE_PATH));
//                Timber.i("%s: id: %d, filePath: %s", Constants.LOG_TAG, id, filePath);
//            }
//        }

        // pass the retrieved cursor to the adapter
        mAdapter.changeCursor(event.getModel());
        showHideEmpty();
    }

    private void showHideEmpty() {
        if (mAdapter.getItemCount() > 0) {
            // hide recycler view when empty
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private String[] getPhotosForDeletion(Cursor cursor, SparseBooleanArray selectedItems) {
        List<String> selectedIds = new ArrayList<>();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (selectedItems.get(i)) {
                if (cursor.moveToPosition(i)) {
                    String id = String.valueOf(cursor.getLong(cursor.getColumnIndex(Constants.PHOTO_ID)));
                    selectedIds.add(id);
                }
            }
        }
        return selectedIds.toArray(new String[selectedIds.size()]);
    }

    private List<Long> getFavouritePhotos(Cursor cursor, SparseBooleanArray selectedItems) {
        List<Long> selectedIds = new ArrayList<>();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (selectedItems.get(i)) {
                if (cursor.moveToPosition(i)) {
                    long id = cursor.getLong(cursor.getColumnIndex(Constants.PHOTO_ID));
                    selectedIds.add(id);
                }
            }
        }
        return selectedIds;
    }

    public class CustomRecyclerViewAdapter extends CustomMultiChoiceCursorRecyclerViewAdapter<CustomViewHolder> {

        public CustomRecyclerViewAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.recycler_item_view, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder viewHolder, Cursor cursor) {
            if (cursor != null) {
                viewHolder.bindViewHolder(cursor);
                // highlight any selected notes
                int position = cursor.getPosition();
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(
                        getActivity(), isSelected(position) ?
                            R.color.colorSelectedBackground : R.color.colorSecondaryBackground));
            }
        }

    }


    public class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        CustomImageView mImageView;
        String mThumbnailPath;
        long mId;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mImageView = (CustomImageView) itemView.findViewById(R.id.image_view);
        }

        public void bindViewHolder(Cursor cursor) {
            mId = cursor.getLong(cursor.getColumnIndex(Constants.PHOTO_ID));
            mThumbnailPath = cursor.getString(cursor.getColumnIndex(Constants.PHOTO_THUMBNAIL_PATH));
            Picasso.with(getActivity())
                    .load(new File(mThumbnailPath))
                    .fit()
                    .centerCrop()
                    .into(mImageView);
        }

        @Override
        public void onClick(View view) {
            if (mAdapter.isActionModeActive()) {
                mAdapter.toggleSelected(getAdapterPosition());
            } else {
                getContract().onHomeItemClick(mId);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            mAdapter.toggleSelected(getAdapterPosition());
            return true;
        }
    }

}
