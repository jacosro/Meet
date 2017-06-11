package dds.project.meet.presentation;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dds.project.meet.R;
import dds.project.meet.logic.Card;
import dds.project.meet.logic.ContactAdapter;
import dds.project.meet.logic.ParticipantAdapter;
import dds.project.meet.logic.RecyclerItemClickListener;
import dds.project.meet.logic.User;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by RaulCoroban on 07/06/2017.
 */

public class SettingsEventActivity extends BaseActivity {

    public static final String TAG = "SettingsEventActivity";
    public static final int SELECTED_LOCATION = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    //UI elements
    private EditText editTextNameSettings;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private TextView whenLabel;
    private TextView whatTimeLabel;
    private ImageButton whatTimeButton;
    private ImageButton whenButton;
    private Button cancel;
    private Button done;
    private Button locationButton;
    private Button descriptionButton;
    private Button addPersons;
    private TextView participantsNumberLabel;

    private RecyclerView recyclerParticipants;
    private ParticipantAdapter adapterParticipants;
    private List<User> dataMembers;

    private ContactAdapter adapterContacts;
    private List<User> dataContacts;

    //Class fields
    private int day, month, year;
    private String time;
    private String[] months = {"Jan.", "Feb.", "March", "April", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
    private Card mCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_settings);

        editTextNameSettings = (EditText) findViewById(R.id.editTextName);
        descriptionTextView = (TextView) findViewById(R.id.descrpitionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        whenLabel = (TextView) findViewById(R.id.whenLabel);
        whatTimeLabel = (TextView) findViewById(R.id.whatTimeLabel);
        whatTimeButton = (ImageButton) findViewById(R.id.whatTimeButton);
        whenButton = (ImageButton) findViewById(R.id.whenButton);
        cancel = (Button) findViewById(R.id.cancelButton);
        done = (Button) findViewById(R.id.doneButton);
        locationButton = (Button) findViewById(R.id.locationButton);
        descriptionButton = (Button) findViewById(R.id.descriptionButton);
        recyclerParticipants = (RecyclerView) findViewById(R.id.recyclerParticipantsSettings);
        addPersons = (Button) findViewById(R.id.manageParticipants);
        participantsNumberLabel = (TextView) findViewById(R.id.participantsNumberSettings);



        Intent fromEvent = getIntent();

        // <- TODO Get EventActivity Card + Members

        editTextNameSettings.setText(mCard.getName());
        descriptionTextView.setText(mCard.getDescription());
        locationTextView.setText(mCard.getLocation());
        whenLabel.setText(mCard.getTime());
        whatTimeLabel.setText(mCard.getTime());

        recyclerParticipants.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerParticipants = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerParticipants.setLayoutManager(layoutManagerParticipants);

        adapterParticipants = new ParticipantAdapter(dataMembers, this);
        recyclerParticipants.setAdapter(adapterParticipants);

        recyclerParticipants.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        deleteContactFromMembers(dataMembers.get(position));
                    }
                })
        );

        adapterParticipants.notifyDataSetChanged();

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



        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsEventActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.description_event, null);
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

        addPersons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(SettingsEventActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsEventActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.contact_list, null);
                mBuilder.setView(mView);

                final AlertDialog dialog = mBuilder.create();

                final RecyclerView recyclerContacts = (RecyclerView) mView.findViewById(R.id.contactRecyclerView);
                recyclerContacts.setHasFixedSize(true);

                LinearLayoutManager layoutManagerContacts = new LinearLayoutManager(SettingsEventActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerContacts.setLayoutManager(layoutManagerContacts);

                recyclerContacts.setAdapter(adapterContacts);

                recyclerContacts.addOnItemTouchListener(
                        new RecyclerItemClickListener(SettingsEventActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                addContactToMembers(adapterContacts.get(position));
                                recyclerContacts.setSelected(true);
                                dialog.hide();
                            }
                        })
                );

                if (dataContacts.isEmpty() && dataMembers.isEmpty()) {
                    new AsyncTask<Void, Void, Set<User>>() {

                        @Override
                        protected void onPreExecute() {
                            showProgressDialog();
                        }

                        @Override
                        protected Set<User> doInBackground(Void... params) {
                            return loadContactsFromPhone();
                        }

                        @Override
                        protected void onPostExecute(final Set<User> result) {
                            Persistence.getInstance().userDAO.getAllPhoneNumbers(new QueryCallback<Collection<String>>() {
                                @Override
                                public void result(Collection<String> data) {
                                    for (User user : result) {
                                        Log.d(TAG, "user: " + user.getTelephone());
                                        if (data.contains(user.getTelephone())) {
                                            Log.d(TAG, "data contains " + user.getTelephone());
                                            dataContacts.add(user);
                                        }
                                    }
                                    Log.d(TAG, dataContacts.toString());
                                    adapterContacts.notifyDataSetChanged();
                                    hideProgressDialog();
                                }
                            });
                        }
                    }.execute();
                }

                dialog.show();
            }
        });
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
                        res.add(new User(contactName, "", contactNumber, ""));
                        break;
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

    public void addContactToMembers(User contact) {
        dataMembers.add(contact);
        dataContacts.remove(contact);
        adapterContacts.notifyDataSetChanged();
        adapterParticipants.notifyDataSetChanged();
        participantsNumberLabel.setText(dataMembers.size() + " participants");
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

            finish();
        }
    }

    private boolean constraintsAreOk() {
        editTextNameSettings.setError(null);

        if (editTextNameSettings.length() < 1) {
            editTextNameSettings.setError("Your event must have a name!");
            return false;
        }

        if (mCard.getDescription() == null ) {
            Toast.makeText(this, "Please, set a description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mCard.getLocation() == null ) {
            Toast.makeText(this, "Please, set a location", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mCard.getPersons() <= 0) {
            Toast.makeText(this, "Please, select at least one participant", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mCard.getDateYear() < 2017 ) {
            Toast.makeText(this, "Please, select a date for the event", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mCard.getTime() == null) {
            Toast.makeText(this, "Please, select a time for the event", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
