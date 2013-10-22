package com.pych.foothillmap.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pych.foothillmap.R;
import com.pych.foothillmap.data.DataHelper;
import com.pych.foothillmap.data.FHClass;
import com.pych.foothillmap.data.FHLocationCatalog;

import java.util.Date;

public class ClassEditActivity extends FragmentActivity {
    EditText mEditTitle;
    AutoCompleteTextView mEditLocation;
    EditText mEditTime;
    EditText mEditWeekday;

    FHClass item = null;
    String oldItemID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_edit);

        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mEditLocation = (AutoCompleteTextView) findViewById(R.id.edit_location);
        mEditTime = (EditText) findViewById(R.id.edit_time);
        mEditWeekday = (EditText) findViewById(R.id.edit_weekday);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                FHLocationCatalog.getSharedCatalog().getLocationTitles());
        mEditLocation.setAdapter(adapter);

        mEditTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date dt = item.getTime();
                int hours = dt == null ? 8 : dt.getHours();
                int minutes = dt == null ? 0 : dt.getMinutes();

                TimePickerDialog dlg = new TimePickerDialog(ClassEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                        Date dt = new Date();
                        dt.setHours(hours);
                        dt.setMinutes(minutes);
                        item.setTime(dt);
                        mEditTime.setText(item.getTimeString());
                    }
                }, hours, minutes, DateFormat.is24HourFormat(ClassEditActivity.this));

                dlg.setTitle(getString(R.string.time_pick_title));
                dlg.show();
            }
        });

        mEditWeekday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) ClassEditActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(ClassEditActivity.this.getCurrentFocus().getWindowToken(), 0);

                WeekdayDialogFragment weekdayDialog = new WeekdayDialogFragment();
                weekdayDialog.setClickListener(new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        item.setWeekday(which);
                        mEditWeekday.setText(DataHelper.getWeekdayString(item.getWeekday()));
                    }
                });
                weekdayDialog.show(getSupportFragmentManager(), "weekday");

            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            FHClass temp_item = b.getParcelable(FHScheduleFragment.KEY_SCHEDULE_DATA);
            setTitle("Edit class");
            if (temp_item != null) {
                oldItemID = temp_item.getID();
                item = new FHClass(
                        temp_item.getTitle(),
                        temp_item.getLocation(),
                        temp_item.getTime(),
                        temp_item.getWeekday());
                mEditTitle.setText(item.getTitle());
                mEditLocation.setText(item.getLocation());
                mEditWeekday.setText(DataHelper.getWeekdayString(item.getWeekday()));
                mEditTime.setText(item.getTimeString());
            }
        }
        if (item == null) {
            setTitle("Create new class");
            Date dt = new Date();
            dt.setHours(8);
            dt.setMinutes(0);
            item = new FHClass("", "", dt, 1);
        }
        setupActionBar();
    }

    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.class_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.menu_edit_cancel:
                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(RESULT_CANCELED, intent);
                } else {
                    getParent().setResult(RESULT_CANCELED, intent);
                }

                finish();
                break;
            case R.id.menu_edit_done:
                saveClass();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void saveClass() {
        if (mEditTitle.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.warning_msg_empty_title, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditWeekday.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.warning_msg_empty_weekday, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditTime.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.warning_msg_empty_time, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            item.setTitle(mEditTitle.getText().toString());
            item.setLocation(mEditLocation.getText().toString());

            Intent intent = new Intent();
            intent.putExtra(FHScheduleFragment.KEY_SCHEDULE_DATA, (Parcelable) item);
            if (oldItemID != null && !oldItemID.isEmpty()) {
                intent.putExtra(FHScheduleFragment.KEY_SCHEDULE_OLD_DATA, oldItemID);
            }

            setResult(RESULT_OK, intent);

            finish();
        } catch (Exception ex) {
        }
    }

    public void onClick(View view) {
        saveClass();
    }

    public class WeekdayDialogFragment extends DialogFragment {
        private DialogInterface.OnClickListener onClick;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ClassEditActivity.this);

            builder.setTitle(getString(R.string.weekday_pick_title))
                    .setItems(DataHelper.dayNames, onClick);

            return builder.create();
        }

        public void setClickListener(DialogInterface.OnClickListener onClickListener) {
            onClick = onClickListener;
        }
    }
}
