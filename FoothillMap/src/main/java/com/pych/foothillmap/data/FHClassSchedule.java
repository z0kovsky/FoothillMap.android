package com.pych.foothillmap.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;

/**
 * Created by Elena Pychenkova on 24.09.13.
 */
public class FHClassSchedule {

    public interface IFHClassScheduleListener extends EventListener {
        void onItemAdded(FHClass item, int position);

        void onItemRemoved(FHClass item);
    }

    public HashMap<String, IFHClassScheduleListener> listenersList = null;
    private HashMap<Integer, ArrayList<FHClass>> items;
    private static FHClassSchedule sharedSchedule = null;
    private static String fileName = "/schedule.archive";

    public FHClassSchedule() {
        restoreSchedule();

        listenersList = new HashMap<String, IFHClassScheduleListener>();

        if (items == null) {
            items = new HashMap<Integer, ArrayList<FHClass>>();
        }

        for (Integer weekday : items.keySet()) {
            for (FHClass item : items.get(weekday)) {
                item.setListener(new FHClass.IFHClassListener() {
                    @Override
                    public void onChanged(FHClass item) {
                        onItemChanged(item);
                    }
                });
            }
        }
    }

    public static FHClassSchedule getSharedSchedule() {
        if (sharedSchedule == null) {
            sharedSchedule = new FHClassSchedule();
        }

        return sharedSchedule;
    }

    public HashMap<Integer, ArrayList<FHClass>> getItems() {
        return items;
    }

    public void addItem(FHClass item) {
        Integer weekday = item.getWeekday();
        if (!items.containsKey(weekday) || items.get(weekday) == null) {
            items.put(weekday, new ArrayList<FHClass>());
        }

        item.setListener(new FHClass.IFHClassListener() {
            @Override
            public void onChanged(FHClass item) {
                onItemChanged(item);
            }
        });

        Date itemTemp = new Date();
        itemTemp.setHours(item.getTime().getHours());
        itemTemp.setMinutes(item.getTime().getMinutes());

        Date temp = new Date();
        int position = -1;
        ArrayList<FHClass> array = items.get(weekday);
        for (int i = 0; i < array.size(); i++) {
            temp.setHours(array.get(i).getTime().getHours());
            temp.setMinutes(array.get(i).getTime().getMinutes());

            if (temp.compareTo(itemTemp) == 1) {
                array.add(i, item);
                position = i;
                break;
            }
        }

        if (position == -1) {
            array.add(item);
            position = array.size() - 1;
        }

        saveSchedule();

        for (IFHClassScheduleListener listener : listenersList.values()) {
            listener.onItemAdded(item, position);
        }
    }

    public void removeItem(FHClass item) {
        Integer weekday = item.getWeekday();
        items.get(weekday).remove(item);

        item.setListener(null);

        if (items.get(weekday).size() == 0) {
            items.remove(weekday);
        }

        saveSchedule();

        for (IFHClassScheduleListener listener : listenersList.values()) {
            listener.onItemRemoved(item);
        }
    }

    public void removeItemByID(String ID) {
        FHClass item = null;
        for (Integer weekday : items.keySet()) {
            ArrayList<FHClass> subItems = items.get(weekday);
            for (FHClass subItem : subItems) {
                if (subItem.getID().equals(ID)) {
                    item = subItem;
                    break;
                }
            }
        }

        if (item != null) {
            removeItem(item);
        }
    }

    private void saveSchedule() {
        ArrayList<FHClass> list = new ArrayList<FHClass>();
        for (Integer weekday : items.keySet()) {
            list.addAll(items.get(weekday));
        }
        StoreManager.storeData(fileName, list);
    }

    private void restoreSchedule() {
        ArrayList<FHClass> list = (ArrayList<FHClass>) StoreManager.restoreData(fileName);
        if (list != null) {
            items = new HashMap<Integer, ArrayList<FHClass>>();
            for (FHClass item : list) {
                item.setTime(item.getTime());
                Integer weekday = item.getWeekday();
                if (!items.containsKey(weekday)) {
                    items.put(weekday, new ArrayList<FHClass>());
                }
                items.get(weekday).add(item);
            }
        }
    }

    public void addListener(IFHClassScheduleListener listener, String key) {
        this.listenersList.put(key, listener);
    }

    public void onItemChanged(FHClass item) {
        removeItem(item);
        addItem(item);

        saveSchedule();
    }
}
