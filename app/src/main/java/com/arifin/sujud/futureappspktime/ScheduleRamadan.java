package com.arifin.sujud.futureappspktime;

import android.content.Context;

import com.arifin.sujud.R;
import com.arifin.sujud.futureappspktime.jitl.Jitl;
import com.arifin.sujud.futureappspktime.jitl.Method;
import com.arifin.sujud.futureappspktime.jitl.Prayer;
import com.arifin.sujud.futureappspktime.jitl.astro.Location;
import com.arifin.sujud.MainActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleRamadan {
	Context context;
//	GPSTracker gps;
//	double lati, longi;
//	GPSTracker gps;

	private GregorianCalendar[] scheduleramadan = new GregorianCalendar[7];
	private boolean[] extremes = new boolean[7];
	private fi.joensuu.joyds1.calendar.Calendar hijriDate;

	private static ScheduleRamadan today;



	public ScheduleRamadan(GregorianCalendar day) {
//		getlocation();
//		gps = new GPSTracker(gps);
		float latitude = (float) MainActivity.lati;
		float longitude = (float)MainActivity.longi;


		Method method = CONSTANTramadan.CALCULATION_METHODS[VARIABLE.settings.getInt("calculationMethodsIndex", CONSTANT.DEFAULT_CALCULATION_METHOD)].copy();
		method.setRound(CONSTANTramadan.ROUNDING_TYPES[VARIABLE.settings.getInt("roundingTypesIndex", 2)]);

		Location location = new Location(latitude,longitude, getGMTOffset(), 0);

//		Location location = new Location(VARIABLE.settings.getFloat("latitude", latitude), VARIABLE.settings.getFloat("longitude", longitude), getGMTOffset(), 0);
		location.setSeaLevel(VARIABLE.settings.getFloat("altitude", 0) < 0 ? 0 : VARIABLE.settings.getFloat("altitude", 0));
		location.setPressure(VARIABLE.settings.getFloat("pressure", 1010));
		location.setTemperature(VARIABLE.settings.getFloat("temperature", 10));

		Jitl itl = CONSTANTramadan.DEBUG ? new DummyJitl(location, method) : new Jitl(location, method);
		Prayer[] dayPrayers = itl.getPrayerTimes(day).getPrayers();
		Prayer[] allTimes = new Prayer[]{dayPrayers[0], dayPrayers[1], dayPrayers[2], dayPrayers[3], dayPrayers[4], dayPrayers[5], itl.getNextDayFajr(day)};

		for(short i = CONSTANTramadan.FAJR1; i <= CONSTANTramadan.MAGRIB1; i++) { // Set the times on the schedule
			scheduleramadan[i] = new GregorianCalendar(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH), allTimes[i].getHour(), allTimes[i].getMinute(), allTimes[i].getSecond());
			scheduleramadan[i].add(Calendar.MINUTE, VARIABLE.settings.getInt("offsetMinutes", 0));
			extremes[i] = allTimes[i].isExtreme();
		}
		scheduleramadan[CONSTANTramadan.MAGRIB1].add(Calendar.DAY_OF_MONTH, 1); // Next fajr is tomorrow

		hijriDate = new fi.joensuu.joyds1.calendar.IslamicCalendar();
	}
	public GregorianCalendar[] getTimesramadan() {
		return scheduleramadan;
	}
	public boolean isExtreme(int i) {
		return extremes[i];
	}
	public short nextTimeIndex() {
		Calendar now = new GregorianCalendar();
		if(now.before(scheduleramadan[CONSTANTramadan.FAJR1])) return CONSTANTramadan.FAJR1;
		for(short i = CONSTANTramadan.FAJR1; i < CONSTANTramadan.MAGRIB1; i++) {
			if(now.after(scheduleramadan[i]) && now.before(scheduleramadan[i + 1])) {
				return ++i;
			}
		}
		return CONSTANTramadan.MAGRIB1;
	}

	private boolean currentlyAfterSunset() {
		Calendar now = new GregorianCalendar();
		return now.after(scheduleramadan[CONSTANTramadan.MAGRIB1]);
	}
	public String hijriDateToString(Context context) {
		boolean addedDay = false;
		if(currentlyAfterSunset()) {
			addedDay = true;
			hijriDate.addDays(1);
		}
		String day = String.valueOf(hijriDate.getDay());
		String month = context.getResources().getStringArray(R.array.hijri_months)[hijriDate.getMonth() - 1];
		String year = String.valueOf(hijriDate.getYear());
		if(addedDay) {
			hijriDate.addDays(-1); // Revert to the day independent of sunset
		}
		return day + " " + month;
	}

	public static ScheduleRamadan today(int day, int month, int year) {
		GregorianCalendar now = new GregorianCalendar();
        now.set(GregorianCalendar.YEAR, year);
        now.set(GregorianCalendar.MONTH, month);
        now.set(GregorianCalendar.DATE, day);

		if(today == null) {
			today = new ScheduleRamadan(now);

		} else {
			GregorianCalendar fajr = today.getTimesramadan()[CONSTANTramadan.FAJR1];
			if(fajr.get(Calendar.YEAR) != now.get(Calendar.YEAR) || fajr.get(Calendar.MONTH) != now.get(Calendar.MONTH) || fajr.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH)) {
				today = new ScheduleRamadan(now);
			}
		}
		return today;
	}
	public static void setSettingsDirty() {
		today = null; // Nullifying causes a new today to be created with new settings when today() is called
	}
	public static boolean settingsAreDirty() {
		return today == null;
	}

	public static double getGMTOffset() {
		Calendar now = new GregorianCalendar();
		int gmtOffset = now.getTimeZone().getOffset(now.getTimeInMillis());
		return gmtOffset / 3600000;
	}
	public static boolean isDaylightSavings() {
		Calendar now = new GregorianCalendar();
		return now.getTimeZone().inDaylightTime(now.getTime());
	}
}