package com.pych.foothillmap.data;

import com.pych.foothillmap.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Elena Pychenkova on 10.10.13.
 */
public class FHLocationCatalog {

    private static FHLocationCatalog sharedCatalog = null;
    private static ArrayList<FHLocation> locationList = null;
    private static ArrayList<String> titleList = null;

    public FHLocationCatalog() {
        loadData();
    }

    public static FHLocationCatalog getSharedCatalog() {
        if (sharedCatalog == null) {
            sharedCatalog = new FHLocationCatalog();
        }
        return sharedCatalog;
    }

    private void loadData() {
        locationList = new ArrayList<FHLocation>();
        titleList = new ArrayList<String>();

        StringBuilder builder = new StringBuilder();
        try {
            InputStream content = DataHelper.context.getResources().openRawResource(R.raw.college_json);
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
        }

        String jsonString = builder.toString();
        try {

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FHLocation location = new FHLocation();
                if (jsonObject.has("id")) {
                    location.setId(Float.valueOf(jsonObject.getString("id")));
                }
                if (jsonObject.has("name")) {
                    location.setTitle(jsonObject.getString("name"));
                }
                if (jsonObject.has("lat")) {
                    location.setLatitude(Float.valueOf(jsonObject.getString("lat")));
                }
                if (jsonObject.has("lon")) {
                    location.setLongitude(Float.valueOf(jsonObject.getString("lon")));
                }
                if (jsonObject.has("bldg")) {
                    location.setBldg(jsonObject.getString("bldg"));
                }
                if (jsonObject.has("bldg")) {
                    location.setRoom(jsonObject.getString("bldg"));
                }
                if (jsonObject.has("category")) {
                    location.setCategoryTitle(jsonObject.getString("category"));
                }

                if (location.getTitle() != null &&
                        !location.getTitle().isEmpty() &&
                        !titleList.contains(location.getTitle())) {
                    titleList.add(location.getTitle());
                }
                if (location.getBldg() != null &&
                        !location.getBldg().isEmpty() &&
                        !titleList.contains(location.getBldg())) {
                    titleList.add(location.getBldg());
                }
                if (location.getRoom() != null &&
                        !location.getRoom().isEmpty() &&
                        !titleList.contains(location.getRoom())) {
                    titleList.add(location.getRoom());
                }

                locationList.add(location);
            }
        } catch (Exception e) {
        }
    }

    public ArrayList<String> getLocationTitles() {
        return titleList;
    }

    public ArrayList<FHLocation> getLocations() {
        return locationList;
    }

    public FHLocation findLocationForClass(FHClass item) {
        FHLocation location = null;

        String locationName = item.getLocation().toLowerCase().replace(" ", "");
        if (locationName == null || locationName.isEmpty()) {
            return null;
        }
        for (FHLocation loc : locationList) {
            if (loc.getTitle() != null && locationName.compareTo(loc.getTitle().toLowerCase().replace(" ", "")) == 0) {
                location = loc;
                break;
            }
            if (loc.getBldg() != null && locationName.compareTo(loc.getBldg().toLowerCase().replace(" ", "")) == 0) {
                location = loc;
                break;
            }
            if (loc.getRoom() != null && locationName.compareTo(loc.getRoom().toLowerCase().replace(" ", "")) == 0) {
                location = loc;
                break;
            }
        }
        return location;
    }
}
