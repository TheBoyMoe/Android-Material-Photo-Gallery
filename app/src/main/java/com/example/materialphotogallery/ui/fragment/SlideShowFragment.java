package com.example.materialphotogallery.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.Utils;
import com.example.materialphotogallery.model.PhotoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Reference:
 * [1] http://www.androidhive.info/2016/04/android-glide-image-library-building-image-gallery-app/
 */
public class SlideShowFragment extends DialogFragment{

    public static final String EXTRA_PHOTO_LIST = "extra_photo_list";
    public static final String EXTRA_PHOTO_POSITION = "extra_photo_position";

    public SlideShowFragment() {}

    private ViewPager mViewPager;
    private TextView mImagePosition;
    private TextView mImageTitle;
    // private TextView mImageDescription;
    private CustomViewPagerAdapter mAdapter;
    private List<PhotoItem> mList;
    private int mCurrentPosition;

    public static SlideShowFragment newInstance(List<PhotoItem> list, int position) {
        SlideShowFragment fragment = new SlideShowFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_PHOTO_LIST, (ArrayList<? extends Parcelable>) list);
        args.putInt(EXTRA_PHOTO_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_slider, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mImagePosition = (TextView) view.findViewById(R.id.image_position);
        mImageTitle = (TextView) view.findViewById(R.id.image_title);
        // mImageDescription = (TextView) view.findViewById(R.id.image_description);

        mList = getArguments().getParcelableArrayList(EXTRA_PHOTO_LIST);
        mCurrentPosition = getArguments().getInt(EXTRA_PHOTO_POSITION);

        // instantiate ViewPagerAdapter and ViewPager
        mAdapter = new CustomViewPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                displayImageInfo(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // no-op
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // no-op
            }

        });

        mViewPager.setCurrentItem(mCurrentPosition, false);
        displayImageInfo(mCurrentPosition);

        return view;
    }

    private void displayImageInfo(int position) {
        mImagePosition.setText(String.format(Locale.ENGLISH, "%d of %d", position + 1, mList.size()));
        PhotoItem item = mList.get(position);
        mImageTitle.setText(item.getTitle());
        // mImageDescription.setText(item.getDescription());
    }

    public class CustomViewPagerAdapter extends PagerAdapter {

        public CustomViewPagerAdapter() {}

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fullscreen_image_view, container, false);
            ImageView preview = (ImageView) view.findViewById(R.id.image_preview);
            PhotoItem item = mList.get(position);
            Utils.loadPreviewWithGlide(getActivity(), item.getPreviewPath(), preview);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }


}
