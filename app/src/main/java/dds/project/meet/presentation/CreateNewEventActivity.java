package dds.project.meet.presentation;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dds.project.meet.R;
import dds.project.meet.logic.ContactAdapter;
import dds.project.meet.logic.ParticipantAdapter;
import dds.project.meet.logic.RecyclerItemClickListener;
import dds.project.meet.logic.User;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by RaulCoroban on 10/04/2017.
 */

public class CreateNewEventActivity extends BaseActivity {

    public static final int SELECTED_PICTURE = 1;
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
        dataMembers = new ArrayList<User>();
        dataContacts = new ArrayList<>();


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

        //loadMembers();

        setListeners();

        adapterContacts = new ContactAdapter(dataContacts, this);

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
        dataMembers.add(new User("Patricio Orlando", "Aqui", "654765876", "porlando@gmail.com"));
        dataMembers.add(new User("Maria Alpuente", "Alli", "654765876", "malpuente@gmail.com"));
        dataMembers.add(new User("Mario Bros", "Alla", "654765876", "mgomezbors@gmail.com"));
        dataMembers.add(new User("Cameron Luigi", "Alla", "654765876", "mgomezbors@gmail.com"));
        dataMembers.add(new User("Toad Bahilo", "Alla", "654765876", "mgomezbors@gmail.com"));
        dataMembers.add(new User("Diango Bros", "Alla", "654765876", "mgomezbors@gmail.com"));
        dataMembers.add(new User("Esteban Brosa", "Alla", "654765876", "mgomezbors@gmail.com"));
        adapterParticipants.notifyDataSetChanged();
    }

    public void addContactToMembers(User contact) {
        dataMembers.add(contact);
        dataContacts.remove(contact);
        adapterContacts.notifyDataSetChanged();
        adapterParticipants.notifyDataSetChanged();
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
                /*addPersons.clearAnimation();
                dataMembers.add(new User("Patricio Orlando", "Aqui", "654765876", "porlando@gmail.com"));
                adapterParticipants.notifyItemInserted(dataMembers.size());
                participantsNumberLabel.setText(dataMembers.size() + " participants");
                recyclerParticipants.smoothScrollToPosition(dataMembers.size());
                */


                ActivityCompat.requestPermissions(CreateNewEventActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateNewEventActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.contact_list, null);
                mBuilder.setView(mView);

                final AlertDialog dialog = mBuilder.create();

                final RecyclerView recyclerContacts = (RecyclerView) mView.findViewById(R.id.contactRecyclerView);
                recyclerContacts.setHasFixedSize(true);

                LinearLayoutManager layoutManagerContacts = new LinearLayoutManager(CreateNewEventActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerContacts.setLayoutManager(layoutManagerContacts);

                recyclerContacts.setAdapter(adapterContacts);

                recyclerContacts.addOnItemTouchListener(
                        new RecyclerItemClickListener(CreateNewEventActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
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
                whatTimeLabel.setText(time);
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

            result.putString("EXTRA_NAME", name.getEditText().getText().toString());
            result.putInt("EXTRA_DAY", day);
            result.putInt("EXTRA_MONTH", month);
            result.putInt("EXTRA_YEAR", year);
            result.putString("EXTRA_TIME", whatTimeLabel.getText().toString());
            result.putString("EXTRA_ADDRESS", trimLabel(editTextLocation.getText().toString(), 35));
            result.putInt("EXTRA_PART_NUM", dataMembers.size());
            result.putInt("EXTRA_DISTANCE", 34);


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
