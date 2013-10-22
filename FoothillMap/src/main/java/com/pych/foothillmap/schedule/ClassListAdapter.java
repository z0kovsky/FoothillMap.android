package com.pych.foothillmap.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pych.foothillmap.R;
import com.pych.foothillmap.data.FHClass;

import java.util.List;

/**
 * Created by Elena Pychenkova on 30.09.13.
 */
public class ClassListAdapter extends ArrayAdapter<FHClass> {
    Context mContext;

    public ClassListAdapter(Context context, int resource, List<FHClass> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_class_row, null);
        }

        FHClass item = getItem(position);
        if (item != null) {
            TextView tvTime = (TextView) v.findViewById(R.id.list_class_row_time);
            TextView tvTitle = (TextView) v.findViewById(R.id.list_class_row_title);
            TextView tvLocation = (TextView) v.findViewById(R.id.list_class_row_location);

            if (tvTime != null) {
                String time = item.getTimeString();
                if (time == null || time.isEmpty()) {
                    tvTime.setText(mContext.getString(R.string.time_string_empty));
                } else {
                    tvTime.setText(time);
                }
            }
            if (tvTitle != null) {
                tvTitle.setText(item.getTitle());
            }
            if (tvLocation != null) {
                if (item.getLocation() != null && !item.getLocation().isEmpty()) {
                    tvLocation.setText(item.getLocation());
                }
            }
        }
        return v;
    }
}
