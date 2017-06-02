package dds.project.meet.presentation;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dds.project.meet.R;
import dds.project.meet.logic.Card;
import dds.project.meet.logic.CardAdapter;
import dds.project.meet.logic.Participant;
import dds.project.meet.logic.ParticipantAdapter;
import dds.project.meet.logic.ParticipantOnEventAdapter;
import dds.project.meet.logic.RecyclerItemClickListener;

/**
 * Created by RaulCoroban on 24/04/2017.
 */

public class EventActivity extends BaseActivity implements OnMapReadyCallback {

    GoogleMap map;
    boolean defaultMap = true;
    String locationEvent;

    RecyclerView recyclerParticipants;
    ArrayList<Participant> dataParticipant;
    private RecyclerView.LayoutManager layoutManagerCards;
    public static RecyclerView.Adapter adapterCards;

    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        recyclerParticipants = (RecyclerView) findViewById(R.id.participantsOnEvent);

        dataParticipant = new ArrayList<Participant>();

        recyclerParticipants.setHasFixedSize(false);
        layoutManagerCards = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerParticipants.setLayoutManager(layoutManagerCards);

        adapterCards = new ParticipantOnEventAdapter(dataParticipant, this);
        recyclerParticipants.setAdapter(adapterCards);
        dataParticipant.add(new Participant("Patricio Orlando", "Aqui", "654765876", "porlando@gmail.com"));
        dataParticipant.add(new Participant("Maria Bahilo", "Aqui", "654765876", "porlando@gmail.com"));
        dataParticipant.add(new Participant("Sandra Castillo", "Aqui", "654765876", "porlando@gmail.com"));

        Intent intent = getIntent();

        TextView nameEvent = (TextView) findViewById(R.id.name_event);
        TextView timeEvent = (TextView) findViewById(R.id.time_event);
        TextView dateEvent = (TextView) findViewById(R.id.date_event);
        TextView locationMap = (TextView) findViewById(R.id.location_map);
        nameEvent.setText(intent.getStringExtra("EXTRA_NAME"));
        timeEvent.setText(intent.getStringExtra("EXTRA_TIME"));
        dateEvent.setText(intent.getIntExtra("EXTRA_DATE_DAY", 0) + " " + months[intent.getIntExtra("EXTRA_DATE_MONTH", 0)]);
        locationMap.setText(intent.getStringExtra("EXTRA_LOCATION"));

        if(locationEvent != null) defaultMap = false;

        if(googleServicesOK()) {
            initMap();
        } else {
            Toast.makeText(this, "No map available", Toast.LENGTH_LONG).show();
        }

    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        Log.d("MAP_READY", "InitMap");
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
        Toast.makeText(this, "Perfect!", Toast.LENGTH_SHORT).show();
        Log.d("MAP_READY", "Searched");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(defaultMap) {
            try {
                geoLocate("london");
                Log.d("MAP_READY", "Everything Ok");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                geoLocate(locationEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
