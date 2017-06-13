package dds.project.meet.presentation;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
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
import dds.project.meet.logic.ParticipantOnEventAdapter;
import dds.project.meet.logic.User;
import dds.project.meet.persistence.QueryCallback;

/**
 * Created by RaulCoroban on 24/04/2017.
 */

public class EventActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String TAG = "EventActivity";

    //UI elements
    private ImageButton back;
    private RecyclerView recyclerParticipants;
    private ArrayList<User> dataUser;
    private RecyclerView.LayoutManager layoutManagerParticipants;
    private RecyclerView.Adapter adapterParticipants;
    private TextView nameEvent;
    private TextView timeEvent;
    private TextView dateEvent;
    private TextView locationMap;
    private MapFragment googleMap;
    private Button settingsButton;
    private Button directionsButton;
    private Button refresh;
    private TextView realDistance;
    private TextView descriptionTextView;
    private MapFragment mapFragment;
    private TextView distanceWalk;
    private TextView distanceCar;

    //Class fields
    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private GoogleMap map;
    private Card mCard;
    private LocationListener mLocationListener;
    private LatLng eventLocation;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        mCard = new Card();

        nameEvent = (TextView) findViewById(R.id.name_event);
        timeEvent = (TextView) findViewById(R.id.time_event);
        dateEvent = (TextView) findViewById(R.id.date_event);
        locationMap = (TextView) findViewById(R.id.location_map);
        back = (ImageButton) findViewById(R.id.backButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        directionsButton = (Button) findViewById(R.id.directionsButton);
        refresh = (Button) findViewById(R.id.refreshButton);
        realDistance = (TextView) findViewById(R.id.realDistance);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextViewEvent);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        distanceWalk = (TextView) findViewById(R.id.distanceWalk);
        distanceCar = (TextView) findViewById(R.id.distanceCar);

        Intent intent = getIntent();
        mCard.setDbKey(intent.getStringExtra("key"));

        dataUser = new ArrayList<User>();
        recyclerParticipants = (RecyclerView) findViewById(R.id.participantsOnEvent);
        recyclerParticipants.setHasFixedSize(false);
        layoutManagerParticipants = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerParticipants.setLayoutManager(layoutManagerParticipants);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEventSettings = new Intent(EventActivity.this, SettingsEventActivity.class);
                toEventSettings.putExtra("key", mCard.getDbKey());
                startActivity(toEventSettings);
            }
        });

        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "https://www.google.com/maps/search/?api=1&query=" + eventLocation.latitude + "," + eventLocation.longitude;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(query));
                startActivity(intent);
            }
        });


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshDistances();
                Toast.makeText(EventActivity.this, "Distances refreshed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mPersistence.cardDAO.findCardByKey(mCard.getDbKey(), new QueryCallback<Card>() {
            @Override
            public void result(Card data) {
                mCard = data;

                Log.d(TAG, data.toString());

                nameEvent.setText(mCard.getName());
                timeEvent.setText(mCard.getTime());
                dateEvent.setText(mCard.getDateDay() + "" + correctSuperScript(mCard.getDateDay()) + " " + months[mCard.getDateMonth()]);
                try {
                    eventLocation = getLatLng(mCard.getLocation());
                } catch (IOException e) {
                    Log.d(TAG, "Location not valid");
                }
                locationMap.setText(mCard.getLocation());
                descriptionTextView.setText(mCard.getDescription());

                adapterParticipants = new ParticipantOnEventAdapter(mCard.getParticipants(), EventActivity.this, mCard);
                recyclerParticipants.setAdapter(adapterParticipants);

                if (googleMap == null) {
                    googleMap = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

                    if (googleServicesOK()) {
                        Log.d("MAP_READY", "Enterning...");
                        googleMap.getMapAsync(EventActivity.this);
                        Log.d("MAP_READY", "InitMap");
                    } else {
                        Toast.makeText(EventActivity.this, "No map available", Toast.LENGTH_LONG).show();
                    }
                } else {
                    googleMap.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.addMarker(new MarkerOptions().position(eventLocation)
                                    .title("Marker on Event Place"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
                            refreshDistances();
                        }
                    });
                }
            }
        });
    }

    private String correctSuperScript(int day) {
        if (day > 20 && day % 10 == 1) return "st";
        if (day > 20 && day % 10 == 2) return "nd";
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

    public void geoLocate(String location) throws IOException {
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

        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            eventLocation = getLatLng(mCard.getLocation());
        } catch (IOException e) {
            Log.d(TAG, "No va el puto mapa " + e);
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (eventLocation != null) {
            googleMap.addMarker(new MarkerOptions().position(eventLocation)
                    .title("Marker on Event Place"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();


        refreshDistances();


    }

    private void refreshDistances() {

        ActivityCompat.requestPermissions(EventActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission denied for Access Fine Location");
            return;
        }

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                LatLng myLoca = likelyPlaces.get(0).getPlace().getLatLng();
                double distance = calculateDistanceBetween(myLoca, eventLocation);
                int distWalk = (int) (distance/5 * 60);
                int distCar = (int) (distance/60 * 60);

                DecimalFormat df = new DecimalFormat("#.##");
                realDistance.setText(df.format(distance) + " km");

                if(distWalk > 1000) {
                    distanceWalk.setText(">16 hrs");
                } else {
                    distanceWalk.setText(distWalk + " min");
                }

                if(distCar > 5000) {
                    distanceCar.setText(">3 days");
                } else {
                    distanceCar.setText(distCar + " min");
                }

                likelyPlaces.release();
            }
        });
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed");
    }
}
