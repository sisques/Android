package es.unizar.eina.ebrozon.lib;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;
import static android.support.v4.content.ContextCompat.startActivity;

public class gps {

    private Context c;
    private String uname;
    private Handler mHandler = new Handler();
    private int INTERVAL = 1000*60;
    private Boolean asignado = false;
    private double lat = 0;
    private double lon = 0;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private SharedPreferences sharedpreferences;



    public gps(Context ctx, String u){
        c = ctx;
        uname = u;
        sharedpreferences = c.getSharedPreferences(Common.MyPreferences, c.MODE_PRIVATE);
    }

    @SuppressLint("MissingPermission")
    public void init(){
        mLocationManager = (LocationManager) c.getSystemService( Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                asignado = true;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mLocationManager.requestLocationUpdates("gps",INTERVAL/30, 50, mLocationListener);
    }

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            updateGpsInfo();
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };


    public void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    public void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }


    private void updateGpsInfo(){
        if(asignado){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Common.lat, Double.toString(lat));
            editor.putString(Common.lon, Double.toString(lon));
            editor.commit();
        }
    }
}
