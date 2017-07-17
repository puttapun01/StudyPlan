package th.ac.rmutt.comsci.studyplan.Activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import th.ac.rmutt.comsci.studyplan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TableActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewSelectDay;
    private TextView btnPlanSelectTimeStart;
    private TextView btnPlanSelectTimeStop;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        initView();
        initListener();
    }

    private void startDialogSelectTimeStart() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                btnPlanSelectTimeStart.setText(hourOfDay + ":" + minute);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    private void startDialogSelectTimeStop() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                btnPlanSelectTimeStop.setText(hourOfDay + ":" + minute);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    private void startDialogSelectDay() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TableActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_select_day, null);

        final TextView mDay1 = (TextView) mView.findViewById(R.id.textViewDay1);
        final TextView mDay2 = (TextView) mView.findViewById(R.id.textViewDay2);
        final TextView mDay3 = (TextView) mView.findViewById(R.id.textViewDay3);
        final TextView mDay4 = (TextView) mView.findViewById(R.id.textViewDay4);
        final TextView mDay5 = (TextView) mView.findViewById(R.id.textViewDay5);
        final TextView mDay6 = (TextView) mView.findViewById(R.id.textViewDay6);
        final TextView mDay7 = (TextView) mView.findViewById(R.id.textViewDay7);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mDay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String day = mDay1.getText().toString().trim();
                textViewSelectDay.setText(day);
                dialog.dismiss();
            }
        });

        mDay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String day = mDay2.getText().toString().trim();
                textViewSelectDay.setText(day);
                dialog.dismiss();
            }
        });

        mDay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String day = mDay3.getText().toString().trim();
                textViewSelectDay.setText(day);
                dialog.dismiss();
            }
        });

        mDay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String day = mDay4.getText().toString().trim();
                textViewSelectDay.setText(day);
                dialog.dismiss();
            }
        });

        mDay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String day = mDay5.getText().toString().trim();
                textViewSelectDay.setText(day);
                dialog.dismiss();
            }
        });

        mDay6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String day = mDay6.getText().toString().trim();
                textViewSelectDay.setText(day);
                dialog.dismiss();
            }
        });

        mDay7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String day = mDay7.getText().toString().trim();
                textViewSelectDay.setText(day);
                dialog.dismiss();
            }
        });
    }


    private void initView() {
        textViewSelectDay = (TextView) findViewById(R.id.textViewSelectDay);
        btnPlanSelectTimeStart = (TextView) findViewById(R.id.btnPlanSelectTimeStart);
        btnPlanSelectTimeStop = (TextView) findViewById(R.id.btnPlanSelectTimeStop);
    }

    private void initListener() {
        textViewSelectDay.setOnClickListener(this);
        btnPlanSelectTimeStart.setOnClickListener(this);
        btnPlanSelectTimeStop.setOnClickListener(this);

    }

    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        if(v == textViewSelectDay){
            startDialogSelectDay();
        }
        if(v == btnPlanSelectTimeStart){
            startDialogSelectTimeStart();
        }
        if(v == btnPlanSelectTimeStop){
            startDialogSelectTimeStop();
        }
    }

}
