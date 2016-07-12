package com.arifin.sujud.nearmosque;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service //implements LocationListener 
{

	private final Context mContext;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;

	Location location;
	double latitude; 
	double longitude;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; 
	private static final long MIN_TIME_BW_UPDATES = 0; 
	protected LocationManager locationManager;
	private LocationListener loctionListener;

	public GPSTracker(Context context,LocationListener listener) {
		this.mContext = context;
		this.loctionListener = listener;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			loctionUpdate();
				

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}
	public void stopUsingGPS(){
		
		if(loctionListener != null){
			locationManager.removeUpdates(loctionListener);
		}		
	}
	public void loctionUpdate()
	{
		try{
		if (!isGPSEnabled && !isNetworkEnabled) {
		} else {
//			this.canGetLocation = true;
			if (isNetworkEnabled) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER,
						MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, loctionListener);
				Log.d("Network", "Network");

			}
			if (isGPSEnabled) {
				if (location == null) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, loctionListener);
					Log.d("GPS Enabled", "GPS Enabled");

				}
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}
		
		return latitude;
	}
	
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}
		return longitude;
	}

	public boolean canGetLocation() {
		if(getLatitude()>0.0)
			return true;
		else
			return false;
	}
	
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
   	 
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
        alertDialog.show();
	}

//	@Override
//	public void onLocationChanged(Location location) {
//		Log.e("location ", "Changed");
//		if(location!=null)
//		{
//			
//			latitude = location.getLatitude();
//			longitude = location.getLongitude();
//		}
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	public String getLocationAddressTitle()
	{
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
    	List<Address> addresList = null;
    	try {
    			addresList = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.toString();
		}
    	Address address;
    	String addressLine = null;
		if(addresList!=null && addresList.size()>0)
		{
    		address = addresList.get(0);
    		addressLine = address.getAddressLine(0)+" "+address.getAddressLine(1);
    		
		}
		return addressLine;
	}

}
//if (locationManager != null) {
//Criteria criteria = new Criteria();
//String bestProvider = locationManager.getBestProvider(criteria, false);
//location = locationManager.getLastKnownLocation(bestProvider);
//
//if (location != null) {
//	latitude = location.getLatitude();
//	longitude = location.getLongitude();
//}
//}
