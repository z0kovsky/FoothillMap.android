package com.pych.foothillmap.schedule;

import android.content.Context;

import com.pych.foothillmap.R;
import com.pych.foothillmap.data.DataHelper;
import com.pych.foothillmap.data.FHClass;
import com.pych.foothillmap.data.FHClassSchedule;
import com.pych.foothillmap.data.SeparatedListAdapter;

import java.util.ArrayList;

/**
 * Created by Elena Pychenkova on 24.09.13.
 */
public class ScheduleListAdapter extends SeparatedListAdapter {
    private Context context;
    private int NUM_DAYS_IN_WEEK = 7;
    private int NUM_VIEW_TYPE_IN_WEEK_SECTION = 2;

    public ScheduleListAdapter(Context context) {
        super(context);

        this.context = context;
    }

    public void bindSchedule() {
        for (Integer weekday : FHClassSchedule.getSharedSchedule().getItems().keySet()) {
            String key = DataHelper.getWeekdayString(weekday);
            ClassListAdapter adapter = new ClassListAdapter(
                    context,
                    R.id.list_class_row,
                    new ArrayList<FHClass>()
            );
            int weekdayPosition = 0;
            for (int i = 0; i < headers.getCount(); i++) {
                if (weekday > DataHelper.getNumberByWeekdayString(headers.getItem(i))) {
                    weekdayPosition = i + 1;
                } else {
                    break;
                }
            }
            addSection(key, weekdayPosition, adapter);

            for (FHClass item : FHClassSchedule.getSharedSchedule().getItems().get(weekday)) {
                adapter.add(item);
            }
        }

        FHClassSchedule.getSharedSchedule().addListener(new FHClassSchedule.IFHClassScheduleListener() {
            @Override
            public void onItemAdded(FHClass item, int classPosition) {
                String key = DataHelper.getWeekdayString(item.getWeekday());
                if (!sections.containsKey(key)) {
                    ClassListAdapter adapter = new ClassListAdapter(
                            context,
                            R.id.list_class_row,
                            new ArrayList<FHClass>()
                    );
                    int weekday = item.getWeekday();
                    int weekdayPosition = 0;
                    for (int i = 0; i < headers.getCount(); i++) {
                        if (weekday > DataHelper.getNumberByWeekdayString(headers.getItem(i))) {
                            weekdayPosition = i + 1;
                        } else {
                            break;
                        }
                    }

                    addSection(key, weekdayPosition, adapter);
                }

                ((ClassListAdapter) sections.get(key)).insert(item, classPosition);
                notifyDataSetChanged();
            }

            @Override
            public void onItemRemoved(FHClass item) {
                String key = DataHelper.getWeekdayString(item.getWeekday());
                if (sections.containsKey(key)) {
                    ((ClassListAdapter) sections.get(key)).remove(item);

                    if (sections.get(key).getCount() == 0) {
                        sections.remove(key);
                        headers.remove(key);
                    }

                    notifyDataSetChanged();
                }
            }

        }, "ScheduleListAdapter");
    }

    @Override
    public int getViewTypeCount() {
        return NUM_DAYS_IN_WEEK * NUM_VIEW_TYPE_IN_WEEK_SECTION;
    }
}
