package dds.project.meet.presentation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import dds.project.meet.R;

/**
 * Created by RaulCoroban on 07/06/2017.
 */

public class SettingsEventActivity extends AppCompatActivity {

    //UI elements
    private EditText editTextNameSettings;
    private EditText editTextLocation;
    private TextView whenLabel;
    private TextView whatTimeLabel;
    private ImageButton whatTimeButton;
    private ImageButton whenButton;
    private Button cancel;
    private Button done;

    //Class fields
    private int day, month, year;
    private String time;
    private String[] months = {"Jan.", "Feb.", "March", "April", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_settings);

        whenLabel = (TextView) findViewById(R.id.whenLabel);
        whatTimeLabel = (TextView) findViewById(R.id.whatTimeLabel);
        whatTimeButton = (ImageButton) findViewById(R.id.whatTimeButton);
        whenButton = (ImageButton) findViewById(R.id.whenButton);
        cancel = (Button) findViewById(R.id.cancelButton);
        done = (Button) findViewById(R.id.doneButton);


        Intent fromEvent = getIntent();

        day = fromEvent.getIntExtra("EXTRA_DATE_DAY", 0);
        month = fromEvent.getIntExtra("EXTRA_DATE_MONTH", 0);
        year = fromEvent.getIntExtra("EXTRA_DATE_YEAR", 0);
        editTextNameSettings.setText(fromEvent.getStringExtra("EXTRA_NAME"));
        editTextLocation.setText(fromEvent.getStringExtra("EXTRA_LOCATION"));
        whatTimeLabel.setText(fromEvent.getStringExtra("EXTRA_TIME"));
        whenLabel.setText(day + " " + months[month]);

        whenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });


        whatTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack();
            }
        });
    }

    public void pickDate() {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = year;
        int mMonth = month;
        int mDay = day;

        DatePickerDialog cal = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int dayOfMonth) {
                day = dayOfMonth;
                month = newMonth;
                year = newYear;

                String date = dayOfMonth + " " + months[month];
                whenLabel.setText(date);
                whenButton.setImageResource(R.drawable.calendars);
            }
        },mYear, mMonth, mDay);
        cal.show();


    }

    public void pickTime() {

        Calendar mcurrentDate = Calendar.getInstance();
        int mHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        int mMinutes = mcurrentDate.get(Calendar.MINUTE);

        TimePickerDialog cal = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String minuteS = "", hourS = "";

                if(minute < 10) {
                    minuteS =  "0" + minute;
                } else minuteS = minute + "";

                if(hourOfDay < 10) {
                    hourS = "0" + hourOfDay;
                } else hourS = hourOfDay + "";

                time = hourS + ":" + minuteS;
                whatTimeLabel.setText(time + "h");
                whatTimeButton.setImageResource(R.drawable.clocks);
            }
        },mHour, mMinutes, false);
        cal.show();

    }

    private void sendBack() {
        if (constraintsAreOk()) {

            Bundle result = new Bundle();

            result.putString("EXTRA_NAME", editTextNameSettings.getText().toString());
            result.putInt("EXTRA_DAY", day);
            result.putInt("EXTRA_MONTH", month);
            result.putInt("EXTRA_YEAR", year);
            result.putString("EXTRA_TIME", time);
            result.putString("EXTRA_ADDRESS", editTextLocation.getText().toString());


            Intent intent = new Intent();
            intent.putExtras(result);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private boolean constraintsAreOk() {
        editTextNameSettings.setError(null);
        editTextLocation.setError(null);

        if (editTextNameSettings.length() < 1) {
            editTextNameSettings.setError("Your event must have a name!");
            return false;
        }
        if (editTextLocation.length() < 1) {
            editTextLocation.setError("Your event must have a location!");
            return false;
        }

        return true;
    }

}
