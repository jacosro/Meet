package dds.project.meet.presentation;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    //UI elements
    private ImageButton back;
    private RecyclerView recyclerParticipants;
    private ArrayList<Participant> dataParticipant;
    private RecyclerView.LayoutManager layoutManagerCards;
    private RecyclerView.Adapter adapterCards;
    private TextView nameEvent;
    private TextView timeEvent;
    private TextView dateEvent;
    private TextView locationMap;
    private MapFragment googleMap;

    //Class fields
    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        nameEvent = (TextView) findViewById(R.id.name_event);
        timeEvent = (TextView) findViewById(R.id.time_event);
        dateEvent = (TextView) findViewById(R.id.date_event);
        locationMap = (TextView) findViewById(R.id.location_map);
        back = (ImageButton) findViewById(R.id.backButton);

        googleMap = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        dataParticipant = new ArrayList<Participant>();
        recyclerParticipants = (RecyclerView) findViewById(R.id.participantsOnEvent);


        recyclerParticipants.setHasFixedSize(false);
        layoutManagerCards = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerParticipants.setLayoutManager(layoutManagerCards);

        adapterCards = new ParticipantOnEventAdapter(dataParticipant, this);
        recyclerParticipants.setAdapter(adapterCards);

        loadDefaultparticipants();

        Intent intent = getIntent();
        nameEvent.setText(intent.getStringExtra("EXTRA_NAME"));
        timeEvent.setText(intent.getStringExtra("EXTRA_TIME"));
        dateEvent.setText(intent.getIntExtra("EXTRA_DATE_DAY", 0) + "" + correctSuperScript(intent.getIntExtra("EXTRA_DATE_DAY", 0)) + " " + months[intent.getIntExtra("EXTRA_DATE_MONTH", 0)]);
        locationMap.setText(intent.getStringExtra("EXTRA_LOCATION"));


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

    }

    private void loadDefaultparticipants() {
        dataParticipant.add(new Participant("Patricio Orlando", "Aqui", "654765876", "porlando@gmail.com"));
        dataParticipant.add(new Participant("Maria Bahilo", "Aqui", "654765876", "porlando@gmail.com"));
        dataParticipant.add(new Participant("Sandra Castillo", "Aqui", "654765876", "porlando@gmail.com"));
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
        Toast.makeText(this, "Perfect!", Toast.LENGTH_SHORT).show();
        Log.d("MAP_READY", "Searched");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
