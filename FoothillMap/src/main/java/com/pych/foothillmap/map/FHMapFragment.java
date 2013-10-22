package com.pych.foothillmap.map;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pych.foothillmap.R;
import com.pych.foothillmap.data.DataHelper;
import com.pych.foothillmap.data.FHClass;
import com.pych.foothillmap.data.FHLocation;
import com.pych.foothillmap.data.FHLocationCatalog;
import com.pych.foothillmap.data.FHSettingList;
import com.pych.foothillmap.data.ScheduleCalculator;
import com.pych.foothillmap.data.SeparatedListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FHMapFragment extends Fragment {

    protected class MarkerWrapper {
        private Marker marker;
        private FHLocation location;

        public MarkerWrapper(Marker marker, FHLocation location) {
            this.marker = marker;
            this.location = location;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }

        public Marker getMarker() {
            return marker;
        }

        public void setLocation(FHLocation location) {
            this.location = location;
        }

        public FHLocation getLocation() {
            return location;
        }
    }

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private SeparatedListAdapter mSettingsAdapter;

    private GoogleMap mMap;
    private MapFragment mMapFragment;
    private boolean isMapWorking;

    private View mView;
    private LatLng FOOTHILL_COORDS = new LatLng(37.361426, -122.127113);

    private Button statusButtonCurrent;
    private Button statusButtonNext;
    private int STATUS_BUTTON_HEIGHT = 100;

    private HashMap<FHLocation.FHLocationCategory, ArrayList<MarkerWrapper>> locationMarkers = null;

    private MarkerWrapper currentMarkerWrapper = null;
    private boolean isCurrentShow = false;
    private MarkerWrapper nextMarkerWrapper = null;
    private boolean isNextShow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScheduleCalculator.getSharedScheduleCalculator().setListener(new ScheduleCalculator.IScheduleCalculatorListener() {
            @Override
            public void onNewCurrentClass(FHClass item) {
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        updateCurrentClassState();
                    }
                });
            }

            @Override
            public void onNewNextClass(FHClass item) {
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        updateNextClassState();
                    }
                });
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ScheduleCalculator.getSharedScheduleCalculator().setListener(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_map, null);

            statusButtonCurrent = (Button) mView.findViewById(R.id.status_btn_current);
            statusButtonNext = (Button) mView.findViewById(R.id.status_btn_next);

            statusButtonCurrent.setVisibility(View.INVISIBLE);
            statusButtonNext.setVisibility(View.INVISIBLE);

            statusButtonCurrent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentMarkerWrapper != null) {
                        showLocation(currentMarkerWrapper.getLocation().getLatitude(), currentMarkerWrapper.getLocation().getLongitude());
                    }

                }
            });

            statusButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nextMarkerWrapper != null) {
                        showLocation(nextMarkerWrapper.getLocation().getLatitude(), nextMarkerWrapper.getLocation().getLongitude());
                    }

                }
            });

            initDrawer();
            initMap();
            setHasOptionsMenu(true);

            loadDefaultMarkers();
            for (int i = 0; i < mDrawerList.getCount(); i++) {
                String title = (String) mDrawerList.getItemAtPosition(i);
                if (title != null) {
                    if (FHSettingList.getSharedSettings().getSettingSelected(title)) {
                        mDrawerList.setItemChecked(i, true);
                        selectItem(i);
                    }
                }
            }
        }
        return mView;
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) mView.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) mView.findViewById(R.id.right_drawer);

        mSettingsAdapter = new SeparatedListAdapter(getActivity());
        mSettingsAdapter.addSection(getString(R.string.section_foothill), new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_multichoice,
                new String[]{
                        getResources().getString(R.string.setting_food_and_drinks),
                        getResources().getString(R.string.setting_restroom),
                        getResources().getString(R.string.setting_parking),
                        getResources().getString(R.string.setting_smoking_area)}));

        mSettingsAdapter.addSection(getString(R.string.section_classes), new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_multichoice,
                new String[]{
                        getResources().getString(R.string.setting_current_class),
                        getResources().getString(R.string.setting_next_class)}));

        mDrawerList.setAdapter(mSettingsAdapter);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initMap() {
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        if (mMap == null) {
            isMapWorking = false;
            return;
        }

        isMapWorking = true;
        mMap.setMyLocationEnabled(true);
        resetMyLocationButton();
        showFoothill();

        mMapFragment.onResume();
    }

    private void loadDefaultMarkers() {
        locationMarkers = new HashMap<FHLocation.FHLocationCategory, ArrayList<MarkerWrapper>>();

        if (!isMapWorking) {
            return;
        }

        for (FHLocation location : FHLocationCatalog.getSharedCatalog().getLocations()) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

            FHLocation.FHLocationCategory category = location.getCategory();
            if (category != null) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(location.getTitle())
                        .visible(false)
                        .snippet(location.getRoom())
                        .icon(DataHelper.getIconForCategory(category)));

                if (!locationMarkers.containsKey(category)) {
                    locationMarkers.put(category, new ArrayList<MarkerWrapper>());
                }
                locationMarkers.get(category).add(new MarkerWrapper(marker, location));
            }
        }
    }

    private void resetMyLocationButton() {
        if (!isMapWorking) {
            return;
        }

        ViewGroup v1 = (ViewGroup) mMapFragment.getView();
        ViewGroup v2 = (ViewGroup) v1.getChildAt(0);
        ViewGroup v3 = (ViewGroup) v2.getChildAt(1);

        View position = v3.getChildAt(0);

        int positionWidth = position.getLayoutParams().width;
        int positionHeight = position.getLayoutParams().height;

        RelativeLayout.LayoutParams positionParams = new RelativeLayout.LayoutParams(positionWidth, positionHeight);
        positionParams.setMargins(20, 20, 20, 100);
        positionParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
        positionParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        position.setLayoutParams(positionParams);
    }

    private void showFoothill() {
        if (!isMapWorking) {
            return;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(FOOTHILL_COORDS)
                .zoom(17)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void showLocation(double lat, double lon) {
        if (!isMapWorking) {
            return;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lon))
                .zoom(19)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        String setting = (String) mSettingsAdapter.getItem(position);
        boolean newVisible = mDrawerList.isItemChecked(position);

        FHLocation.FHLocationCategory category = FHLocation.FHLocationCategory.Others;
        if (setting.compareTo(getString(R.string.setting_food_and_drinks)) == 0) {
            category = FHLocation.FHLocationCategory.FoodAndDrinks;
        }
        if (setting.compareTo(getString(R.string.setting_parking)) == 0) {
            category = FHLocation.FHLocationCategory.Parking;
        }
        if (setting.compareTo(getString(R.string.setting_smoking_area)) == 0) {
            category = FHLocation.FHLocationCategory.SmokingArea;
        }
        if (setting.compareTo(getString(R.string.setting_restroom)) == 0) {
            category = FHLocation.FHLocationCategory.Restroom;
        }
        if (setting.compareTo(getString(R.string.setting_current_class)) == 0) {
            category = FHLocation.FHLocationCategory.CurrenClass;
            isCurrentShow = newVisible;
        }
        if (setting.compareTo(getString(R.string.setting_next_class)) == 0) {
            category = FHLocation.FHLocationCategory.NextClass;
            isNextShow = newVisible;
        }

        if (category != FHLocation.FHLocationCategory.Others &&
                locationMarkers.containsKey(category)) {
            for (MarkerWrapper wrap : locationMarkers.get(category)) {
                wrap.marker.setVisible(newVisible);
            }
        }

        if (category == FHLocation.FHLocationCategory.CurrenClass) {
            updateCurrentClassState();
        }
        if (category == FHLocation.FHLocationCategory.NextClass) {
            updateNextClassState();
        }

        String title = (String) mDrawerList.getItemAtPosition(position);
        FHSettingList.getSharedSettings().setSettingSelected(title, newVisible);

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fhschedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showFoothill();
                break;
            case R.id.menu_filter:
                onFilterClicked();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void onFilterClicked() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }

    private void updateCurrentClassState() {
        FHClass item = ScheduleCalculator.getSharedScheduleCalculator().getCurrentClass();
        if (isCurrentShow && item != null) {

            if (currentMarkerWrapper != null) {
                currentMarkerWrapper.marker.setVisible(false);
            }

            FHLocation location = FHLocationCatalog.getSharedCatalog().findLocationForClass(item);
            if (location != null) {
                statusButtonCurrent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.marker_pin_red, 0);

                if (isMapWorking) {
                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(getString(R.string.status_marker_current, item.getTitle(), item.getTimeString()))
                            .visible(false)
                            .snippet(item.getLocation())
                            .icon(DataHelper.getIconForCategory(FHLocation.FHLocationCategory.CurrenClass)));
                    marker.setVisible(true);
                    currentMarkerWrapper = new MarkerWrapper(marker, location);
                }
            } else {
                statusButtonCurrent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            statusButtonCurrent.setText(getString(R.string.status_button_current, item.getTimeString(), item.getTitle()));
            if (statusButtonNext.getVisibility() == View.VISIBLE) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int height = statusButtonCurrent.getHeight() == 0 ? STATUS_BUTTON_HEIGHT : statusButtonCurrent.getHeight();

                params.setMargins(0, height, 0, 0);
                statusButtonNext.setLayoutParams(params);
            }
            statusButtonCurrent.setVisibility(View.VISIBLE);
        } else {
            if (currentMarkerWrapper != null) {
                currentMarkerWrapper.marker.setVisible(false);
                currentMarkerWrapper = null;
            }

            statusButtonCurrent.setVisibility(View.INVISIBLE);
            if (statusButtonNext.getVisibility() == View.VISIBLE) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                statusButtonNext.setLayoutParams(params);
            }
        }
    }

    private void updateNextClassState() {
        FHClass item = ScheduleCalculator.getSharedScheduleCalculator().getNextClass();
        if (isNextShow && item != null) {

            if (nextMarkerWrapper != null) {
                nextMarkerWrapper.marker.setVisible(false);
            }
            FHLocation location = FHLocationCatalog.getSharedCatalog().findLocationForClass(item);
            if (location != null) {
                if (isMapWorking) {
                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(getString(R.string.status_marker_next, item.getTitle(), item.getTimeString()))
                            .visible(false)
                            .snippet(item.getLocation())
                            .icon(DataHelper.getIconForCategory(FHLocation.FHLocationCategory.NextClass)));
                    marker.setVisible(true);
                    nextMarkerWrapper = new MarkerWrapper(marker, location);
                }
                statusButtonNext.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.marker_pin_blue, 0);
            } else {
                statusButtonNext.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            }

            statusButtonNext.setText(getString(R.string.status_button_next, item.getTimeString(), item.getTitle()));
            if (statusButtonCurrent.getVisibility() == View.VISIBLE) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int height = statusButtonCurrent.getHeight() == 0 ? STATUS_BUTTON_HEIGHT : statusButtonCurrent.getHeight();
                params.setMargins(0, height, 0, 0);
                statusButtonNext.setLayoutParams(params);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                statusButtonNext.setLayoutParams(params);
            }
            statusButtonNext.setVisibility(View.VISIBLE);
        } else {
            if (nextMarkerWrapper != null) {
                nextMarkerWrapper.marker.setVisible(false);
                nextMarkerWrapper = null;
            }

            statusButtonNext.setVisibility(View.INVISIBLE);
        }
    }


}
