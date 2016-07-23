package com.example.materialphotogallery.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.Constants;
import com.example.materialphotogallery.common.Utils;
import com.example.materialphotogallery.thread.InsertItemThread;
import com.example.materialphotogallery.ui.fragment.AboutFragment;
import com.example.materialphotogallery.ui.fragment.FavouriteFragment;
import com.example.materialphotogallery.ui.fragment.HomeFragment;
import com.example.materialphotogallery.ui.fragment.ModelFragment;
import com.example.materialphotogallery.ui.fragment.PhotoMapFragment;
import com.example.materialphotogallery.ui.fragment.SettingsFragment;

import java.util.List;

import timber.log.Timber;

/**
 *  References:
 *  [1] https://guides.codepath.com/android/Fragment-Navigation-Drawer
 *  [2] http://stackoverflow.com/questions/13472258/handling-actionbar-title-with-the-fragment-back-stack
 *  [3] http://stackoverflow.com/questions/17107005/how-to-clear-fragment-backstack-in-android
 */
public class MainActivity extends AppCompatActivity implements
        HomeFragment.Contract,
        FavouriteFragment.Contract,
        PhotoMapFragment.Contract{

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    private static final String MODEL_FRAGMENT = "model_fragment";
    private static final String HOME_FRAGMENT = "home_fragment";
    private static final String FAVOURITE_FRAGMENT = "favourite_fragment";
    private static final String PHOTO_MAP_FRAGMENT = "photo_map_fragment";
    private static final String SETTINGS_FRAGMENT = "settings_fragment";
    private static final String ABOUT_FRAGMENT = "about_fragment";

    private static final String CURRENT_PAGE_TITLE = "current_page_title";
    private static final String FULL_SIZE_PHOTO_PATH = "full_size_photo_path";
    private static final String PHOTO_PREVIEW_EXT = "_preview.jpg";
    private static final String PHOTO_THUMB_EXT = "_thumb.jpg";
    private static final int PHOTO_REQUEST_CODE = 100;
    private CoordinatorLayout mLayout;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private String mCurrentTitle;
    private String mFullSizePhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        initToolbar();
        initFab();
        setupDrawerLayout();

        // set the initial fragment & title on startup
        if (savedInstanceState == null) {
            displayInitialFragment();
        } else {
            mFullSizePhotoPath = savedInstanceState.getString(FULL_SIZE_PHOTO_PATH);
            mCurrentTitle = savedInstanceState.getString(CURRENT_PAGE_TITLE);
            setTitle(mCurrentTitle);
        }

        // cache a reference to the model fragment
        Fragment modelFragment = getSupportFragmentManager().findFragmentByTag(MODEL_FRAGMENT);
        if (modelFragment == null) {
            modelFragment = ModelFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(modelFragment, MODEL_FRAGMENT)
                    .commit();
        }

    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> list = fm.getFragments();
        if (list.size() == 1) { // home fragment is top of stack/visible
            HomeFragment fragment = (HomeFragment) fm.findFragmentByTag(HOME_FRAGMENT);
            if (fragment != null) {
                fragment.onEnterAnimationComplete();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Utils.showToast(this, "Clicked about");
                return true;
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_PAGE_TITLE, mCurrentTitle);
        outState.putString(FULL_SIZE_PHOTO_PATH, mFullSizePhotoPath);
    }

    @Override
    public void onBackPressed() {
        // update the page title
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count <= 1) {
            finish();
        } else{
            mCurrentTitle = fm.getBackStackEntryAt(count - 2).getName();
        }
        super.onBackPressed();
        setTitle(mCurrentTitle);

        // update nav drawer selection
        switch (mCurrentTitle) {
            case "Photo Gallery":
                mNavigationView.setCheckedItem(R.id.drawer_home);
                break;
            case "Favourites":
                mNavigationView.setCheckedItem(R.id.drawer_favourite);
                break;
            case "Map":
                mNavigationView.setCheckedItem(R.id.drawer_map);
                break;
            case "Settings":
                mNavigationView.setCheckedItem(R.id.drawer_settings);
                break;
            case "About":
                mNavigationView.setCheckedItem(R.id.drawer_about);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_REQUEST_CODE) {
                // generate scaled versions of the photo
                String previewPath = Utils.generatePreviewImage(mFullSizePhotoPath, 1400, 1400);
                String thumbnailPath = Utils.generateThumbnailImage(mFullSizePhotoPath, 300, 300);

                // insert record into database
                ContentValues cv = Utils.setContentValues(
                        Utils.generateCustomId(),
                        mFullSizePhotoPath,
                        previewPath,
                        thumbnailPath
                );
                new InsertItemThread(this, cv).start();
            }
        } else if (resultCode == RESULT_CANCELED){
            Utils.showSnackbar(mLayout, "Operation cancelled by user");
        } else {
            Utils.showSnackbar(mLayout, "Error executing operation");
        }
    }

    private void setupDrawerLayout() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        // select the fragment to instantiate based on the item clicked
        Fragment fragment = null;
        String tag = null;
        Class fragmentClass;
        switch (item.getItemId()) {
            case R.id.drawer_home:
                fragmentClass = HomeFragment.class;
                tag = HOME_FRAGMENT;
                break;
            case R.id.drawer_favourite:
                fragmentClass = FavouriteFragment.class;
                tag = FAVOURITE_FRAGMENT;
                break;
            case R.id.drawer_map:
                fragmentClass = PhotoMapFragment.class;
                tag = PHOTO_MAP_FRAGMENT;
                break;
            case R.id.drawer_settings:
                fragmentClass = SettingsFragment.class;
                tag = SETTINGS_FRAGMENT;
                break;
            case R.id.drawer_about:
                fragmentClass = AboutFragment.class;
                tag = ABOUT_FRAGMENT;
                break;
            default:
                fragmentClass = HomeFragment.class;
                tag = HOME_FRAGMENT;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Timber.e("%s: Could not instantiate %s fragment, %s",
                    Constants.LOG_TAG, fragmentClass.getName(), e.getMessage());
        }

        // highlight the selected item and update page title
        item.setChecked(true);
        switch (item.getTitle().toString()) {
            case "Photo Gallery":
                mCurrentTitle = getString(R.string.menu_title_home);
                break;
            case "Favourites":
                mCurrentTitle = getString(R.string.menu_title_favourite);
                break;
            case "Map":
                mCurrentTitle = getString(R.string.menu_title_map);
                break;
            case "Settings":
                mCurrentTitle = getString(R.string.menu_title_settings);
                break;
            case "About":
                mCurrentTitle = getString(R.string.menu_title_about);
                break;
            default:
                mCurrentTitle = getString(R.string.menu_title_home);
        }

        // clear the back stack if adding home fragment again
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 1 && fragmentClass == HomeFragment.class) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // replacing the existing fragment
        fm.beginTransaction()
                //.replace(R.id.fragment_container, fragment)
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(mCurrentTitle)
                .commit();

        setTitle(mCurrentTitle);
        mDrawer.closeDrawers();
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utils.hasCamera(MainActivity.this)) {
                        takePicture();
                    } else {
                        Utils.showSnackbar(mLayout, "Your device lacks a camera");
                    }
                }
            });
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            if (actionbar != null) {
                actionbar.setHomeAsUpIndicator(R.drawable.ic_drawer);
                actionbar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void displayInitialFragment() {
        mCurrentTitle = getString(R.string.menu_title_home);
        setTitle(mCurrentTitle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, HomeFragment.newInstance(), HOME_FRAGMENT)
                .addToBackStack(mCurrentTitle)
                .commit();
    }

    private void takePicture() {
        if (canWriteToExternalStorage()) {
            // permission given, take the picture
            launchCameraApp();
        } else {
            // TODO request permissions
            Utils.showSnackbar(mLayout, "You don't have permission to save photos to the device");
        }
    }

    private void launchCameraApp() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri filePathUri = Utils.generatePhotoFileUri(this);
        if (filePathUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePathUri);
            if (Utils.isCameraAppInstalled(this, intent)) {
                startActivityForResult(intent, PHOTO_REQUEST_CODE);
            } else {
                Utils.showSnackbar(mLayout, "No app found suitable to capture photos");
            }
            mFullSizePhotoPath = generateFilePath(filePathUri);
        }
    }

    private boolean canWriteToExternalStorage() {
        // TODO
        return true;
    }

    private String generateFilePath(Uri uriPath) {
        String pattern = "/storage";
        int position = uriPath.toString().indexOf(pattern);
        return uriPath.toString().substring(position);
    }


}
