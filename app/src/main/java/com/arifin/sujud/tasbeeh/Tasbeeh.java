package com.arifin.sujud.tasbeeh;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arifin.sujud.R;
import com.arifin.sujud.universal.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Shahzad Ahmad on 04-Jul-15.
 */
public class Tasbeeh extends AppCompatActivity {


    int counter;

    Button add;
    ImageView reset,save;
    TextView display;
    Context context;
    private InterstitialAd mInterstitialAd;
    PowerManager.WakeLock wakeLock;
    public void updateDisplay() {
        this.display.setText("Your total is: " + this.counter);
    }


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasbeeh);
        context = this;


        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(Utils.Interstitial);

        AdRequest adRequest1 = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest1);

        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        wakeLock.acquire();

        add = (Button) findViewById(R.id.bAdd);
        reset = (ImageView) findViewById(R.id.ivresettasbeeh);
        save = (ImageView) findViewById(R.id.ivsavetasbeeh);
        display = (TextView) findViewById(R.id.tvDisplay);

        counter=Utils.getPreferencesInt("Tasbeeh", context);
        display.setText("" + counter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.savePreferencesInt("Tasbeeh",counter,context);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                counter++;
                display.setText("" + counter);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Utils.savePreferencesInt("Tasbeeh",0,context);
                counter=0;
                display.setText("" + counter);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    // Volume up key detected
                    // Do something
                    counter++;
                    display.setText("" + counter);
//                    Toast.makeText(context, "Up", Toast.LENGTH_SHORT).show();
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    // Volume down key detected
                    // Do something
                    if (counter != 0) {
                        counter--;
                        display.setText("" + counter);
                    }
//                    Toast.makeText(context, "Down", Toast.LENGTH_SHORT).show();
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }
}