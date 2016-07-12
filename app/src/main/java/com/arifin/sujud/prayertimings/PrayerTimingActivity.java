package com.arifin.sujud.prayertimings;

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
import android.view.View;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.arifin.sujud.R;
import com.arifin.sujud.futureappspktime.CONSTANT;
import com.arifin.sujud.futureappspktime.Schedule;
import com.arifin.sujud.futureappspktime.VARIABLE;
import com.arifin.sujud.futureappspktime.receiver.StartNotificationReceiver;
import com.arifin.sujud.futureappspktime.service.FillDailyTimetableService;

//import com.futureappspk.ramadantimetable.Utils;
import com.arifin.sujud.universal.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by BuAli_bluehorn on 11-May-15.
 */
public class PrayerTimingActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;

    private ArrayList<HashMap<String, String>> timetable = new ArrayList<HashMap<String, String>>(7);
    private SimpleAdapter timetableView;

    private static SensorListener orientationListener;
    private static boolean isTrackingOrientation = false;
    View view;
    Context context;
    TextView currentDate,currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prayer_time_fragment);
        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);
        context = this;
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(Utils.Interstitial);

        AdRequest adRequest1 = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest1);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
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
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        DateFormat tf = new SimpleDateFormat("h:mm a");
        String time = tf.format(Calendar.getInstance().getTime());


        currentTime =(TextView)findViewById(R.id.idTvTimeHomePrayer);
        currentTime.setText(time);
        currentDate = (TextView)findViewById(R.id.idTvDateHomePrayer);
        currentDate.setText(date);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ((ListView) findViewById(R.id.listView)).getLayoutParams().height = displayMetrics.heightPixels * 3 / 5;
        ((ListView) findViewById(R.id.listView)).getLayoutParams().width = displayMetrics.widthPixels * 4 / 5;
        timetable.clear();
//        https://play.google.com/apps/testing/com.futureapppspk.quran_e_pak
        for (int i = CONSTANT.FAJR; i <= CONSTANT.NEXT_FAJR; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("time_name", getString(CONSTANT.TIME_NAMES[i]));
            timetable.add(i, map);
        }

        timetableView = new SimpleAdapter(context, timetable, R.layout.prayer_time_row, new String[]{"mark", "time_name", "time", "time_am_pm"}, new int[]{R.id.mark, R.id.time_name, R.id.time, R.id.time_am_pm}) {
            public boolean areAllItemsEnabled() {
                return false;
            } // Disable list's item selection

            public boolean isEnabled(int position) {
                return false;
            }
        };

        ((ListView) findViewById(R.id.listView)).setAdapter(timetableView);

        ((ListView) findViewById(R.id.listView)).setOnHierarchyChangeListener(new OnHierarchyChangeListener() { // Set zebra stripes
            private int numChildren = 0;

            public void onChildViewAdded(View parent, View child) {
//                child.setBackgroundResource(++numChildren % 2 == 0 ? themeManager.getAlternateRowColor() : android.R.color.transparent);
                if (numChildren > CONSTANT.NEXT_FAJR)
                    numChildren = 0; // Last row has been reached, reset for next time
            }

            public void onChildViewRemoved(View parent, View child) {
            }
        });

        VARIABLE.mainActivityIsRunning = true;
        updateTodaysTimetableAndNotification();
        startTrackingOrientation();
        super.onResume();
    }

    public void refreshFragment2() {
    }

    public void updateTodaysTimetableAndNotification() {
        StartNotificationReceiver.setNext(context);
        FillDailyTimetableService.set(this, Schedule.today(), timetable, timetableView);
    }

    private void startTrackingOrientation() {
        if (!isTrackingOrientation)
            isTrackingOrientation = ((SensorManager) context.getSystemService(context.SENSOR_SERVICE)).registerListener(orientationListener, SensorManager.SENSOR_ORIENTATION);
    }

    private void stopTrackingOrientation() {
        if (isTrackingOrientation)
            ((SensorManager) context.getSystemService(context.SENSOR_SERVICE)).unregisterListener(orientationListener);
        isTrackingOrientation = false;
    }

    private void restart() {
        long restartTime = Calendar.getInstance().getTimeInMillis() + CONSTANT.RESTART_DELAY;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, restartTime, PendingIntent.getActivity(context, 0, getIntent(), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT));
        finish();
    }

    private void configureCalculationDefaults() {
        if (!VARIABLE.settings.contains("latitude") || !VARIABLE.settings.contains("longitude")) {
            Location currentLocation = VARIABLE.getCurrentLocation(context);
            try {
                SharedPreferences.Editor editor = VARIABLE.settings.edit();
                editor.putFloat("latitude", (float) currentLocation.getLatitude());
                editor.putFloat("longitude", (float) currentLocation.getLongitude());
                editor.commit();
                VARIABLE.updateWidgets(context);
            } catch (Exception ex) {
                ((TextView) findViewById(R.id.notes)).setText(getString(R.string.location_not_set));
            }
        }
        if (!VARIABLE.settings.contains("calculationMethodsIndex")) {
            try {
                String country = Locale.getDefault().getISO3Country().toUpperCase();

                SharedPreferences.Editor editor = VARIABLE.settings.edit();
                for (int i = 0; i < CONSTANT.CALCULATION_METHOD_COUNTRY_CODES.length; i++) {
                    if (Arrays.asList(CONSTANT.CALCULATION_METHOD_COUNTRY_CODES[i]).contains(country)) {
                        editor.putInt("calculationMethodsIndex", i);
                        editor.commit();
                        VARIABLE.updateWidgets(context);
                        break;
                    }
                }
            } catch (Exception ex) {
                // Wasn't set, oh well we'll uses DEFAULT_CALCULATION_METHOD later
            }
        }
    }

}
