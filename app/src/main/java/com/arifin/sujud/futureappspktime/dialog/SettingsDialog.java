package com.arifin.sujud.futureappspktime.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.arifin.sujud.R;
import com.arifin.sujud.futureappspktime.Schedule;
import com.arifin.sujud.futureappspktime.util.LocaleManager;


import com.arifin.sujud.universal.ThemeManager;

import java.util.GregorianCalendar;

//import adhanalarm.util.ThemeManager;

public class SettingsDialog extends Dialog {

    private LocaleManager localeManager;
    private ThemeManager themeManager;
    private static MediaPlayer mediaPlayer;


    public SettingsDialog(Context context, LocaleManager localeManager, ThemeManager themeManager) {
        super(context);
        this.localeManager = localeManager;
        this.themeManager = themeManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
        setTitle(R.string.settings);


        double gmtOffset = Schedule.getGMTOffset();
        String plusMinusGMT = gmtOffset < 0 ? "" + gmtOffset : "+" + gmtOffset;
        String daylightTime = Schedule.isDaylightSavings() ? " " + getContext().getString(R.string.daylight_savings) : "";

        ((TextView)findViewById(R.id.display_time_zone)).setText(getContext()
                .getString(R.string.system_time_zone) + ": " + getContext()
                .getString(R.string.gmt) + plusMinusGMT + " ("
                + new GregorianCalendar().getTimeZone().getDisplayName()
                + daylightTime + ")");

        ((Button)findViewById(R.id.set_calculation)).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new CalculationSettingsDialog(v.getContext()).show();

            }
        });

        ((Button)findViewById(R.id.set_notification)).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new NotificationSettingsDialog(v.getContext()).show();
            }
        });
//        ((Button)findViewById(R.id.set_interface)).setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                new InterfaceSettingsDialog(v.getContext(), themeManager, localeManager).show();
//            }
//        });
//        ((Button)findViewById(R.id.set_advanced)).setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                new AdvancedSettingsDialog(v.getContext()).show();
//            }
//        });
//        ((CheckBox)findViewById(R.id.bismillah_on_boot_up)).setChecked(VARIABLE.settings.getBoolean("bismillahOnBootUp", false));
//        ((CheckBox)findViewById(R.id.bismillah_on_boot_up)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    mediaPlayer = MediaPlayer.create(getContext(), R.raw.bismillah);
//                    mediaPlayer.setScreenOnWhilePlaying(true);
//                    mediaPlayer.start();
//                } else {
//                    if(mediaPlayer != null) mediaPlayer.stop();
//                }
//                SharedPreferences.Editor editor = VARIABLE.settings.edit();
//                editor.putBoolean("bismillahOnBootUp", isChecked);
//                editor.commit();
//            }
//        });
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if(hasFocus && (themeManager.isDirty() || localeManager.isDirty())) {
//            dismiss();
//        } else if(hasFocus) {
//            Schedule.setSettingsDirty(); // Technically we should do it only when they have changed i.e. if Calculation or Advanced settings changed but this is easier
//        }
//    }
}