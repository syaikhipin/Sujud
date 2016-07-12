package com.arifin.sujud;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.arifin.sujud.futureappspktime.CONSTANT;
import com.arifin.sujud.futureappspktime.VARIABLE;
import com.arifin.sujud.universal.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Shahzad Ahmad on 21-Sep-15.
 */
public class Settings extends AppCompatActivity {
    Context context;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_advanced1);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.customactionbar);
        context=this;

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(Utils.Interstitial);

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);

        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(Utils.Banner)
                .build();
        mAdView.loadAd(adRequest2);
//        setTitle(R.string.advanced);

        Spinner time_format = (Spinner)findViewById(R.id.time_format);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.time_format, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_format.setAdapter(adapter);
        time_format.setSelection(VARIABLE.settings.getInt("timeFormatIndex", CONSTANT.DEFAULT_TIME_FORMAT));

        Spinner calculation_methods = (Spinner)findViewById(R.id.calculation_methods);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(context, R.array.calculation_methods, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calculation_methods.setAdapter(adapter1);
        calculation_methods.setSelection(VARIABLE.settings.getInt("calculationMethodsIndex", CONSTANT.DEFAULT_CALCULATION_METHOD));


        ((EditText)findViewById(R.id.pressure)).setText(Float.toString(VARIABLE.settings.getFloat("pressure", 1010)));
        ((EditText)findViewById(R.id.temperature)).setText(Float.toString(VARIABLE.settings.getFloat("temperature", 10)));
        ((EditText)findViewById(R.id.altitude)).setText(Float.toString(VARIABLE.settings.getFloat("altitude", 0)));

        ((EditText)findViewById(R.id.latitude)).setText(""+MainActivity.lati);
        ((EditText)findViewById(R.id.longitude)).setText(""+MainActivity.longi);

        Spinner rounding_types = (Spinner)findViewById(R.id.rounding_types);
        adapter = ArrayAdapter.createFromResource(context, R.array.rounding_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rounding_types.setAdapter(adapter);
        rounding_types.setSelection(VARIABLE.settings.getInt("roundingTypesIndex", CONSTANT.DEFAULT_ROUNDING_TYPE));

        ((EditText)findViewById(R.id.offset_minutes)).setText(Integer.toString(VARIABLE.settings.getInt("offsetMinutes", 0)));

        ((Button)findViewById(R.id.save_settings)).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = VARIABLE.settings.edit();
                editor.putInt("timeFormatIndex", ((Spinner)findViewById(R.id.time_format)).getSelectedItemPosition());
                try {
                    editor.putFloat("altitude", Float.parseFloat(((EditText)findViewById(R.id.altitude)).getText().toString()));
                } catch(Exception ex) {
                    editor.putFloat("altitude", 0);
                }
                try {
                    editor.putFloat("pressure", Float.parseFloat(((EditText)findViewById(R.id.pressure)).getText().toString()));
                } catch(Exception ex) {
                    editor.putFloat("pressure", 1010);
                }
                try {
                    editor.putFloat("temperature", Float.parseFloat(((EditText) findViewById(R.id.temperature)).getText().toString()));
                } catch(Exception ex) {
                    editor.putFloat("temperature", 10);
                }
                editor.putInt("roundingTypesIndex", ((Spinner) findViewById(R.id.rounding_types)).getSelectedItemPosition());
                try {
                    editor.putInt("offsetMinutes", Integer.parseInt(((EditText) findViewById(R.id.offset_minutes)).getText().toString()));
                } catch(Exception ex) {
                    editor.putInt("offsetMinutes", 0);
                }

                try {
                    editor.putFloat("latitude", Float.parseFloat(((EditText)findViewById(R.id.latitude)).getText().toString()));
                } catch(Exception ex) {
                    // Invalid latitude
                }
                try {
                    editor.putFloat("longitude", Float.parseFloat(((EditText) findViewById(R.id.longitude)).getText().toString()));
                } catch(Exception ex) {
                    // Invalid longitude
                }
                editor.putInt("calculationMethodsIndex", ((Spinner)findViewById(R.id.calculation_methods)).getSelectedItemPosition());
                editor.commit();
                Toast.makeText(context,"Setting Saved",Toast.LENGTH_SHORT).show();
                finish();
//                SettingsActivity.context.finish();
//                dismiss();
            }
        });
        ((Button)findViewById(R.id.reset_settings)).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                ((Spinner)findViewById(R.id.calculation_methods)).setSelection(CONSTANT.DEFAULT_CALCULATION_METHOD);
                ((Spinner)findViewById(R.id.time_format)).setSelection(CONSTANT.DEFAULT_TIME_FORMAT);
                ((EditText)findViewById(R.id.pressure)).setText("1010.0");
                ((EditText)findViewById(R.id.temperature)).setText("10.0");
                ((EditText)findViewById(R.id.altitude)).setText("0.0");
                ((Spinner)findViewById(R.id.rounding_types)).setSelection(CONSTANT.DEFAULT_ROUNDING_TYPE);
                ((EditText)findViewById(R.id.offset_minutes)).setText("0");
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}
