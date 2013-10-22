package com.pych.foothillmap.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Elena Pychenkova on 10.10.13.
 */
public class ScheduleCalculator {

    public interface IScheduleCalculatorListener extends EventListener {
        void onNewCurrentClass(FHClass item);

        void onNewNextClass(FHClass item);
    }

    protected class UpdateSchedule extends TimerTask {

        public void run() {
            updateState();
        }
    }

    private FHClass currentClass;
    private FHClass nextClass;
    private IScheduleCalculatorListener listener;
    private static ScheduleCalculator sharedScheduleCalculator;
    private final Timer timer = new Timer();
    private boolean isInitialized = false;

    public ScheduleCalculator() {

    }

    public static ScheduleCalculator getSharedScheduleCalculator() {
        if (sharedScheduleCalculator == null) {
            sharedScheduleCalculator = new ScheduleCalculator();
        }

        return sharedScheduleCalculator;
    }

    public void init() {
        if (isInitialized) return;

        isInitialized = true;
        final Timer timer = new Timer();
        TimerTask updateTask = new UpdateSchedule();
        timer.scheduleAtFixedRate(updateTask, 0, 60000);

        FHClassSchedule.getSharedSchedule().addListener(new FHClassSchedule.IFHClassScheduleListener() {
            @Override
            public void onItemAdded(FHClass item, int position) {
                Log.d("DEBUG", "ScheduleCalculator: onItemAdded");
                updateState();
            }

            @Override
            public void onItemRemoved(FHClass item) {
                Log.d("DEBUG", "ScheduleCalculator: onItemRemoved");
                updateState();
            }
        }, "ScheduleCalculator");
    }

    protected void updateState() {
        Date date = new Date();

        FHClass newCurrentClass = findCurrentClassForTime(date);
        if (!isClassEqual(currentClass, newCurrentClass)) {
            if (newCurrentClass == null) {
                currentClass = null;
            } else {
                currentClass = new FHClass(
                        newCurrentClass.getTitle(),
                        newCurrentClass.getLocation(),
                        newCurrentClass.getTime(),
                        newCurrentClass.getWeekday());
            }

            if (this.listener != null)
                this.listener.onNewCurrentClass(currentClass);
        }

        FHClass newNextClass = findNextClassForTime(date);
        if (!isClassEqual(nextClass, newNextClass)) {
            if (newNextClass == null) {
                nextClass = null;
            } else {
                nextClass = new FHClass(
                        newNextClass.getTitle(),
                        newNextClass.getLocation(),
                        newNextClass.getTime(),
                        newNextClass.getWeekday());
            }

            if (this.listener != null)
                this.listener.onNewNextClass(nextClass);
        }
    }

    private boolean isClassEqual(FHClass classA, FHClass classB) {
        return (classA == null && classB == null) || (
                classA != null && classB != null &&
                        (classA.getTitle().compareTo(classB.getTitle()) == 0) &&
                        (classA.getLocation().compareTo(classB.getLocation()) == 0) &&
                        (classA.getWeekday() == classB.getWeekday()) &&
                        (classA.getTimeString().compareTo(classB.getTimeString()) == 0));
    }

    private FHClass findCurrentClassForTime(Date time) {
        FHClass classInfo = null;

        Calendar c = new GregorianCalendar(TimeZone.getDefault());
        c.setTime(time);

        String day_name = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int weekday = DataHelper.getNumberByWeekdayString(day_name);

        ArrayList<FHClass> list = FHClassSchedule.getSharedSchedule().getItems().get(weekday);
        if (list != null) {
            for (FHClass item : list) {
                Date temp = new Date();
                temp.setHours(item.getTime().getHours());
                temp.setMinutes(item.getTime().getMinutes());

                long diffInMs = temp.getTime() - time.getTime();
                long dMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
                long dHours = TimeUnit.MILLISECONDS.toHours(diffInMs);

                if (dMinutes <= 0 && dHours == 0 && dMinutes > (-1) * item.getClassDuration()) {
                    classInfo = item;
                }

                if (dMinutes > 0) {
                    break;
                }
            }
        }

        return classInfo;
    }

    private FHClass findNextClassForTime(Date time) {
        FHClass classInfo = null;

        Calendar c = Calendar.getInstance();
        c.setTime(time);

        String day_name = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int weekday = DataHelper.getNumberByWeekdayString(day_name);

        ArrayList<FHClass> list = FHClassSchedule.getSharedSchedule().getItems().get(weekday);
        if (list != null) {
            for (FHClass item : list) {
                Date temp = new Date();
                temp.setHours(item.getTime().getHours());
                temp.setMinutes(item.getTime().getMinutes());

                long diffInMs = temp.getTime() - time.getTime();
                long dMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
                long dHours = TimeUnit.MILLISECONDS.toHours(diffInMs);

                if (dMinutes > 0) {
                    classInfo = item;
                    break;
                }
            }
        }

        return classInfo;
    }

    public void setListener(IScheduleCalculatorListener listener) {
        this.listener = listener;
    }

    public FHClass getCurrentClass() {
        return currentClass;
    }

    public FHClass getNextClass() {
        return nextClass;
    }
}
