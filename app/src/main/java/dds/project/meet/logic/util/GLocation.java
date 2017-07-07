package dds.project.meet.logic.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dds.project.meet.presentation.EventActivity;

/**
 * Created by jacosro on 7/07/17.
 */

public class GLocation {

    public class CurrentLocation {

        public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
        public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

        public void getCurrentLocation(ResultCallback<PlaceLikelihoodBuffer> callback) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }

            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(callback);
        }

    }

    public CurrentLocation currentLocation;

    private Activity context;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mLocation;

    private GLocation(Activity context, String location, Object dummy) throws IOException {
        currentLocation = new CurrentLocation();
        this.context = context;
        mLocation = getLatLng(location);
    }

    public GLocation(Activity context, String location) throws IOException {
        this(context, location, false);
        initGApi();
    }

    public GLocation(FragmentActivity context, String location, GoogleApiClient.OnConnectionFailedListener callback) throws IOException {
        this(context, location, false);
        initGApi(callback);
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public LatLng getLatLng(String address) throws IOException {
        Geocoder gc = new Geocoder(context);
        List<Address> list = gc.getFromLocationName(address, 1);
        if (!list.isEmpty()) {
            Address add = list.get(0);
            double latitude = add.getLatitude();
            double longitude = add.getLongitude();

            return new LatLng(latitude, longitude);

        }
        return null;
    }

    public boolean googleServicesOK() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isOk = api.isGooglePlayServicesAvailable(context);
        if (isOk == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isOk)) {
            Dialog d = api.getErrorDialog(context, isOk, 0);
            d.show();
        } else Toast.makeText(context, "Cannot connect to Google Services. Try it later", Toast.LENGTH_LONG).show();
        return false;
    }

    private void initGApi() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API);

        mGoogleApiClient = builder.build();
        mGoogleApiClient.connect();
    }

    private void initGApi(GoogleApiClient.OnConnectionFailedListener callback) {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context, callback)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API);

        mGoogleApiClient = builder.build();
        mGoogleApiClient.connect();
    }

}
