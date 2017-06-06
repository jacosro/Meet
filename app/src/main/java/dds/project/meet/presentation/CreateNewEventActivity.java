package dds.project.meet.presentation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

    String date, time, uriString;
    ImageView eventImage;
    ImageButton photo, when, whatTime;
    RecyclerView recyclerParticipants;
    Context context = this;

    EditText editTextName, editTextLocation;
    TextView num;

    Boolean timePicked = false;
    Boolean datePicked = false;

    static ArrayList<Participant> dataMembers;
    RecyclerView.LayoutManager layoutManagerParticipants;
    ParticipantAdapter adapterParticipants;

    private static int day, month, year;
    private FloatingActionButton doneFab;

    String[] months = {"Jan.", "Feb.", "March", "April", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
    static final int SELECTED_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        num = (TextView) findViewById(R.id.participantsNumber);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLocation = (EditText) findViewById(R.id.editTextLocation);

        loadMembersRecyclerView();
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
        dataMembers.add(new Participant("Esteban Bros", "Alla", "654765876", "mgomezbors@gmail.com"));
        adapterParticipants.notifyDataSetChanged();
    }

    private void loadMembersRecyclerView() {
        recyclerParticipants = (RecyclerView) findViewById(R.id.recyclerParticipants);
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
                        num.setText(dataMembers.size() + " participants");
                    }
                })
        );

        adapterParticipants.notifyDataSetChanged();
    }

    public void setListeners() {

        when = (ImageButton) findViewById(R.id.whenButton);
        when.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        whatTime = (ImageButton) findViewById(R.id.whatTimeButton);
        whatTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });


        FloatingActionButton done = (FloatingActionButton) findViewById(R.id.doneFAB);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donePressed();
            }
        });

        photo = (ImageButton) findViewById(R.id.photoButton);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                ((Activity) v.getContext()).startActivityForResult(photoPickerIntent, SELECTED_PICTURE);
                //startActivityForResult(i, SELECTED_PICTURE);
            }
        });

        Button cancel = (Button) findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Button addPersons = (Button) findViewById(R.id.manageParticipants);
        addPersons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPersons.clearAnimation();
                dataMembers.add(new Participant("Patricio Orlando", "Aqui", "654765876", "porlando@gmail.com"));
                adapterParticipants.notifyItemInserted(dataMembers.size());
                num.setText(dataMembers.size() + " participants");
                recyclerParticipants.smoothScrollToPosition(dataMembers.size());
            }
        });

        Button searchLocation = (Button) findViewById(R.id.searchLocation);
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout locationEditText = (TextInputLayout) findViewById(R.id.locationEditText);
                try {
                    final String dir = locationEditText.getEditText().getText().toString();
                    if(validateLocation(dir)) {
                        Log.d("LOCATION", "Correct Location");
                        TextInputLayout location = (TextInputLayout) findViewById(R.id.locationEditText);
                        location.getEditText().setText(getExactLocationName(dir));
                    } else {
                        Snackbar retry = Snackbar.make(findViewById(R.id.create_event_layout), "Location not found" , Snackbar.LENGTH_LONG);
                        retry.setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    validateLocation(dir);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        retry.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView num = (TextView) findViewById(R.id.participantsNumber);
        num.setText(dataMembers.size() + " participants");

        doneFab = (FloatingActionButton) findViewById(R.id.doneFAB);
        doneFab.hide();
    }

    public void pickDate() {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog cal = new DatePickerDialog(CreateNewEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                CreateNewEventActivity.day = dayOfMonth;
                CreateNewEventActivity.month = month;
                CreateNewEventActivity.year = year; // <-- TODO Refactor

                date = dayOfMonth + " " + months[month];
                TextView dS = (TextView) findViewById(R.id.whenLabel);
                dS.setText(date);
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
                TextView tS = (TextView) findViewById(R.id.whatTimeLabel);
                tS.setText(time + "h");
                whatTime.setImageResource(R.drawable.clocks);
                timePicked = true;
            }
        },mHour, mMinutes, false);
        cal.show();

        if (datePicked) doneFab.show();
    }

    public boolean validateLocation(String location) throws IOException {
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        if (list.size() >= 1) return true;
        return false;
    }

    public String getExactLocationName(String location) throws IOException {
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        String ret = list.get(0).getAddressLine(0) + " " + list.get(0).getCountryName();
        return ret;
    }



    public void donePressed() {
        TextInputLayout name = (TextInputLayout) findViewById(R.id.nameEditText);
        TextView tS = (TextView) findViewById(R.id.whatTimeLabel);
        TextView dS = (TextView) findViewById(R.id.whenLabel);


        if (constraintsAreOk()) {
            Card newEvent = CardFactory.getCard(tS.getText().toString(), day, month, year, trimLabel(name.getEditText().getText().toString(), 25), trimLabel(editTextLocation.getText().toString(), 35), dataMembers.size(), 34);
            MainActivity.dataCards.add(newEvent); // <- TODO Refactor to startActivityForResult in MainActivity
            MainActivity.adapterCards.notifyDataSetChanged();

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
