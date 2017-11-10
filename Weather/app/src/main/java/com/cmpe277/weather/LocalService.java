package com.cmpe277.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocalService extends Service {
    public static final String TAG = "Weather";
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Log.i(TAG, "Starting LocalService");
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)  {
                Log.d(TAG, "onLocationChanged(): callback received!");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d(TAG, "longitude is " + longitude);
                Log.d(TAG, "latitude is " + latitude);

                try {
                    Geocoder gcd = new Geocoder(LocalService.this, Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        Intent i = new Intent("locationUpdate");
                        i.putExtra("currentCity", addresses.get(0).getLocality());
                        Log.i(TAG, "Broadcasting city: " + addresses.get(0).getLocality());
                        sendBroadcast(i);
                    }
                } catch (IOException e) {}
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged(): callback received!");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled(): callback received!");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled(): callback received!");
            }
        };
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }


    @Override
    public void onDestroy() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
