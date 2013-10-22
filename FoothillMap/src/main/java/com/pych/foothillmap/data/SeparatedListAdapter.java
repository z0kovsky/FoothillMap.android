package com.pych.foothillmap.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.pych.foothillmap.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Elena Pychenkova on 24.09.13.
 */

// it is modified J.Sharkey's SeparatedListAdapter
// http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/
public class SeparatedListAdapter extends BaseAdapter {
    public final Map<String, Adapter> sections;
    public final ArrayAdapter<String> headers;
    public final static int TYPE_SECTION_HEADER = 0;

    public SeparatedListAdapter(Context context) {
        headers = new ArrayAdapter<String>(context, R.layout.list_header);
        sections = new LinkedHashMap<String, Adapter>();
    }

    public void addSection(String section, Adapter adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
    }

    public void addSection(String section, Integer position, Adapter adapter) {
        this.headers.insert(section, position);
        this.sections.put(section, adapter);
    }

    public Object getItem(int position) {
        for (int i = 0; i < headers.getCount(); i++) {
            String section = headers.getItem(i);
            Adapter adapter = sections.get(section);
            if (adapter != null) {
                int size = adapter.getCount() + 1;

                if (position == 0) return section;
                if (position < size) return adapter.getItem(position - 1);

                position -= size;
            }
        }
        return null;
    }

    public int getCount() {
        int total = 0;
        for (Adapter adapter : this.sections.values())
            total += adapter.getCount() + 1;

        return total;
    }

    public int getViewTypeCount() {
        int total = 1;
        for (Adapter adapter : this.sections.values())
            total += adapter.getViewTypeCount();

        return total;
    }

    public int getItemViewType(int position) {
        int type = 1;
        for (int i = 0; i < headers.getCount(); i++) {
            String section = headers.getItem(i);

            Adapter adapter = sections.get(section);
            if (adapter != null) {
                int size = adapter.getCount() + 1;

                if (position == 0) {
                    return TYPE_SECTION_HEADER;
                }
                if (position < size) {
                    return type + adapter.getItemViewType(position - 1);
                }

                position -= size;
                type += adapter.getViewTypeCount();
            }
        }

        return -1;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionnum = 0;
        for (int i = 0; i < headers.getCount(); i++) {
            String section = headers.getItem(i);
            Adapter adapter = sections.get(section);
            if (adapter != null) {
                int size = adapter.getCount() + 1;

                if (position == 0) return headers.getView(sectionnum, convertView, parent);
                if (position < size) return adapter.getView(position - 1, convertView, parent);

                position -= size;
                sectionnum++;
            }
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
