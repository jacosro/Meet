package dds.project.meet.presentation;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dds.project.meet.R;
import dds.project.meet.logic.adapters.ContactAdapter;
import dds.project.meet.logic.adapters.ParticipantAdapter;
import dds.project.meet.logic.entities.Event;
import dds.project.meet.logic.entities.User;
import dds.project.meet.logic.util.EventFactory;
import dds.project.meet.logic.util.RecyclerItemClickListener;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by RaulCoroban on 10/04/2017.
 */

public class CreateNewEventActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final int SELECTED_PICTURE = 1;
    public static final int SELECTED_LOCATION = 2;
    public static final String TAG = "CreateNewEventActivity";
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    // UI Elements
    private EditText editTextName, editTextLocation;
    private TextView participantsNumberLabel;
    private TextInputLayout location;
    private TextInputLayout name;
    private TextView whenLabel;
    private TextView whatTimeLabel;

    private RecyclerView recyclerParticipants;
    private ParticipantAdapter adapterParticipants;
    private List<User> dataMembers;

    private ContactAdapter adapterContacts;
    private List<User> dataContacts;
    private ProgressBar mProgressBar;

    private FloatingActionButton doneFab;
    private Button cancel;
    private Button addPersons;
    private Button locationButton;
    private Button descriptionButton;

    private User mUser;


    //Class fields
    private String uriString;
    private ImageButton when, whatTime;
    private Boolean timePicked = false;
    private Boolean datePicked = false;
    private Event mEvent;

    private String[] months = {"Jan.", "Feb.", "March", "April", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        participantsNumberLabel = (TextView) findViewById(R.id.participantsNumber);
        editTextName = (EditText) findViewById(R.id.editTextName);
        when = (ImageButton) findViewById(R.id.whenButton);
        whatTime = (ImageButton) findViewById(R.id.whatTimeButton);
        cancel = (Button) findViewById(R.id.cancelButton);
        recyclerParticipants = (RecyclerView) findViewById(R.id.recyclerParticipants);
        doneFab = (FloatingActionButton) findViewById(R.id.doneFAB);
        addPersons = (Button) findViewById(R.id.manageParticipants);
        locationButton = (Button) findViewById(R.id.locationButton);
        descriptionButton = (Button) findViewById(R.id.descriptionButton);
        location = (TextInputLayout) findViewById(R.id.locationEditText);
        whenLabel = (TextView) findViewById(R.id.whenLabel);
        whatTimeLabel= (TextView) findViewById(R.id.whatTimeLabel);
        name = (TextInputLayout) findViewById(R.id.nameEditText);

        mUser = mPersistence.userDAO.getCurrentUser();

        dataMembers = new ArrayList<>();
        dataMembers.add(mUser);

        dataContacts = new ArrayList<>();
        adapterContacts = new ContactAdapter(dataContacts, this);

        mEvent = EventFactory.getEmptyCard();

        doneFab.hide();

        setListeners();
        initializeRecyclerView();
        getPermissions();
    }

    private void setListeners() {


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


        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donePressed();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        addPersons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateNewEventActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.create_event_and_settings_contact_list, null);
                mBuilder.setView(mView);

                mProgressBar = (ProgressBar) mView.findViewById(R.id.contact_list_progressBar);
                mProgressBar.setVisibility(View.INVISIBLE);
                final AlertDialog dialog = mBuilder.create();

                final RecyclerView recyclerContacts = (RecyclerView) mView.findViewById(R.id.contactRecyclerView);
                recyclerContacts.setHasFixedSize(true);

                LinearLayoutManager layoutManagerContacts = new LinearLayoutManager(CreateNewEventActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerContacts.setLayoutManager(layoutManagerContacts);

                recyclerContacts.setAdapter(adapterContacts);

                recyclerContacts.addOnItemTouchListener(
                        new RecyclerItemClickListener(CreateNewEventActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                addContactToMembers(dataContacts.get(position));
                                recyclerContacts.setSelected(true);
                                dialog.hide();
                            }
                        })
                );

                if (dataContacts.isEmpty() && dataMembers.size() == 1) {
                    new AsyncTask<Void, Void, Set<User>>() {

                        @Override
                        protected void onPreExecute() {
                            mProgressBar.setIndeterminate(true);
                            mProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected Set<User> doInBackground(Void... params) {
                            return loadContactsFromPhone();
                        }

                        @Override
                        protected void onPostExecute(final Set<User> result) {
                            mPersistence.userDAO.getAllUsers(new QueryCallback<Collection<User>>() {
                                @Override
                                public void result(Collection<User> data) {
                                    for (User user : result) {
                                        for (User userDB : data) {
                                            Log.d(TAG, user.getTelephone() + " == " + userDB.getTelephone());
                                            if (userDB.getTelephone().equals(user.getTelephone()) && !userDB.equals(mUser)) {
                                                User toDataContacts = new User(user.getName(), userDB.getUsername(), user.getTelephone(), userDB.getEmail());
                                                toDataContacts.setUid(userDB.getUid());
                                                dataContacts.add(toDataContacts);
                                                adapterContacts.notifyItemInserted(dataContacts.size() - 1);
                                            }
                                        }
                                    }
                                    Log.d(TAG, dataContacts.toString());
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }.execute();
                }

                dialog.show();
            }
        });


        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateNewEventActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.create_event_and_settings_description, null);
                mBuilder.setView(mView);

                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });

                mView.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                        descriptionButton.setText("Change Description");
                        mEvent.setDescription(((TextInputLayout) mView.findViewById(R.id.descriptionEditText)).getEditText().getText().toString());
                    }
                });


            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(CreateNewEventActivity.this), SELECTED_LOCATION);
                    locationButton.setText("Change Location");
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Repairable " + e);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Services not available " + e);
                }
            }
        });


        participantsNumberLabel.setText(dataMembers.size() + " participants");

    }

    private void initializeRecyclerView() {
        recyclerParticipants.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerParticipants = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerParticipants.setLayoutManager(layoutManagerParticipants);

        adapterParticipants = new ParticipantAdapter(dataMembers, this);
        recyclerParticipants.setAdapter(adapterParticipants);

        recyclerParticipants.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        User user = dataMembers.get(position);

                        if (!mUser.equals(user)) {
                            deleteContactFromMembers(user);
                        }
                    }
                })
        );

        adapterParticipants.notifyDataSetChanged();
    }

    //Auxiliar methods
    public void getPermissions() {
        ActivityCompat.requestPermissions(CreateNewEventActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    public void addContactToMembers(User contact) {
        dataMembers.add(contact);
        dataContacts.remove(contact);
        adapterParticipants.notifyItemInserted(dataMembers.size() - 1);
        adapterContacts.notifyDataSetChanged();
        participantsNumberLabel.setText(dataMembers.size() + " participants");
    }

    public void deleteContactFromMembers(User contact) {
        dataMembers.remove(contact);

        Set<User> sorted = new TreeSet<>(dataContacts);
        sorted.add(contact);

        dataContacts.clear();
        dataContacts.addAll(sorted);

        adapterContacts.notifyDataSetChanged();
        adapterParticipants.notifyDataSetChanged();
        participantsNumberLabel.setText(dataMembers.size() + " participants");
    }

    private Set<User> loadContactsFromPhone() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        Set<User> res = new TreeSet<>();
        if(cursor != null)
        {
            while(cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactNumber = arrangeNumber(contactNumber);
                        String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME));
                        res.add(new User(contactName, contactName, contactNumber, ""));
                    }
                    pCur.close();
                }

            }
            cursor.close();
        }
        return res;
    }

    private String arrangeNumber(String contactNumber) {
        return contactNumber.replaceAll("\\s+", "").replaceAll("-", "").replaceAll("\\+[0-9][0-9]", "");
    }

    public void pickDate() {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog cal = new DatePickerDialog(CreateNewEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int dayOfMonth) {
                mEvent.setDateDay(dayOfMonth);
                mEvent.setDateMonth(newMonth);
                mEvent.setDateYear(newYear);

                whenLabel.setText(dayOfMonth + " " + months[newMonth]);
                when.setImageResource(R.drawable.icon_calendar_selected);
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

                String time = hourS + ":" + minuteS;
                mEvent.setTime(time);
                whatTimeLabel.setText(time);
                whatTime.setImageResource(R.drawable.icon_clock_selected);
                timePicked = true;
            }
        },mHour, mMinutes, false);
        cal.show();

        if (datePicked) doneFab.show();
    }

    public void donePressed() {
        Log.d(TAG, "done pressed");

        mEvent.setPersons(dataMembers.size());
        mEvent.setParticipants(dataMembers);

        if (constraintsAreOk()) {
            mEvent.setName(editTextName.getText().toString());
            mPersistence.eventDAO.addEvent(mEvent, new QueryCallback<Boolean>() {
                @Override
                public void result(Boolean data) {
                    Log.d(TAG, "Add card " + data);
                    finish();
                }
            });
        }
    }

    private boolean constraintsAreOk() {
        editTextName.setError(null);

        if (editTextName.length() < 1) {
            editTextName.setError("Your event must have a name!");
            return false;
        }

        if (editTextName.length() > 25) {
            editTextName.setError("Event name too long! (Max. 25)");
            return false;
        }

        if (mEvent.getDescription() == null ) {
            Toast.makeText(this, "Please, set a description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mEvent.getLocation() == null ) {
            Toast.makeText(this, "Please, set a location", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mEvent.getPersons() <= 0) {
            Toast.makeText(this, "Please, select at least one create_event_participant", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mEvent.getDateYear() < 2017 ) {
            Toast.makeText(this, "Please, select a date for the event", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mEvent.getTime() == null) {
            Toast.makeText(this, "Please, select a time for the event", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public double calculateDistanceBetween(LatLng latLng1, LatLng latLng2) {
        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        Location loc2 = new Location(LocationManager.GPS_PROVIDER);

        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);


        return loc1.distanceTo(loc2)/1000;
    }

    //Waiting for result
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Could not calculate distance");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Place place = null;

        if(requestCode == SELECTED_LOCATION && data != null) {
            if(resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);
                mEvent.setLocation(place.getAddress().toString());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Log.d(TAG, "Place not found");
                Toast.makeText(CreateNewEventActivity.this, "Cannot find place :(", Toast.LENGTH_SHORT).show();
            }
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            mGoogleApiClient.connect();

            final Place placeForDistance = place;

            ActivityCompat.requestPermissions(CreateNewEventActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission denied for Access Fine Location");
                return;
            }

            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    LatLng myLoca = likelyPlaces.get(0).getPlace().getLatLng();
                    double distance = calculateDistanceBetween(myLoca, placeForDistance.getLatLng());
                    mEvent.setKm((int) distance);

                    likelyPlaces.release();
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }
}
