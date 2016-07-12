package com.arifin.sujud.nearmosque;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import com.arifin.sujud.R;
import com.arifin.sujud.universal.CustomHttpClient;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
public class MapDirectionDisplayActivity extends AppCompatActivity implements OnClickListener
{
    Context context;
    Drawable drawable,drawableDot;
    GoogleMap mapView ;
    boolean flag=true;
	private GPSTracker gpsTracker;
	private Bundle lastActivityData;
	private Button btnBack;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);
        gpsTracker = new GPSTracker(MapDirectionDisplayActivity.this,new MyLocationListener(this));
        context=getApplicationContext();
        
        //setScreenViews();
        getLastActivityInfo();
        
        setUpMapIfNeeded();
    }
    
   
//    private void setScreenViews() {
//    	
//    	btnBack = (Button)findViewById(R.id.bt_back);
//		btnBack.setOnClickListener(this);
//		btnBack.setVisibility(View.VISIBLE);
//		
//	}


	private void getLastActivityInfo() 
    {
		lastActivityData = getIntent().getExtras();
	}

    private void setUpMapIfNeeded() {
        if (mapView == null) 
        {
        	mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mapView != null) 
            {
            	CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lastActivityData.getDouble("point_one_lat")
            			, lastActivityData.getDouble("point_one_lon")), 15);
            	mapView.animateCamera(cameraUpdate);
            }
        }
        
        new DirectionLoadingThread().execute();
    }
	
    
    @Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
		
	}
	@Override
	protected void onResume() {
		
		try {
			if(gpsTracker!=null)
				gpsTracker.loctionUpdate();
		} catch (Exception e) {
		}
		super.onResume();
	}
	@Override
	public void finish() {
		try {
			if(gpsTracker!=null)
			{
				gpsTracker.stopUsingGPS();
				gpsTracker.stopSelf();
			}
			
		} catch (Exception e) {
		}
		
		super.finish();
	}
    public class MyLocationListener implements LocationListener
    {
        Context context;
       
        MyLocationListener(Context context)
        {
            this.context=context;
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Toast.makeText(context,"Gps Disabled",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Toast.makeText(context,"Gps Enabled",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

        @Override
        public void onLocationChanged(Location location)
        {
            double lat = location.getLatitude();
            double lng= location.getLongitude();
        }
    }
    
    private class DirectionLoadingThread extends AsyncTask<Void, Void, JSONArray>
    {

		@Override
		protected JSONArray doInBackground(Void... arg0) {
			
			String apiUrl ="http://maps.googleapis.com/maps/api/directions/json?origin="+lastActivityData.getDouble("point_one_lat")+","+lastActivityData.getDouble("point_one_lon")
					+"&destination="+lastActivityData.getDouble("point_two_lat")
					+","+lastActivityData.getDouble("point_two_lon")+"&sensor=false";
					String result;
					try {
						result  = CustomHttpClient.executeGet(apiUrl);
						if(result!=null)
						{
							return new JSONObject(result).getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
						}
					}catch (Exception e) {
					}
			return null;
		}
    	
		@Override
		protected void onPostExecute(JSONArray resultArray) {
			 if (mapView != null) 
	            {
	            	mapView.addMarker(new MarkerOptions()
	                .position(new LatLng(lastActivityData.getDouble("point_one_lat"), lastActivityData.getDouble("point_one_lon")))
	                .title(lastActivityData.getString("point_location_one_title")));
	            
	            	LatLng lastLonLat = new LatLng(lastActivityData.getDouble("point_one_lat"), lastActivityData.getDouble("point_one_lon"));
					if(resultArray!=null)
					{
						LatLng newLonLat = null ;
						for (int i = 0; i < resultArray.length(); i++) {
							
							try {
								newLonLat = new LatLng(resultArray.getJSONObject(i).getJSONObject("end_location").getDouble("lat")
										, resultArray.getJSONObject(i).getJSONObject("end_location").getDouble("lng"));
								mapView.addPolyline(new PolylineOptions().add(lastLonLat
								,newLonLat)
							        	.width(3)
							        	.color(Color.BLUE));
				                
							
							} catch (JSONException e) {
							}
							lastLonLat = newLonLat;
						}
						
					}
					
					mapView.addMarker(new MarkerOptions()
	                .position(new LatLng(lastActivityData.getDouble("point_two_lat"), lastActivityData.getDouble("point_two_lon")))
	                .title(lastActivityData.getString("point_location_two_title")));
	            }
			super.onPostExecute(resultArray);
		}
    }

	@Override
	public void onClick(View clickedView) {
		
		if(clickedView ==  btnBack)
		{
			finish();
		}
	}
}