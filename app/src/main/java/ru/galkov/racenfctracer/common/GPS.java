package ru.galkov.racenfctracer.common;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;


public class GPS implements LocationListener {


    private LocationManager lm;
    private int minDistance  = 1;
    private int minTime = 1;
    private Double Longitude, Latitude, Altitude;
    private TextView gps_monitor;

    public GPS(Context this_, TextView gps_nowhere2) {

        lm = (LocationManager) this_.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        gps_monitor = gps_nowhere2;
    }


    public void setGPSString(String str1) {
        gps_monitor.setText(str1);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)     {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
            Altitude = location.getAltitude();
            setGPSString("Координаты: " + Latitude + ", " + Longitude + ", " + Altitude);
        }
    }

    private Double getLongitude() {
        return Longitude;
    }
    private Double gettLatitude() {
        return Latitude;
    }
    private Double getAltitude() {
        return Altitude;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s)  {     }

    @Override
    public void onProviderDisabled(String s) {     }

}
