package com.pych.foothillmap.data;


import com.pych.foothillmap.R;

import java.util.HashMap;

/**
 * Created by Elena Pychenkova on 10.10.13.
 */
public class FHSettingList {

    private HashMap<String, Boolean> settings = null;
    private static FHSettingList sharedSettings;
    private static String fileName = "/settings.archive";


    public FHSettingList() {
        restoreData();

        if (settings == null) {
            settings = new HashMap<String, Boolean>();
            settings.put(DataHelper.context.getString(R.string.setting_food_and_drinks), true);
            settings.put(DataHelper.context.getString(R.string.setting_parking), true);
            settings.put(DataHelper.context.getString(R.string.setting_restroom), true);
            settings.put(DataHelper.context.getString(R.string.setting_smoking_area), true);
            settings.put(DataHelper.context.getString(R.string.setting_current_class), true);
            settings.put(DataHelper.context.getString(R.string.setting_next_class), true);
        }
    }

    public static FHSettingList getSharedSettings() {
        if (sharedSettings == null) {
            sharedSettings = new FHSettingList();
        }

        return sharedSettings;
    }

    public void setSettingSelected(String title, Boolean selected) {
        settings.put(title, selected);
        saveData();
    }

    public Boolean getSettingSelected(String title) {
        if (settings.containsKey(title)) {
            return settings.get(title);
        }

        return false;
    }

    private void restoreData() {
        settings = (HashMap<String, Boolean>) StoreManager.restoreData(fileName);
    }

    private void saveData() {
        StoreManager.storeData(fileName, settings);
    }
}
