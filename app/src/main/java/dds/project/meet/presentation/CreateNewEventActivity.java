package dds.project.meet.presentation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dds.project.meet.R;
import dds.project.meet.logic.Card;
import dds.project.meet.logic.CardFactory;
import dds.project.meet.logic.Participant;
import dds.project.meet.logic.ParticipantAdapter;
import dds.project.meet.logic.RecyclerItemClickListener;

/**
 * Created by RaulCoroban on 10/04/2017.
 */

public class CreateNewEventActivity extends AppCompatActivity {

    public static final int SELECTED_PICTURE = 1;
    public static final String TAG = "CreateNewEventActivity";

    // UI Elements
    private EditText editTextName, editTextLocation;
    private TextView participantsNumberLabel;
    private RecyclerView recyclerParticipants;
    private TextInputLayout location;
    private TextInputLayout name;
    private TextView whenLabel;
    private TextView whatTimeLabel;
    public static ArrayList<Participant> dataMembers;
    private RecyclerView.LayoutManager layoutManagerParticipants;
    private ParticipantAdapter adapterParticipants;

    private FloatingActionButton doneFab;
    private Button cancel;
    private Button addPersons;
    private Button searchLocation;


    //Class fields
    private String date, time, uriString;
    private ImageButton photo, when, whatTime;
    private Boolean timePicked = false;
    private Boolean datePicked = false;

    private int day, month, year;
    private String[] months = {"Jan.", "Feb.", "March", "April", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        participantsNumberLabel = (TextView) findViewById(R.id.participantsNumber);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        when = (ImageButton) findViewById(R.id.whenButton);
        whatTime = (ImageButton) findViewById(R.id.whatTimeButton);
        photo = (ImageButton) findViewById(R.id.photoButton);
        cancel = (Button) findViewById(R.id.cancelButton);
        recyclerParticipants = (RecyclerView) findViewById(R.id.recyclerParticipants);
        doneFab = (FloatingActionButton) findViewById(R.id.doneFAB);
        addPersons = (Button) findViewById(R.id.manageParticipants);
        searchLocation = (Button) findViewById(R.id.searchLocation);
        location = (TextInputLayout) findViewById(R.id.locationEditText);
        whenLabel = (TextView) findViewById(R.id.whenLabel);
        whatTimeLabel= (TextView) findViewById(R.id.whatTimeLabel);
        name = (TextInputLayout) findViewById(R.id.nameEditText);
        dataMembers = new ArrayList<Participant>();


        recyclerParticipants.setHasFixedSize(true);

        layoutManagerParticipants = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerParticipants.setLayoutManager(layoutManagerParticipants);

        adapterParticipants = new ParticipantAdapter(dataMembers, this);
        recyclerParticipants.setAdapter(adapterParticipants);

        recyclerParticipants.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Participant p = dataMembers.get(position);
                        dataMembers.remove(position);
                        adapterParticipants.notifyDataSetChanged();
                        participantsNumberLabel.setText(dataMembers.size() + " participants");
                    }
                })
        );

        adapterParticipants.notifyDataSetChanged();

        loadMembers();

        setListeners();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            uriString = uri.toString();
            photo.setImageURI(Uri.parse(uriString));
            photo.setImageResource(R.drawable.cameras);
        }
    }

    public void loadMembers() {
        dataMembers.add(new Participant("Patricio Orlando", "Aqui", "654765876", "porlando@gmail.com"));
        dataMembers.add(new Participant("Maria Alpuente", "Alli", "654765876", "malpuente@gmail.com"));
        dataMembers.add(new Participant("Mario Bros", "Alla", "654765876", "mgomezbors@gmail.com"));
        dataMembers.add(new Participant("Cameron Luigi", "Alla", "654765876", "mgomezbors@gmail.com"));
        dataMembers.add(new Participant("Toad Bahilo", "Alla", "654765876", "mgomezbors@gmail.com"));
        dataMembers.add(new Participant("Diango Bros", "Alla", "654765876", "mgomezbors@gmail.com"));
        dataMembers.add(new Participant("Esteban Brosa", "Alla", "654765876", "mgomezbors@gmail.com"));
        adapterParticipants.notifyDataSetChanged();
    }


    public void setListeners() {


        when.setOnClickListener(new View.OnClickListener() {
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
        });


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                ((Activity) v.getContext()).startActivityForResult(photoPickerIntent, SELECTED_PICTURE);
            }
        });



        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donePressed();
            }
        });

        doneFab.hide();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        addPersons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPersons.clearAnimation();
                dataMembers.add(new Participant("Patricio Orlando", "Aqui", "654765876", "porlando@gmail.com"));
                adapterParticipants.notifyItemInserted(dataMembers.size());
                participantsNumberLabel.setText(dataMembers.size() + " participants");
                recyclerParticipants.smoothScrollToPosition(dataMembers.size());
            }
        });


        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String dir = location.getEditText().getText().toString();
                    String searchResult = getExactLocationName(dir);
                    if (searchResult != null) {
                        location.getEditText().setText(searchResult);
                    } else {
                        Snackbar retry = Snackbar.make(findViewById(R.id.create_event_layout), "Location not found", Snackbar.LENGTH_LONG);
                        retry.setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    getExactLocationName(dir);
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        });
                        retry.show();
                    }
                } catch (IOException e) {
                    Log.e(TAG,e.getMessage());
                }
            }
        });


        participantsNumberLabel.setText(dataMembers.size() + " participants");

    }

    public void pickDate() {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog cal = new DatePickerDialog(CreateNewEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int dayOfMonth) {
                day = dayOfMonth;
                month = newMonth;
                year = newYear;

                date = dayOfMonth + " " + months[month];
                whenLabel.setText(date);
                when.setImageResource(R.drawable.calendars);
                datePicked = true;
            }
        },mYear, mMonth, mDay);
        cal.show();

        if (timePicked) doneFab.show();
    }

    public void pickTime() {

        Calendar mcurrentDate = Calendar.getInstance();
        int mHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        int mMinutes = mcurrentDate.get(Calendar.MINUTE);

        TimePickerDialog cal = new TimePickerDialog(CreateNewEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                whatTime.setImageResource(R.drawable.clocks);
                timePicked = true;
            }
        },mHour, mMinutes, false);
        cal.show();

        if (datePicked) doneFab.show();
    }

    public String getExactLocationName(String location) throws IOException {
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        if (list.size() >= 1) return list.get(0).getAddressLine(0) + " " + list.get(0).getCountryName();
        return null;
    }


    public void donePressed() {
        if (constraintsAreOk()) {

            Bundle result = new Bundle();

            result.putString("name", name.getEditText().getText().toString());
            result.putInt("day", day);
            result.putInt("day", month);
            result.putInt("day", year);
            result.putString("whatTimeLabel", whatTimeLabel.getText().toString());
            result.putString("address", trimLabel(editTextLocation.getText().toString(), 35));
            result.putInt("participantsNum", dataMembers.size());
            result.putInt("distance", 34);


            Intent intent = new Intent();
            intent.putExtras(result);
            setResult(Activity.RESULT_OK, intent);

            finish();
        }


    }

    private String trimLabel(String s, int limit) {
        if(s.length() >= limit) {
            return s.substring(0, limit);
        }
        else return s;
    }

    private boolean constraintsAreOk() {
        editTextName.setError(null);
        editTextLocation.setError(null);

        if (editTextName.length() < 1) {
            editTextName.setError("Your event must have a name!");
            return false;
        }
        if (editTextLocation.length() < 1) {
            editTextLocation.setError("Your event must have a location!");
            return false;
        }

        if (!timePicked || !datePicked) return false;

        return true;
    }


}
