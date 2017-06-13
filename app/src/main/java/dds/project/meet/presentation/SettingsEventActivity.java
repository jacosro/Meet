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
import dds.project.meet.logic.entities.Card;
import dds.project.meet.logic.entities.User;
import dds.project.meet.logic.util.CardFactory;
import dds.project.meet.logic.util.RecyclerItemClickListener;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by RaulCoroban on 07/06/2017.
 */

public class SettingsEventActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "SettingsEventActivity";
    public static final int SELECTED_LOCATION = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    // UI Elements
    private EditText editTextName;
    private TextView descriptionTextView;
    private TextView participantsNumberLabel;
    private TextView locationTextView;
    private TextView whenLabel;
    private TextView whatTimeLabel;

    private RecyclerView recyclerParticipants;
    private ParticipantAdapter adapterParticipants;
    private List<User> dataMembers;

    private ContactAdapter adapterContacts;
    private List<User> dataContacts;
    private ProgressBar mProgressBar;

    private Button cancelButton;
    private Button manageParticipants;
    private Button locationButton;
    private Button descriptionButton;

    private Button doneButton;

    private User mUser;
    private ImageButton whenButton, whatTimeButton;

    //Class fields
    private Card mCard;
    private String[] months = {"Jan.", "Feb.", "March", "April", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_settings);
        mCard = new Card();

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        editTextName = (EditText) findViewById(R.id.editTextName);
        descriptionTextView = (TextView) findViewById(R.id.descrpitionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        locationButton = (Button) findViewById(R.id.locationButton);
        descriptionButton = (Button) findViewById(R.id.descriptionButton);
        recyclerParticipants = (RecyclerView) findViewById(R.id.recyclerParticipants);
        manageParticipants = (Button) findViewById(R.id.manageParticipants);
        whenButton = (ImageButton) findViewById(R.id.whenButton);
        whenLabel = (TextView) findViewById(R.id.whenLabel);
        whatTimeButton = (ImageButton) findViewById(R.id.whatTimeButton);
        whatTimeLabel = (TextView) findViewById(R.id.whatTimeLabel);
        participantsNumberLabel = (TextView) findViewById(R.id.participantsNumberLabel);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        doneButton = (Button) findViewById(R.id.doneButton);

        mPersistence.cardDAO.findCardByKey(key, new QueryCallback<Card>() {
            @Override
            public void result(Card data) {
                mCard = data;

                editTextName.setText(mCard.getName());
                descriptionTextView.setText(mCard.getDescription());
                locationTextView.setText(mCard.getLocation());
                whenLabel.setText(mCard.getDateDay() + " " + months[mCard.getDateMonth()]);
                whatTimeLabel.setText(mCard.getTime());
                dataMembers = mCard.getParticipants();

                recyclerParticipants.setHasFixedSize(true);

                RecyclerView.LayoutManager layoutManagerParticipants = new LinearLayoutManager(SettingsEventActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerParticipants.setLayoutManager(layoutManagerParticipants);

                adapterParticipants = new ParticipantAdapter(dataMembers, SettingsEventActivity.this);
                recyclerParticipants.setAdapter(adapterParticipants);

                adapterParticipants.notifyDataSetChanged();

                recyclerParticipants.addOnItemTouchListener(
                        new RecyclerItemClickListener(SettingsEventActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User user = dataMembers.get(position);

                                if (!mUser.equals(user)) {
                                    deleteContactFromMembers(user);
                                }
                            }
                        })
                );

            }
        });


        mUser = mPersistence.userDAO.getCurrentUser();

        dataMembers = new ArrayList<>();
        dataMembers.add(mUser);
        dataContacts = new ArrayList<>();

        mCard = CardFactory.getEmptyCard();

        setListeners();

        adapterContacts = new ContactAdapter(dataContacts, this);
    }

    public void setListeners() {


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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        manageParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsEventActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.create_event_and_settings_contact_list, null);
                mBuilder.setView(mView);

                mProgressBar = (ProgressBar) mView.findViewById(R.id.contact_list_progressBar);
                mProgressBar.setVisibility(View.INVISIBLE);
                final AlertDialog dialog = mBuilder.create();

                final RecyclerView recyclerContacts = (RecyclerView) mView.findViewById(R.id.contactRecyclerView);
                recyclerContacts.setHasFixedSize(true);

                LinearLayoutManager layoutManagerContacts = new LinearLayoutManager(SettingsEventActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerContacts.setLayoutManager(layoutManagerContacts);

                recyclerContacts.setAdapter(adapterContacts);

                recyclerContacts.addOnItemTouchListener(
                        new RecyclerItemClickListener(SettingsEventActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsEventActivity.this);
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
                        mCard.setDescription(((TextInputLayout) mView.findViewById(R.id.descriptionEditText)).getEditText().getText().toString());
                        refreshUI();
                    }
                });


            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(SettingsEventActivity.this);
                    startActivityForResult(intent, SELECTED_LOCATION);
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

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donePressed();
            }
        });


        participantsNumberLabel.setText(dataMembers.size() + " participants");
    }

    //Auxiliar Methods
    public void addContactToMembers(User contact) {
        dataMembers.add(contact);
        dataContacts.remove(contact);
        adapterParticipants.notifyItemInserted(dataMembers.size() - 1);
        adapterContacts.notifyDataSetChanged();
        refreshUI();
    }

    public void deleteContactFromMembers(User contact) {
        dataMembers.remove(contact);

        Set<User> sorted = new TreeSet<>(dataContacts);
        sorted.add(contact);

        dataContacts.clear();
        dataContacts.addAll(sorted);

        adapterContacts.notifyDataSetChanged();
        adapterParticipants.notifyDataSetChanged();
        refreshUI();
    }

    private String arrangeNumber(String contactNumber) {
        return contactNumber.replaceAll("\\s+", "").replaceAll("-", "").replaceAll("\\+[0-9][0-9]", "");
    }

    public void pickDate() {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog cal = new DatePickerDialog(SettingsEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int dayOfMonth) {
                mCard.setDateDay(dayOfMonth);
                mCard.setDateMonth(newMonth);
                mCard.setDateYear(newYear);
                refreshUI();
            }
        }, mYear, mMonth, mDay);
        cal.show();

    }

    public void pickTime() {

        Calendar mcurrentDate = Calendar.getInstance();
        int mHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        int mMinutes = mcurrentDate.get(Calendar.MINUTE);

        TimePickerDialog cal = new TimePickerDialog(SettingsEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String minuteS = "", hourS = "";

                if (minute < 10) {
                    minuteS = "0" + minute;
                } else minuteS = minute + "";

                if (hourOfDay < 10) {
                    hourS = "0" + hourOfDay;
                } else hourS = hourOfDay + "";

                String time = hourS + ":" + minuteS;
                mCard.setTime(time);
                refreshUI();

            }
        }, mHour, mMinutes, false);
        cal.show();

        refreshUI();
    }

    private Set<User> loadContactsFromPhone() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        Set<User> res = new TreeSet<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
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

    public void donePressed() {
        Log.d(TAG, "done pressed");

        mCard.setPersons(dataMembers.size());
        mCard.setParticipants(dataMembers);

        if (constraintsAreOk()) {
            mCard.setName(editTextName.getText().toString());
            mPersistence.cardDAO.updateCard(mCard, new QueryCallback<Boolean>() {
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

        if (mCard.getDescription() == null) {
            Toast.makeText(this, "Please, set a description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mCard.getLocation() == null) {
            Toast.makeText(this, "Please, set a locationTextView", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mCard.getPersons() <= 0) {
            Toast.makeText(this, "Please, select at least one create_event_participant", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mCard.getDateYear() < 2017) {
            Toast.makeText(this, "Please, select a date for the event", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mCard.getTime() == null) {
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


        return loc1.distanceTo(loc2) / 1000;
    }

    private void refreshUI() {
        descriptionTextView.setText(mCard.getDescription());
        locationTextView.setText(mCard.getLocation());
        whatTimeLabel.setText(mCard.getTime());
        whatTimeButton.setImageResource(R.drawable.icon_clock_selected);
        whenLabel.setText(mCard.getDateDay() + " " + months[mCard.getDateMonth()]);
        whenButton.setImageResource(R.drawable.icon_calendar_selected);
        participantsNumberLabel.setText(dataMembers.size() + " PARTICIPANTS");
    }

    //Waiting for result
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Place place = null;

        if(requestCode == SELECTED_LOCATION && data != null) {
            if(resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                mCard.setLocation(place.getAddress().toString());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Log.d(TAG, "Place not found");
                Toast.makeText(SettingsEventActivity.this, "Cannot find place :(", Toast.LENGTH_SHORT).show();
            }

            if (place != null) {
                GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, this)
                        .addApi(LocationServices.API)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .build();
                mGoogleApiClient.connect();

                final Place placeForDistance = place;

                ActivityCompat.requestPermissions(SettingsEventActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
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
                        mCard.setKm((int) distance);

                        likelyPlaces.release();
                    }
                });
                refreshUI();
            }

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Could not calculate distance");
    }

}
