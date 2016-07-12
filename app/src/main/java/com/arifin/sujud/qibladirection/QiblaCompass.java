package com.arifin.sujud.qibladirection;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arifin.sujud.R;
import com.arifin.sujud.universal.GPSTracker;
import com.arifin.sujud.universal.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;


/**
 * Created by Shahzad on 11-Aug-15.
 */
public class QiblaCompass extends AppCompatActivity implements SensorListener {
    private InterstitialAd mInterstitialAd;

    private RelativeLayout direcCantainer;
    Context context;
    SensorManager sensorManager;
    static final int sensor = SensorManager.SENSOR_ORIENTATION;
    private Rose rose;
    View view;
    GPSTracker gps;
    double latitude,longitude;
    private String add;
    TextView CountryName, distanceQibla, degreeQibla;
    int Distance;
    double Qlati=21.42243;
    double Qlongi=39.82624;
    public  static double degree;
    private String location_string;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qibla_compass);
        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);
        context=this;
        IntializeView();
        getlocation();
        AsyncCallWS2 task=new AsyncCallWS2();
        task.execute();
//        getAddress(latitude, longitude);
        Distance=getDistanceBetween();
        degree=bearing(latitude, longitude, Qlati, Qlongi);
        direcCantainer = (RelativeLayout)findViewById(R.id.cantainer_layout);
        rose = new Rose(context);
        direcCantainer.addView(rose);
        rose.invalidate();
        sensorManager = (SensorManager)getSystemService(context.SENSOR_SERVICE);


        degreeQibla.setText(String.format("%.2f", degree)+ (char) 0x00B0);
//        Toast.makeText(context, ""+Distance/1000, Toast.LENGTH_SHORT).show();
        int distance = Distance/1000;
        String distanceS = String.valueOf(distance);

//        String nameC = context.getResources().getConfiguration().locale.getCountry();
//        distanceQibla.setText(distanceS);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Utils.Interstitial);
        AdRequest adRequest1 = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest1);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

    }

    public JSONObject getLocationInfo() {
        InputStream is = null;
        JSONObject jObject = null;
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
    private class AsyncCallWS2 extends AsyncTask<String, Void, Void> {
        // private String s;

        final ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected Void doInBackground(String... params) {
            JSONObject ret = getLocationInfo();
            JSONObject location;

            try {
                location = ret.getJSONArray("results").getJSONObject(0);
                location_string = location.getString("formatted_address");
                Log.d("test", "formattted address:" + location_string);

            } catch (JSONException e1) {
                e1.printStackTrace();

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            CountryName.setText(location_string);
            dialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private int getDistanceBetween() {

        double latA = Qlati;
        double lngA = Qlongi;
        double latB = latitude;
        double lngB = longitude;

        Location locationA = new Location("point A");
        locationA.setLatitude(latA);

        locationA.setLongitude(lngA);
        Location locationB = new Location("point B");
        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);
        float distance = locationA.distanceTo(locationB);
        int dis = (int) distance;

        return dis;
    }
    private void IntializeView() {

        CountryName=(TextView)findViewById(R.id.idTvCountryName);
        distanceQibla = (TextView)findViewById(R.id.idDistance);
        degreeQibla = (TextView)findViewById(R.id.idDegree);
    }
    protected double bearing(double startLat, double startLng, double endLat, double endLng){
        double longitude1 = startLng;
        double longitude2 = endLng;
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff= Math.toRadians(longitude2 - longitude1);
        double y= Math.sin(longDiff)* Math.cos(latitude2);
        double x= Math.cos(latitude1)* Math.sin(latitude2)- Math.sin(latitude1)* Math.cos(latitude2)* Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;


    }
    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get (0);
//            add = obj.getAddressLine (0);
            add =obj.getLocality();
            add = add + ", " + obj.getCountryName();
//            add = obj.getCountryName();

            Log.v("IGA", "Address" + add);

            CountryName.setText(add);
            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "You have no Internet connection", Toast.LENGTH_SHORT).show();
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "You have no Internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    private void getlocation() {
        // TODO Auto-generated method stub
        gps = new GPSTracker(context);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
    @Override
    public void onResume() {
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(Utils.Interstitial)
//                .build();
//        mInterstitialAd.loadAd(adRequest);
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }

        super.onResume();
        sensorManager.registerListener(this, sensor);
//        ((TextView)view.findViewById(R.id.idTvCountryName)).setText(new GregorianCalendar().getTimeZone().getDisplayName());
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor != QiblaCompass.sensor)
            return;
        int orientation = (int) values[0];
        rose.setDirections(orientation, orientation);
    }
    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {
    }
}
