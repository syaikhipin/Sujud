package com.arifin.sujud.futureappspktime.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.arifin.sujud.R;
import com.arifin.sujud.futureappspktime.CONSTANT;
import com.arifin.sujud.futureappspktime.ScheduleRamadan;
import com.arifin.sujud.futureappspktime.VARIABLE;

import com.arifin.sujud.futureappspktime.CONSTANTramadan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class FillDailyTimetableServiceRamadan extends Service {

	private static Activity parent2;

	private static ScheduleRamadan day2;
	private static ArrayList<HashMap<String, String>> timetable2;
	private static SimpleAdapter timetableView2;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		try {
			GregorianCalendar[] schedule = day2.getTimesramadan();
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
			if(VARIABLE.settings.getInt("timeFormatIndex", CONSTANT.DEFAULT_TIME_FORMAT) != CONSTANT.DEFAULT_TIME_FORMAT) {
				timeFormat = new SimpleDateFormat("HH:mm ");
			}

			for(short i = CONSTANTramadan.FAJR1; i <= CONSTANTramadan.MAGRIB1; i++) {
				String fullTime = timeFormat.format(schedule[i].getTime());
//				timetable2.get(i).put("mark", ""); // Clear all existing markers since we're going to set the next one
				timetable2.get(i).put("time", fullTime.substring(0, fullTime.lastIndexOf(" ")));
                timetable2.get(i).put("aftar", fullTime.substring(0, fullTime.lastIndexOf(" ")));
				if(VARIABLE.settings.getInt("timeFormatIndex", CONSTANTramadan.DEFAULT_TIME_FORMAT) == CONSTANT.DEFAULT_TIME_FORMAT) {
					timetable2.get(i).put("time_am_pm", fullTime.substring(fullTime.lastIndexOf(" ") + 1, fullTime.length()) + (day2.isExtreme(i) ? "*" : ""));
                    timetable2.get(i).put("aftar_time_am_pm", fullTime.substring(fullTime.lastIndexOf(" ") + 1, fullTime.length()) + (day2.isExtreme(i) ? "*" : ""));
				} else {
					timetable2.get(i).put("time_am_pm", day2.isExtreme(i) ? "*" : "");
                    timetable2.get(i).put("aftar_time_am_pm", day2.isExtreme(i) ? "*" : "");
				}
				if(day2.isExtreme(i)) ((TextView)parent2.findViewById(R.id.notes)).setText("* " + getString(R.string.extreme));
			}
////            timetable2.get(day2.nextTimeIndex()).put("mark1", getString(R.string.next_time_marker));
////			timetable2.get(day2.nextTimeIndex()).put("mark", getString(R.string.next_time_marker));
//
//			timetableView2.notifyDataSetChanged();
//
//			// Add Latitude, Longitude and Qibla DMS location
//			Location location = new Location(VARIABLE.settings.getFloat("latitude", 43.67f), VARIABLE.settings.getFloat("longitude", -79.417f), Schedule.getGMTOffset(), 0);
//			location.setSeaLevel(VARIABLE.settings.getFloat("altitude", 0) < 0 ? 0 : VARIABLE.settings.getFloat("altitude", 0));
//			location.setPressure(VARIABLE.settings.getFloat("pressure", 1010));
//			location.setTemperature(VARIABLE.settings.getFloat("temperature", 10));
//
//			DecimalFormat df = new DecimalFormat("#.###");
//			Dms latitude = new Dms(location.getDegreeLat());
//			Dms longitude = new Dms(location.getDegreeLong());
//			Dms qibla = Jitl.getNorthQibla(location);
//			VARIABLE.qiblaDirection = (float)qibla.getDecimalValue(Direction.NORTH);
//			((TextView)parent2.findViewById(R.id.current_latitude_deg)).setText(String.valueOf(latitude.getDegree()));
//			((TextView)parent2.findViewById(R.id.current_latitude_min)).setText(String.valueOf(latitude.getMinute()));
//			((TextView)parent2.findViewById(R.id.current_latitude_sec)).setText(df.format(latitude.getSecond()));
//			((TextView)parent2.findViewById(R.id.current_longitude_deg)).setText(String.valueOf(longitude.getDegree()));
//			((TextView)parent2.findViewById(R.id.current_longitude_min)).setText(String.valueOf(longitude.getMinute()));
//			((TextView)parent2.findViewById(R.id.current_longitude_sec)).setText(df.format(longitude.getSecond()));
//			((TextView)parent2.findViewById(R.id.current_qibla_deg)).setText(String.valueOf(qibla.getDegree()));
//			((TextView)parent2.findViewById(R.id.current_qibla_min)).setText(String.valueOf(qibla.getMinute()));
//			((TextView)parent2.findViewById(R.id.current_qibla_sec)).setText(df.format(qibla.getSecond()));
		} catch(Exception ex) {
			try {
				java.io.StringWriter sw = new java.io.StringWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(sw, true);
				ex.printStackTrace(pw);
				pw.flush(); sw.flush();
//				((TextView)parent.findViewById(R.id.notes)).setText(sw.toString());
			} catch(Exception ex2) {
				// App must not be open (ex. killed from app history), prevent a force close
			}
		}
	}

	/**
	 * We use this class in a static way by using the following set function.
	 * In the future we may want to be able to display other dates than just today.
	 *  **/
	public static void set(Activity _parent, ScheduleRamadan _day, ArrayList<HashMap<String, String>> _timetable, SimpleAdapter _timetableView) {
		parent2 = _parent;

		day2 = _day;
		timetable2 = _timetable;
		timetableView2 = _timetableView;

		parent2.startService(new Intent(parent2, FillDailyTimetableServiceRamadan.class));
	}
}