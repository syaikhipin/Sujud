package com.arifin.sujud.futureappspktime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arifin.sujud.futureappspktime.Notifier;
import com.arifin.sujud.prayertimings.PrayerTimingActivity;


//import adhanalarm.AdhanAlarm;

public class ClickNotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Notifier.stop();
		
		Intent i = new Intent(context, PrayerTimingActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}