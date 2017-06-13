package dds.project.meet.logic.util;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jacosro on 13/06/17.
 */

public class TimeDistance {

    public static final int AVERAGE_PERSON_SPEED = 5;
    public static final int AVERAGE_CAR_SPEED = 60;

    public static double calculateDistanceBetween(LatLng latLng1, LatLng latLng2) {
        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        Location loc2 = new Location(LocationManager.GPS_PROVIDER);

        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);

        return loc1.distanceTo(loc2)/1000;
    }

    public static int getWalkingTime(double distance) {
        return (int) (distance / AVERAGE_PERSON_SPEED * 60);
    }

    public static int getDrivingTime(double distance) {
        return (int) (distance / AVERAGE_CAR_SPEED * 60);
    }
}
