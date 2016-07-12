package com.arifin.sujud.ramadantimings;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ListView;
import android.widget.TextView;


import com.arifin.sujud.R;
import com.arifin.sujud.futureappspktime.CONSTANTramadan;
import com.arifin.sujud.futureappspktime.ScheduleRamadan;
import com.arifin.sujud.futureappspktime.VARIABLE;
import com.arifin.sujud.universal.Constant;
import com.arifin.sujud.universal.Utils;




import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by BuAli_bluehorn on 11-May-15.
 */
public class RamadanTimingActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;

//    private ArrayList<HashMap<String, String>> timetable2 = new ArrayList<HashMap<String, String>>(30);
//    private SimpleAdapter timetableView2;

    private static SensorListener orientationListener;
    private static boolean isTrackingOrientation = false;

    private ArrayList<Constant> arraylist;
    MyAdaptor adaptor;
    ListView listView;
    Context context;
    TextView currentDate,currentTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ramadan_times);
        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);
        context=this;


        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(Utils.Interstitial);

        AdRequest adRequest1 = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest1);

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        DateFormat tf = new SimpleDateFormat("h:mm a");
        String time = tf.format(Calendar.getInstance().getTime());


        currentTime =(TextView)findViewById(R.id.idTvTimeHomeRamadan);
        currentTime.setText(time);
        currentDate = (TextView)findViewById(R.id.idTvDateHomeRamdan);
        currentDate.setText(date);

        listView=(ListView)findViewById(R.id.listView123);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
    @Override
    public void onResume() {
        int t = 8;
        int m = 6;
        int y = 2016;
        String date = "";
        Utils.list.clear();
        for(int j= 0; j<30; j++) {
            int i = CONSTANTramadan.FAJR1;
            int i2 = CONSTANTramadan.MAGRIB1;
            if(t>30){
                m = m+1;
                t=1 ;
            }
            GregorianCalendar[] schedule = ScheduleRamadan.today(t, m - 1, y).getTimesramadan();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:a");
            String fullTime = timeFormat.format(schedule[i].getTime());
            String fullTimem = timeFormat.format(schedule[i2].getTime());
            date=""+t+"-"+m+"-"+y;
            t= t+1;
            Constant ramdhan=new Constant(fullTime, fullTimem,date);
            Utils.list.add(ramdhan);
        }
        adaptor = new MyAdaptor(context, Utils.list);
        listView.setAdapter(adaptor);
        super.onResume();
    }
    private void startTrackingOrientation() {
        if(!isTrackingOrientation) isTrackingOrientation = ((SensorManager)context.getSystemService(context.SENSOR_SERVICE)).registerListener(orientationListener, SensorManager.SENSOR_ORIENTATION);
    }
    private void stopTrackingOrientation() {
        if(isTrackingOrientation) ((SensorManager)context.getSystemService(context.SENSOR_SERVICE)).unregisterListener(orientationListener);
        isTrackingOrientation = false;
    }
    private void restart() {
        long restartTime = Calendar.getInstance().getTimeInMillis() + CONSTANTramadan.RESTART_DELAY;
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, restartTime, PendingIntent.getActivity(context, 0, getIntent(), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT));
       finish();
    }
    private void configureCalculationDefaults() {
        if(!VARIABLE.settings.contains("latitude") || !VARIABLE.settings.contains("longitude")) {
            Location currentLocation = VARIABLE.getCurrentLocation(context);
            try {
                SharedPreferences.Editor editor = VARIABLE.settings.edit();
                editor.putFloat("latitude", (float)currentLocation.getLatitude());
                editor.putFloat("longitude", (float)currentLocation.getLongitude());
                editor.commit();
                VARIABLE.updateWidgets(context);
            } catch(Exception ex) {
                ((TextView)findViewById(R.id.notes)).setText(getString(R.string.location_not_set));
            }
        }
        if(!VARIABLE.settings.contains("calculationMethodsIndex")) {
            try {
                String country = Locale.getDefault().getISO3Country().toUpperCase();
                SharedPreferences.Editor editor = VARIABLE.settings.edit();
                for(int i = 0; i < CONSTANTramadan.CALCULATION_METHOD_COUNTRY_CODES.length; i++) {
                    if(Arrays.asList(CONSTANTramadan.CALCULATION_METHOD_COUNTRY_CODES[i]).contains(country)) {
                        editor.putInt("calculationMethodsIndex", i);
                        editor.commit();
                        VARIABLE.updateWidgets(context);
                        break;
                    }
                }
            } catch(Exception ex) {
            }
        }
    }
}
