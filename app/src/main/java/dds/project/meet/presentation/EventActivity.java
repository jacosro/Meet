package dds.project.meet.presentation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dds.project.meet.R;
import dds.project.meet.logic.Card;
import dds.project.meet.logic.GPSTracker;
import dds.project.meet.logic.ParticipantOnEventAdapter;
import dds.project.meet.logic.User;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by RaulCoroban on 24/04/2017.
 */

public class EventActivity extends BaseActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String TAG = "EventActivity";

    //UI elements
    private ImageButton back;
    private RecyclerView recyclerParticipants;
    private ArrayList<User> dataUser;
    private RecyclerView.LayoutManager layoutManagerCards;
    private RecyclerView.Adapter adapterCards;
    private TextView nameEvent;
    private TextView timeEvent;
    private TextView dateEvent;
    private TextView locationMap;
    private MapFragment googleMap;
    private Button settingsButton;
    private Button directionsButton;
    private  TextView realDistance;

    //Class fields
    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private GoogleMap map;
    private int dayE, monthE, yearE;
    private String nameE, timeE, locationE;
    private LocationListener mLocationListener;
    private LatLng eventLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        nameEvent = (TextView) findViewById(R.id.name_event);
        timeEvent = (TextView) findViewById(R.id.time_event);
        dateEvent = (TextView) findViewById(R.id.date_event);
        locationMap = (TextView) findViewById(R.id.location_map);
        back = (ImageButton) findViewById(R.id.backButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        directionsButton = (Button) findViewById(R.id.directionsButton);
        realDistance = (TextView) findViewById(R.id.realDistance);

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        mPersistence.cardDAO.getCardByKey(key, new QueryCallback<Card>() {
            @Override
            public void result(Card data) {
                nameEvent.setText(nameE);
                timeEvent.setText(timeE);
                dateEvent.setText(dayE + "" + correctSuperScript(dayE) + " " + months[monthE]);
                locationMap.setText(locationE);



            }
        });

        googleMap = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);


        dataUser = new ArrayList<User>();
        recyclerParticipants = (RecyclerView) findViewById(R.id.participantsOnEvent);


        recyclerParticipants.setHasFixedSize(false);
        layoutManagerCards = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerParticipants.setLayoutManager(layoutManagerCards);

        adapterCards = new ParticipantOnEventAdapter(dataUser, this);
        recyclerParticipants.setAdapter(adapterCards);

        loadDefaultparticipants();

        /*
        nameE = intent.getStringExtra("EXTRA_NAME");
        timeE = intent.getStringExtra("EXTRA_TIME");
        dayE = intent.getIntExtra("EXTRA_DATE_DAY", 0);
        monthE = intent.getIntExtra("EXTRA_DATE_MONTH", 0);
        locationE = intent.getStringExtra("EXTRA_LOCATION");
        */



        if(googleServicesOK()) {
            Log.d("MAP_READY", "Enterning...");
            googleMap.getMapAsync(this);
            Log.d("MAP_READY", "InitMap");
        } else {
            Toast.makeText(this, "No map available", Toast.LENGTH_LONG).show();
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // <-- TODO Keep in memory?
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEventSettings = new Intent(EventActivity.this, SettingsEventActivity.class);

                toEventSettings.putExtra("EXTRA_NAME", nameE);
                toEventSettings.putExtra("EXTRA_LOCATION", locationE);
                toEventSettings.putExtra("EXTRA_TIME", timeE);
                toEventSettings.putExtra("EXTRA_DATE_DAY", dayE);
                toEventSettings.putExtra("EXTRA_DATE_MONTH", monthE);
                toEventSettings.putExtra("EXTRA_DATE_YEAR", yearE);

                startActivityForResult(toEventSettings, Activity.RESULT_OK);
            }
        });

        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toTrash = new Intent(EventActivity.this, TrashActivity.class);
                startActivity(toTrash);
            }
        });




    }

    private void loadDefaultparticipants() {
        dataUser.add(new User("Patricio Orlando", "Aqui", "654765876", "porlando@gmail.com"));
        dataUser.add(new User("Maria Bahilo", "Aqui", "654765876", "porlando@gmail.com"));
        dataUser.add(new User("Sandra Castillo", "Aqui", "654765876", "porlando@gmail.com"));
    }

    private String correctSuperScript(int day) {
        if(day > 20 && day % 10 == 1) return "st";
        if(day > 20 && day % 10 == 2) return "nd";
        return "th";
    }


    public boolean googleServicesOK() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isOk = api.isGooglePlayServicesAvailable(this);
        if (isOk == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isOk)) {
            Dialog d = api.getErrorDialog(this, isOk, 0);
            d.show();
        } else Toast.makeText(this, "Cannot connect, sorry", Toast.LENGTH_LONG).show();
        return false;
    }

    public void goTo(double lat, double lng, float zoom) {
        LatLng dir = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(dir, zoom);
        map.moveCamera(update);
    }

    public void geoLocate(String location) throws IOException{
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        Address add = list.get(0);

        String locality = add.getLocality();

        double latitude = add.getLatitude();
        double longitude = add.getLongitude();

        goTo(latitude, longitude, 17);

        MarkerOptions mo = new MarkerOptions()
                .title(locality)
                .position(new LatLng(latitude, longitude));
        map.addMarker(mo);
        Toast.makeText(this, "Place found!", Toast.LENGTH_SHORT).show();
        Log.d("MAP_READY", "Searched");
    }

    private LatLng getLatLng(String address) throws IOException {
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(address, 1);
        if (list.size() > 0) {
        Address add = list.get(0);

        String locality = add.getLocality();

        double latitude = add.getLatitude();
        double longitude = add.getLongitude();

        return new LatLng(latitude, longitude);

        } return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            eventLocation = getLatLng(locationE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (eventLocation != null) {
            googleMap.addMarker(new MarkerOptions().position(eventLocation)
                    .title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));

            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                DecimalFormat df = new DecimalFormat("#.0");
                LatLng myLocationLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());
                Toast.makeText(this, myLocationLatLng.toString(), Toast.LENGTH_LONG).show();
                realDistance.setText(df.format(calculateDistanceBetween(myLocationLatLng, eventLocation)) + " km");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String nameNew = extras.getString("EXTRA_NAME");
                    int day = extras.getInt("EXTRA_DAY");
                    int month = extras.getInt("EXTRA_MONTH");
                    int year = extras.getInt("EXTRA_DISTANCE");
                    String address = extras.getString("EXTRA_ADDRESS");
                    String whatTimeLabel = extras.getString("EXTRA_TIME");

                    nameE = nameNew; nameEvent.setText(nameNew);
                    dayE = day; monthE = month; yearE = year; dateEvent.setText(day + "" + correctSuperScript(day) + " " + months[month]);
                    locationE = address; locationMap.setText(address);
                    try {
                        geoLocate(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timeE = whatTimeLabel; timeEvent.setText(whatTimeLabel);


                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
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

}
