package dds.project.meet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mikepenz.materialdrawer.Drawer;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by RaulCoroban on 10/04/2017.
 */

public class CreateNewEvent extends AppCompatActivity {

    String date, time, uriString;
    ImageView eventImage;
    ImageButton buttonImage;

    boolean bool = true;

    String[] months = {"JAN", "FEB", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"};
    static final int SELECTED_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        ImageButton when = (ImageButton) findViewById(R.id.whenButton);
        when.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        ImageButton whatTime = (ImageButton) findViewById(R.id.whatTimeButton);
        whatTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });

        Button done = (Button) findViewById(R.id.doneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donePressed();
            }
        });

        buttonImage = (ImageButton) findViewById(R.id.addImageButton);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECTED_PICTURE);

            }
        });

        Button cancel = (Button) findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nameEditText.setText("");
                return false;
            }
        });

        final EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
        locationEditText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                locationEditText.setText("");
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            uriString = uri.toString();
            buttonImage.setImageURI(Uri.parse(uriString));
        }
    }

    public void pickDate() {

        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog cal = new DatePickerDialog(CreateNewEvent.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + " " + months[month];
                TextView dS = (TextView) findViewById(R.id.dateSelection);
                dS.setText(date);
            }
        },mYear, mMonth, mDay);
        cal.show();
    }

    public void pickTime() {

        Calendar mcurrentDate = Calendar.getInstance();
        int mHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        int mMinutes = mcurrentDate.get(Calendar.MINUTE);

        TimePickerDialog cal = new TimePickerDialog(CreateNewEvent.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time = hourOfDay + ":" + minute;
                TextView tS = (TextView) findViewById(R.id.timeSelection);
                tS.setText(time);
            }
        },mHour, mMinutes, false);
        cal.show();
    }

    public void donePressed() {
        EditText name = (EditText) findViewById(R.id.nameEditText);
        EditText location = (EditText) findViewById(R.id.locationEditText);
        TextView tS = (TextView) findViewById(R.id.timeSelection);
        TextView dS = (TextView) findViewById(R.id.dateSelection);
        ImageView eventImageView = (ImageView) findViewById(R.id.eventImageView);

        Card newEvent = CardFactory.getCard(tS.getText().toString(), "TODAY" , name.getText().toString(), location.getText().toString(), 0, 0, uriString);
        MainActivity.dataCards.add(newEvent);
        MainActivity.adapterCards.notifyDataSetChanged();
        finish();
    }
}
