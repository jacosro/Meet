package dds.project.meet.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import dds.project.meet.R;

/**
 * Created by RaulCoroban on 07/06/2017.
 */

public class SettingsEventActivity extends AppCompatActivity {

    //UI elements
    private EditText editTextName;
    private EditText editTextLocation;
    private TextView whenLabel;
    private TextView whatTimeLabel;

    //Class fields
    private int day, month, year;
    private String[] months = {"Jan.", "Feb.", "March", "April", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_settings);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        whenLabel = (TextView) findViewById(R.id.whenLabel);
        whatTimeLabel = (TextView) findViewById(R.id.whatTimeLabel);

        Intent fromEvent = getIntent();

        day = fromEvent.getIntExtra("EXTRA_DATE_DAY", 0);
        month = fromEvent.getIntExtra("EXTRA_DATE_MONTH", 0);
        year = fromEvent.getIntExtra("EXTRA_DATE_YEAR", 0);
        editTextName.setText(fromEvent.getStringExtra("EXTRA_NAME"));
        editTextLocation.setText(fromEvent.getStringExtra("EXTRA_LOCATION"));
        whatTimeLabel.setText(fromEvent.getStringExtra("EXTRA_TIME"));
        whenLabel.setText(day + " " + months[month]);

        /*when.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });


        whatTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });*/
    }

}
