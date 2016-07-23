package com.example.materialphotogallery.custom;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Adapter is a combination of the following two recycler view adapters
 * [1] https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * [2] https://github.com/jimmy0251/effective-recycler-adapter/tree/master/effective-recycler-adapter/src/main/java/com/jimmy/effectiverecycleradapter
 *
 *     use in combination with MultiChoiceModeListener interface
 *
 * @param <VH>
 */
public abstract class CustomMultiChoiceCursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{

    private static final String KEY_STATE_ADAPTER = "state_adapter";

    private ActionMode actionMode;

    private AppCompatActivity activity;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    private MultiChoiceModeListener choiceListener =MultiChoiceModeListener.EMPTY_LISTENER;

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            return choiceListener.onCreateActionMode(actionMode, mode.getMenuInflater(), menu);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return choiceListener.onActionItemClicked(actionMode, item);
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            choiceListener.onDestroyActionMode();
            selectedItems.clear();
            notifyDataSetChanged();
            actionMode = null;
        }
    };

    /**
     * Sets MultiChoiceModeListener for this Activity.
     *
     * @param activity                AppCompatActivity
     * @param multiChoiceModeListener callback for MultiChoice actions
     */
    public void setMultiChoiceModeListener(AppCompatActivity activity,
                                           MultiChoiceModeListener multiChoiceModeListener) {
        if (activity != null && multiChoiceModeListener != null) {
            this.activity = activity;
            choiceListener = multiChoiceModeListener;
        }
    }

    /**
     * Toggles selected state
     *
     * @param position position of the item
     */
    public void toggleSelected(int position) {
        setSelected(position, !isSelected(position));
    }

    /**
     * Sets item's selected state
     *
     * @param position position of the item
     * @param selected whether item is selected or not
     */
    public void setSelected(int position, boolean selected) {
        if (activity == null) return;

        if (selectedItems.size() == 0) {
            startActionMode();
        }

        if (selected) {
            selectedItems.put(position, true);
        } else {
            selectedItems.delete(position);
        }

        choiceListener.onItemSelectionChanged(actionMode, position, selected);

        if (selectedItems.size() == 0) {
            finishActionMode();
        }
        notifyItemChanged(position);
    }

    /**
     * Starts ActionMode, Should not be invoked directly
     *
     * Instead use setSelected and toggleSelected method which will start ActionMode when
     * appropriate
     */
    public void startActionMode() {
        activity.startSupportActionMode(callback);
    }

    /**
     * Finishes Action Mode
     *
     * Clears selected items and notifyDataSetChanged will be invoked
     */
    public void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    /**
     * Provides selected items count
     *
     * @return selected items count
     */
    public int getSelectedCount() {
        return selectedItems.size();
    }

    /**
     * Provides selected item's positions
     *
     * @return SparseBooleanArray containing selected positions
     */
    public SparseBooleanArray getSelectedPositions() {
        return selectedItems.clone();
    }

    /**
     * Checks whether given position is selected
     *
     * @param position position of item
     * @return whether position is selected
     */
    public boolean isSelected(int position) {
        return selectedItems.get(position);
    }

    /**
     * Checks whether ActionMode is active
     *
     * @return whether ActionMode is active
     */
    public boolean isActionModeActive() {
        return actionMode != null;
    }

    /**
     * Selects all positions of adapter
     *
     * It will invoke onSelectionChanged callback only on the positions which are not selected
     */
    public void selectAll() {
        for (int i = 0, count = getItemCount(); i < count; i++) {
            if (!isSelected(i)) {
                setSelected(i, true);
            }
        }
    }

    /**
     * Saves Adapter state in given bundle, later it can be restored with restoreInstanceState
     *
     * @param outState Bundle in which Adapter state will be saved
     */
    public void saveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_STATE_ADAPTER, onSaveInstanceState());
    }

    /**
     * Provides current Adapter State
     *
     * @return Current adapter state
     */
    public AdapterState onSaveInstanceState() {
        AdapterState state = new AdapterState();
        state.mSelectedPositions = selectedItems;
        return state;
    }

    /**
     * Restores Adapter state from given Bundle if it was previously saved with saveInstanceState
     *
     * @param savedInstanceState Bundle from which Adapter state will be restored
     */
    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            onRestoreInstanceState((AdapterState) savedInstanceState.getParcelable(KEY_STATE_ADAPTER));
        }
    }

    /**
     * Sets given Adapter state as the current adapter state
     *
     * @param adapterState Adapter state of EffectiveRecyclerView
     */
    public void onRestoreInstanceState(AdapterState adapterState) {
        SparseBooleanArray selectedItems = adapterState.mSelectedPositions;
        for (int i = 0; i < selectedItems.size(); i++) {
            setSelected(selectedItems.keyAt(i), true);
        }
    }

    public static class AdapterState implements Parcelable {
        private SparseBooleanArray mSelectedPositions;

        private AdapterState() {
        }

        private AdapterState(Parcel in) {
            mSelectedPositions = in.readSparseBooleanArray();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSparseBooleanArray(mSelectedPositions);
        }

        public static Creator<AdapterState> CREATOR = new Creator<AdapterState>() {
            @Override
            public AdapterState createFromParcel(Parcel source) {
                return new AdapterState(source);
            }

            @Override
            public AdapterState[] newArray(int size) {
                return new AdapterState[size];
            }
        };
    }


    /**
     *  methods that handle cursor
     */

    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;

    public CustomMultiChoiceCursorRecyclerViewAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }



}
