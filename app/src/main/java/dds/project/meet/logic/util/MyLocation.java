package dds.project.meet.logic.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Query;

import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by jacosro on 7/07/17.
 */

public class MyLocation {
    private static final String TAG = "MyLocation";

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static boolean permissionRequested = false;
    private static GoogleApiClient mGoogleApiClient;

    public static void get(Activity context, final QueryCallback<LatLng> callback) {
        Log.d(TAG, "Getting myLocation");
        if (!isPermissionRequested()) {
            Log.d(TAG, "Location has not been requested before");
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            permissionRequested = true;
        } else if (isPermissionGranted(context)) {
            Log.d(TAG, "Permission is granted");
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(getGApiClient(context), null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    LatLng result = placeLikelihoods.get(0).getPlace().getLatLng();
                    placeLikelihoods.release();
                    callback.result(result);
                }
            });
        } else {
            Log.d(TAG, "Permission denied");
        }
    }

    public static boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isPermissionRequested() {
        return permissionRequested;
    }

    public static boolean isPermissionDenied(Context context) {
        return isPermissionRequested() && !isPermissionGranted(context);
    }

    public static GoogleApiClient getGApiClient(Activity context) {
        if (mGoogleApiClient == null) {
            GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API);

            mGoogleApiClient = builder.build();
            mGoogleApiClient.connect();
        }
        return mGoogleApiClient;
    }

}
