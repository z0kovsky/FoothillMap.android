package com.pych.foothillmap.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.util.EventListener;
import java.util.UUID;

/**
 * Created by Elena Pychenkova on 24.09.13.
 */
public class FHClass implements Serializable, Parcelable {

    public interface IFHClassListener extends EventListener {
        void onChanged(FHClass item);
    }

    private static final long serialVersionUID = 1L;

    private String ID;
    private String title;
    private String location;
    private Date time;
    private Integer weekday;

    private String localTime;
    private transient IFHClassListener listener = null;

    public FHClass(String title, String location, Date time, Integer weekday) {
        setTime(time);
        this.weekday = weekday;
        this.title = title;
        this.location = location;
        this.ID = UUID.randomUUID().toString();
    }

    public FHClass(Parcel source) {
        this.title = source.readString();
        this.location = source.readString();
        setTime(new Date(source.readLong()));
        this.weekday = source.readInt();
        this.ID = source.readString();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        fireListener();
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
        fireListener();
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
        this.localTime = DataHelper.getTimeString(time);
        fireListener();
    }

    public Integer getWeekday() {
        return this.weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
        fireListener();
    }

    public String getTimeString() {
        return this.localTime;
    }

    public String getID() {
        return this.ID;
    }

    public int getClassDuration() {
        return 90;
    }

    public void setListener(IFHClassListener listener) {
        this.listener = listener;
    }

    private void fireListener() {
        if (listener != null) {
            listener.onChanged(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(location);
        dest.writeLong(time.getTime());
        dest.writeInt(weekday);
        dest.writeString(ID);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FHClass createFromParcel(Parcel in) {
            return new FHClass(in);
        }

        public FHClass[] newArray(int size) {
            return new FHClass[size];
        }
    };

}
