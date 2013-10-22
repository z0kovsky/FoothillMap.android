package com.pych.foothillmap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.pych.foothillmap.data.DataHelper;
import com.pych.foothillmap.data.ScheduleCalculator;
import com.pych.foothillmap.map.FHMapFragment;
import com.pych.foothillmap.schedule.FHScheduleFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private ScheduleCalculator scheduleCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataHelper.context = this;
        ScheduleCalculator.getSharedScheduleCalculator().init();

        initTabs(savedInstanceState);
    }

    private void initTabs(Bundle savedInstanceState) {
        Log.d("DEBUG", "MainActivity: initTabs");

        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        bar.addTab(bar.newTab().setText(getString(R.string.tab_schedule)).setTabListener(new FHTabsListener(new FHScheduleFragment(), getApplicationContext())));
        bar.addTab(bar.newTab().setText(getString(R.string.tab_map)).setTabListener(new FHTabsListener(new FHMapFragment(), getApplicationContext())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("DEBUG", "MainActivity: onPrepareOptionsMenu");
        updateMenuButtons(menu);
        return true;
    }

    private void updateMenuButtons(Menu menu) {
        Log.d("DEBUG", "MainActivity: updateMenuButtons");

        try {
            MenuItem btnAddClass = menu.findItem(R.id.menu_new_item);
            MenuItem btnFilter = menu.findItem(R.id.menu_filter);

            switch (getActionBar().getSelectedTab().getPosition()) {
                case 1:
                    btnAddClass.setVisible(false);
                    btnFilter.setVisible(true);
                    break;
                case 0:
                    btnAddClass.setVisible(true);
                    btnFilter.setVisible(false);
                    break;
            }
        } catch (Exception ex) {
            Log.d("DEBUG", ex.getMessage());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
                .getSelectedNavigationIndex());
    }

    class FHTabsListener implements ActionBar.TabListener {
        public Fragment fragment;
        public Context context;

        public FHTabsListener(Fragment fragment, Context context) {
            this.fragment = fragment;
            this.context = context;
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(android.R.id.content, fragment);
            invalidateOptionsMenu();
            switch (tab.getPosition()) {
                case 1:
                    getActionBar().setHomeButtonEnabled(true);
                    break;
                case 0:
                    getActionBar().setHomeButtonEnabled(false);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
        }
    }
}
