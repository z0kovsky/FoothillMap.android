package com.pych.foothillmap.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pych.foothillmap.R;
import com.pych.foothillmap.data.DataHelper;
import com.pych.foothillmap.data.FHClass;
import com.pych.foothillmap.data.FHClassSchedule;

public class FHScheduleFragment extends Fragment {
    static final int CODE_EDIT_INTENT = 1;
    static final String KEY_SCHEDULE_DATA = "com.pych.foothillmap.schedule.data";
    static final String KEY_SCHEDULE_OLD_DATA = "com.pych.foothillmap.schedule.old.data";

    View mView;
    ListView mListView;
    ScheduleListAdapter mScheduleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_schedule, null);

            if (mScheduleAdapter == null) {
                mScheduleAdapter = new ScheduleListAdapter(getActivity());
                mScheduleAdapter.bindSchedule();
            }

            mListView = (ListView) mView.findViewById(R.id.schedule_listview);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showItem(position);
                }
            });

            mListView.setAdapter(mScheduleAdapter);
            mListView.setChoiceMode(1);
            mListView.invalidateViews();
            setHasOptionsMenu(true);
        }

        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fhschedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_item:
                addNewItem(null);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void addNewItem(FHClass item) {
        Activity activity = getActivity();
        Intent intent = new Intent(activity, ClassEditActivity.class);
        if (item != null) {
            intent.putExtra(KEY_SCHEDULE_DATA, (Parcelable) item);
        }
        startActivityForResult(intent, CODE_EDIT_INTENT);
    }

    private void showItem(int position) {
        final FHClass item = (FHClass) mScheduleAdapter.getItem(position);

        final ClassInfoDialogFragment classViewDialog = new ClassInfoDialogFragment();
        classViewDialog.setItem(item);
        classViewDialog.setEditClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classViewDialog.dismiss();
                addNewItem(item);
                mScheduleAdapter.notifyDataSetChanged();
                mListView.invalidateViews();
            }
        });
        classViewDialog.setDeleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classViewDialog.dismiss();
                FHClassSchedule.getSharedSchedule().removeItemByID(item.getID());
                mScheduleAdapter.notifyDataSetChanged();
                mListView.invalidateViews();
            }
        });
        classViewDialog.setShowsDialog(true);
        classViewDialog.show(getFragmentManager(), "classViewDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_EDIT_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                if (b == null) return;
                try {
                    if (b.containsKey(KEY_SCHEDULE_OLD_DATA)) {
                        String oldID = b.getString(KEY_SCHEDULE_OLD_DATA);
                        FHClassSchedule.getSharedSchedule().removeItemByID(oldID);
                    }
                    if (b.containsKey(KEY_SCHEDULE_DATA)) {
                        FHClass item = b.getParcelable(KEY_SCHEDULE_DATA);
                        FHClassSchedule.getSharedSchedule().addItem(item);
                    }

                    mListView.invalidateViews();
                } catch (Exception e) {
                }
            }
        }
    }

    public class ClassInfoDialogFragment extends DialogFragment {
        private View.OnClickListener onEditClickListener;
        private View.OnClickListener onDeleteClickListener;
        private View view;
        private FHClass item = null;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FHScheduleFragment.this.getActivity());
            view = getActivity().getLayoutInflater().inflate(R.layout.dialog_class_view, null);
            builder.setView(view);

            Button btnEdit = (Button) view.findViewById(R.id.view_edit_btn);
            btnEdit.setOnClickListener(onEditClickListener);
            Button btnDelete = (Button) view.findViewById(R.id.view_delete_btn);
            btnDelete.setOnClickListener(onDeleteClickListener);

            if (item != null) {
                TextView tvTitle = (TextView) view.findViewById(R.id.view_title);
                tvTitle.setText(item.getTitle());

                TextView tvTime = (TextView) view.findViewById(R.id.view_time);
                tvTime.setText(item.getTimeString() + ", " + DataHelper.getWeekdayString(item.getWeekday()));

                TextView tvLocation = (TextView) view.findViewById(R.id.view_location);
                tvLocation.setText(item.getLocation());
            }

            return builder.create();
        }


        public void setEditClickListener(View.OnClickListener onClickListener) {
            onEditClickListener = onClickListener;
        }

        public void setDeleteClickListener(View.OnClickListener onClickListener) {
            onDeleteClickListener = onClickListener;
        }

        public void setItem(FHClass item) {
            this.item = item;

            if (view != null) {
                if (item != null) {
                    TextView tvTitle = (TextView) view.findViewById(R.id.view_title);
                    tvTitle.setText(item.getTitle());

                    TextView tvTime = (TextView) view.findViewById(R.id.view_time);
                    tvTime.setText(item.getTimeString() + ", " + DataHelper.getWeekdayString(item.getWeekday()));

                    TextView tvLocation = (TextView) view.findViewById(R.id.view_location);
                    tvLocation.setText(item.getLocation());
                }
            }
        }
    }
}
