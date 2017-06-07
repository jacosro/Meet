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
    private String day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_settings);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        whenLabel = (TextView) findViewById(R.id.whenLabel);
        whatTimeLabel = (TextView) findViewById(R.id.whatTimeLabel);

        Intent fromEvent = getIntent();

        editTextName.setText(fromEvent.getStringExtra("EXTRA_NAME"));
        editTextLocation.setText(fromEvent.getStringExtra("EXTRA_LOCATION"));
        day = fromEvent.getStringExtra("EXTRA_DATE_DAY");
        month = fromEvent.getStringExtra("EXTRA_DATE_MONTH");
        year = fromEvent.getStringExtra("EXTRA_DATE_YEAR");
        whatTimeLabel.setText(fromEvent.getStringExtra("EXTRA_TIME"));

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
