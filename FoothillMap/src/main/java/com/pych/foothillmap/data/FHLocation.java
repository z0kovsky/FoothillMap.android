package com.pych.foothillmap.data;

/**
 * Created by Elena Pychenkova on 10.10.13.
 */
public class FHLocation {

    public enum FHLocationCategory {
        FoodAndDrinks,
        Restroom,
        Parking,
        SmokingArea,
        CurrenClass,
        NextClass,
        Others;

        public static FHLocationCategory getCategoryByTitle(String title) {
            FHLocationCategory category = FHLocationCategory.Others;

            if (title.compareTo("Parking") == 0) {
                category = FHLocationCategory.Parking;
            }
            if (title.compareTo("Restroom") == 0) {
                category = FHLocationCategory.Restroom;
            }
            if (title.compareTo("FoodAndDrinks") == 0) {
                category = FHLocationCategory.FoodAndDrinks;
            }
            if (title.compareTo("SmokingArea") == 0) {
                category = FHLocationCategory.SmokingArea;
            }
            if (title.compareTo("CurrentClass") == 0) {
                category = FHLocationCategory.CurrenClass;
            }
            if (title.compareTo("NextClass") == 0) {
                category = FHLocationCategory.NextClass;
            }

            return category;
        }
    }

    private float longitude;
    private float latitude;
    private float id;
    private String title;
    private String bldg;
    private String room;
    private String categoryTitle;
    private FHLocationCategory category;

    public FHLocation() {
    }

    public FHLocation(
            float longitude,
            float latitude,
            float id,
            String title,
            String bldg,
            String room) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.title = title;
        this.bldg = bldg;
        this.room = room;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setId(float id) {
        this.id = id;
    }

    public float getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setBldg(String bldg) {
        this.bldg = bldg;
    }

    public String getBldg() {
        return bldg;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRoom() {
        return room;
    }

    public void setCategoryTitle(String category) {
        this.categoryTitle = category;
        this.category = FHLocationCategory.getCategoryByTitle(category);
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategory(FHLocationCategory category) {
        this.category = category;
        this.categoryTitle = category.toString();
    }

    public FHLocationCategory getCategory() {
        return category;
    }

}
