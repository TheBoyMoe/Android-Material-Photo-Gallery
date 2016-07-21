package com.example.materialphotogallery.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.materialphotogallery.ui.fragment.AboutFragment;
import com.example.materialphotogallery.ui.fragment.FavouriteFragment;
import com.example.materialphotogallery.ui.fragment.HomeFragment;
import com.example.materialphotogallery.ui.fragment.PhotoMapFragment;
import com.example.materialphotogallery.ui.fragment.SettingsFragment;

import timber.log.Timber;

/**
 *  References:
 *  [[1] https://guides.codepath.com/android/Fragment-Navigation-Drawer
 *  [2] http://stackoverflow.com/questions/13472258/handling-actionbar-title-with-the-fragment-back-stack
 */
public class MainActivity extends AppCompatActivity implements
        HomeFragment.Contract,
        FavouriteFragment.Contract,
        PhotoMapFragment.Contract{

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    private static final String CURRENT_PAGE_TITLE = "current_page_title";
    private CoordinatorLayout mLayout;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private String mCurrentTitle;

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
            // else restore the page title
            mCurrentTitle = savedInstanceState.getString(CURRENT_PAGE_TITLE);
            setTitle(mCurrentTitle);
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
        Class fragmentClass;
        switch (item.getItemId()) {
            case R.id.drawer_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.drawer_favourite:
                fragmentClass = FavouriteFragment.class;
                break;
            case R.id.drawer_map:
                fragmentClass = PhotoMapFragment.class;
                break;
            case R.id.drawer_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.drawer_about:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
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

        // replacing the existing fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
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
                    Utils.showSnackbar(mLayout, "Clicked on fab");
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

                // hide app title to custom text view
//                actionbar.setDisplayShowTitleEnabled(false);
                // enable custom view
//                actionbar.setDisplayShowCustomEnabled(true);
                // instantiate & show title depending on fragment loaded
//                LayoutInflater inflater = LayoutInflater.from(this);
//                View pageTitle = inflater.inflate(R.layout.current_view_title, null);
//                ((TextView)pageTitle.findViewById(R.id.action_bar_title)).setText(mCurrentTitle);
//                actionbar.setCustomView(pageTitle);
            }

            // hide the toolbar shadow on devices API 21+
            // View toolbarShadow = findViewById(R.id.toolbar_shadow);
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //    toolbarShadow.setVisibility(View.GONE);
            //}
        }
    }

    private void displayInitialFragment() {
        mCurrentTitle = getString(R.string.menu_title_home);
        setTitle(mCurrentTitle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, HomeFragment.newInstance())
                .addToBackStack(mCurrentTitle)
                .commit();
    }

}
