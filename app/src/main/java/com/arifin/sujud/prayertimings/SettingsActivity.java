package com.arifin.sujud.prayertimings;

import android.app.Activity;
import android.os.Bundle;

import com.arifin.sujud.futureappspktime.dialog.SettingsDialog;
import com.arifin.sujud.futureappspktime.util.LocaleManager;
import com.arifin.sujud.R;
import com.arifin.sujud.universal.ThemeManager;

/**
 * Created by BuAli_bluehorn on 23-Jun-15.
 */
public class SettingsActivity extends Activity{
    public static Activity context;
    private static ThemeManager themeManager;
    private static LocaleManager localeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localeManager = new LocaleManager();
        setContentView(R.layout.setting_activity);
        context = this;
        new SettingsDialog(this, localeManager, themeManager).show();

    }
}
