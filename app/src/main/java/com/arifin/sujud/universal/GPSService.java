package com.arifin.sujud.universal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class GPSService extends Service implements LocationListener {

    //private static String url_insert_location = "http://172.20.10.4/testing/insert.php";
    SharedPreferences preferences;
    SharedPreferences.Editor editor3;
    String URL="http://avotradriver.azurewebsites.net/api/ReportManager/SendLocation";
    //String DriID = preferences.getString("driverid", null);

    String Date;
    Calendar c;
    private int year;
    private int month;
    private int day;
    int hour;
    int min;





    public static String LOG = "Log";

   // JSONParser jsonParser = new JSONParser();

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 ; // 1 second

    // Declaring a Location Manager
    protected LocationManager locationManager;


    public GPSService(Context context){
        this.mContext = context;
    }

    public GPSService(){
        super();
        mContext = GPSService.this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.i(LOG, "Service started");
        Log.i("asd", "This is sparta");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor3 = preferences.edit();

        String DriID = preferences.getString("driverid", null);

        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        Date = month+1 + "/" + day + "/" + year +" "+hour+" : "+min;


        getLocation();
        String longi= String.valueOf(longitude);
        String lati= String.valueOf(latitude);

        sendJson(DriID, longi, lati, Date,URL);

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG, "Service destroyed");
    }

    protected void sendJson(final String id, final String longitude, final String latitude, final String Time,final String url) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); // For Preparing Message Pool for the child
                // Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(),
                        10000); // Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();


                try {
                    HttpPost post = new HttpPost(url);
                    json.put("id", id);
                    json.put("speed", "0");
                    json.put("heading", "0");
                    json.put("altitude", "0");
                    json.put("latitude", latitude);
                    json.put("longitude", longitude);
                    json.put("timestamp", Time);
                    // json.put("password", pwd);
                    StringEntity se = new StringEntity(json.toString());

                    Log.d("InputData", json.toString());

                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                            "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

					/* Checking response */
                    if (response != null) {
                        InputStream in = response.getEntity().getContent();
                        String result = convertInputStreamToString(in);
                        Log.d("InputStream", result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Error", "Cannot Estabilish Connection");
                }

                Looper.loop(); // Loop in the message queue
            }
        };

        t.start();
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    public Location getLocation () {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //updates will be send according to these arguments
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }




    @Override
    public void onLocationChanged(Location location) {
//this will be called every second

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor3 = preferences.edit();

        String DriID = preferences.getString("driverid", null);

        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        Date = month+1 + "/" + day + "/" + year +" "+hour+" : "+min;


        getLocation();
        String longi= String.valueOf(longitude);
        String lati= String.valueOf(latitude);
        sendJson(DriID, longi, lati, Date, URL);


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

}