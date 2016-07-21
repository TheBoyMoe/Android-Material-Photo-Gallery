package com.example.materialphotogallery.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.materialphotogallery.R;
import com.example.materialphotogallery.common.Utils;
import com.example.materialphotogallery.ui.fragment.HomeFragment;
/**
 *  References:
 *  [[1] https://guides.codepath.com/android/Fragment-Navigation-Drawer
 */
public class MainActivity extends AppCompatActivity implements HomeFragment.Contract{

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    private CoordinatorLayout mLayout;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        initToolbar();
        initFab();
        setupDrawerLayout();

        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = HomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
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

    private void setupDrawerLayout() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // handle clicking on individual nav items via switch()
                Utils.showSnackbar(mLayout, "Clicked " + item.getTitle());

                item.setChecked(true);
                mDrawer.closeDrawers();
                return true;
            }
        });
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

                // hide app title
                actionbar.setDisplayShowTitleEnabled(false);
                // enable custom view
                actionbar.setDisplayShowCustomEnabled(true);
                // instantiate & show title depending on fragment loaded
                LayoutInflater inflater = LayoutInflater.from(this);
                View pageTitle = inflater.inflate(R.layout.current_view_title, null);
                ((TextView)pageTitle.findViewById(R.id.action_bar_title)).setText("custom page title");
                actionbar.setCustomView(pageTitle);
            }
        }


    }



}
