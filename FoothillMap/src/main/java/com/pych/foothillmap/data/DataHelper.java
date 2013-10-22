package com.pych.foothillmap.data;

import android.content.Context;
import android.text.format.DateUtils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.pych.foothillmap.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Elena Pychenkova on 30.09.13.
 */
public class DataHelper {

    public static String[] dayNames;
    private static ArrayList<String> dayNamesList;
    public static Context context;

    static {
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        dayNamesList = new ArrayList<String>(Arrays.asList(symbols.getWeekdays()));
        dayNamesList.remove(0);
        dayNames = dayNamesList.toArray(new String[dayNamesList.size()]);
    }

    public static String getWeekdayString(int number) {
        return DataHelper.dayNames[number];
    }

    public static int getNumberByWeekdayString(String name) {
        return DataHelper.dayNamesList.indexOf(name);
    }

    public static String getTimeString(Date date) {
        if (context == null) {
            return "";
        }

        return DateUtils.formatDateTime(
                context,
                date.getTime(), DateUtils.FORMAT_SHOW_TIME);
    }

    public static BitmapDescriptor getIconForCategory(FHLocation.FHLocationCategory category) {
        BitmapDescriptor icon;

        switch (category) {
            case Parking:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_parking);
                break;
            case FoodAndDrinks:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_food);
                break;
            case Restroom:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_restroom);
                break;
            case SmokingArea:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_smoking);
                break;
            case CurrenClass:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pin_red);
                break;
            case NextClass:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pin_blue);
                break;
            default:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pin_grey);
                break;
        }

        return icon;
    }
}
