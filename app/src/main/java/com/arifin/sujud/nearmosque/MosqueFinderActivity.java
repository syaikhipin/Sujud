package com.arifin.sujud.nearmosque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.arifin.sujud.R;
import com.arifin.sujud.universal.ConstantsClass;
import com.arifin.sujud.universal.CustomHttpClient;

public class MosqueFinderActivity extends AppCompatActivity implements OnClickListener, LocationListener, OnScrollListener, OnItemClickListener{

	public static final long MAX_RADIUS_LIMIT = 1000000;
	private static final int MAP_ACTIVITY_REQUEST_CODE = 1;;
	private ListView listViewMosques;
	private MosquesListAdaptor mosquesListAdaptor;
	private ArrayList<JSONObject> mosquesJsonArrayList;
	private Object googleApiKey;
	public String nextPageToken = null;
	private ProgressDialog loadingPrgresBAr;
	private SharedPreferences ifarzSharedPref;
	private GPSTracker gpsTracker;
	private int countChnageLocation = 0;
	private Double latitude = 0.0;
	private Double longitude = 0.0;
	private boolean isthreadRunnong = true;
	public long defaultRadusToSearch = 1000;
	private Button btnLoadMore;
	private boolean isMapActivityReturned = false;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mosque_finder);
        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);
		context=this;
		googleApiKey = getResources().getString(R.string.google_api_key);
		mosquesJsonArrayList = new ArrayList<JSONObject>();
		ifarzSharedPref = getSharedPreferences(ConstantsClass.IFARZ_SHAREDPREFERENCE, MODE_PRIVATE);
		if(ifarzSharedPref.getString(ConstantsClass.LOCATION_NAME, "").equals(""))
			gpsTracker = new GPSTracker(MosqueFinderActivity.this,MosqueFinderActivity.this);
		
		setScreenViews();
		loadingPrgresBAr = new ProgressDialog(context);
		loadingPrgresBAr.setMessage("Loading...");
	}

	private void setScreenViews() {
		
		
		btnLoadMore = (Button)findViewById(R.id.btn_load_more);
		btnLoadMore.setOnClickListener(this);
		btnLoadMore.setVisibility(View.VISIBLE);
		
		listViewMosques = (ListView)findViewById(R.id.listview_mosque);
		mosquesListAdaptor = new MosquesListAdaptor(context);
		listViewMosques.setAdapter(mosquesListAdaptor);
		listViewMosques.setOnScrollListener(this);
		listViewMosques.setOnItemClickListener(this);
		
		
		try {
				latitude = (double) ifarzSharedPref.getFloat(ConstantsClass.LATI_KEY, 0.0f);
				longitude = (double) ifarzSharedPref.getFloat(ConstantsClass.LONG_KEY, 0.0f);
		} catch (Exception e) {
		}
 	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		isMapActivityReturned  = false;
		super.onActivityResult(requestCode, resultCode, data);
	}
	private class MosquesListAdaptor extends BaseAdapter
	{

		private LayoutInflater layoutInflator;
		

		MosquesListAdaptor(Context context)
		{
			layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			if(mosquesJsonArrayList!=null)
				return mosquesJsonArrayList.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int location, View convertedView, ViewGroup viewGroup) {
			MosqueViewHolder mosqueViewHolder;
			if(convertedView == null)
			{
				convertedView = layoutInflator.inflate(R.layout.rowitem_mosque, null);
				mosqueViewHolder = new MosqueViewHolder();
				mosqueViewHolder.textViewMosqueName = (TextView) convertedView.findViewById(R.id.textview_mosque_name);
				mosqueViewHolder.textViewMosqueDistance = (TextView) convertedView.findViewById(R.id.textview_mosque_distance);
				convertedView.setTag(mosqueViewHolder);
			}
			else
				mosqueViewHolder = (MosqueViewHolder) convertedView.getTag();
			
			try {
				mosqueViewHolder.textViewMosqueName.setText(mosquesJsonArrayList.get(location).getString("name"));
				mosqueViewHolder.textViewMosqueDistance.setText(
						mosquesJsonArrayList.get(location).getJSONObject("distance").getString("text"));
			} catch (JSONException e) {
			}
			return convertedView;
		}
		
	}
	
	private class MosquesDataLoadingThread extends AsyncTask<Void, Void, ArrayList<JSONObject>>
	{

		private String nextPageV;
		

		public MosquesDataLoadingThread(String nextPageTkn)
		{
			this.nextPageV = nextPageTkn;
		}
		@Override
		protected ArrayList<JSONObject> doInBackground(Void... arg0) {
			
			String apiUrl = "https://maps.googleapis.com/maps/api/place/search/json?location="
			+latitude+","+longitude+"&radius="+String.valueOf(defaultRadusToSearch) +"&sensor=true&types=mosque&key="
			+googleApiKey+"&pagetoken="+nextPageV;
			JSONArray tempJsonArry = null;
			ArrayList<JSONObject> tempJsonArrayList = new ArrayList<JSONObject>();
			String result;
			try {
				result  = CustomHttpClient.executeGet(apiUrl);
				if(result!=null)
				{
					try {
							JSONObject tempRsultJson = new JSONObject(result);
							tempJsonArry = tempRsultJson.getJSONArray("results");
							for (int i = 0; i < tempJsonArry.length(); i++) 
							{
								try {
									String distanceApiUrl = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+latitude+","+longitude
											+"&destinations="+tempJsonArry.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat")
											+","+tempJsonArry.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng")+"&sensor=false";
									
									String distanceResult = CustomHttpClient.executeGet(distanceApiUrl);
									tempJsonArry.getJSONObject(i).put("distance", new JSONObject(distanceResult).getJSONArray("rows")
											.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance"));
								} catch (Exception e) {
								}
								
							}
							if(tempRsultJson.has("next_page_token"))
								nextPageToken = tempRsultJson.getString("next_page_token");
							else
								nextPageToken = null;
							
							if(tempJsonArry!=null && tempJsonArry.length()>0)
							{
								for (int i = 0; i < tempJsonArry.length(); i++) {
									tempJsonArrayList.add(tempJsonArry.getJSONObject(i));
								}
								
							}
						} catch (Exception e) {
					}
				}
			} catch (Exception e) {
			}
			return tempJsonArrayList;
		}
		
		@Override
		protected void onPostExecute(ArrayList<JSONObject> resultArry) {
			isthreadRunnong = false;
			if(loadingPrgresBAr!=null)
				loadingPrgresBAr.dismiss();
			if(resultArry!=null)
			{
				if(mosquesJsonArrayList.size()==0 && resultArry.size()==0 && defaultRadusToSearch<MAX_RADIUS_LIMIT )
				{
					if(loadingPrgresBAr!=null)
						loadingPrgresBAr.show();
					nextPageToken = "";
					defaultRadusToSearch *= 10;
					isthreadRunnong = true;
					mosquesJsonArrayList.clear();
					new MosquesDataLoadingThread("").execute();
				}
				else
				{
					
					mosquesJsonArrayList.addAll(resultArry);
					Collections.sort(mosquesJsonArrayList,new JsonListComparator());
				}
				
			}
			if(mosquesJsonArrayList.size()<20)
				btnLoadMore.setVisibility(View.VISIBLE);
			mosquesListAdaptor.notifyDataSetChanged();
			super.onPostExecute(resultArry);
		}
	}
	
	private class JsonListComparator implements Comparator<JSONObject> 
	{
		public int compare(JSONObject lhs, JSONObject rhs) {
			try {
				float lhsDist = Float.valueOf(lhs.getJSONObject("distance").getString("text").replace("km", "").trim());
				float rhsDist = Float.valueOf(rhs.getJSONObject("distance").getString("text").replace("km", "").trim());
				if(lhsDist>rhsDist)
					return 1;
				else if(lhsDist<rhsDist)
					return -1;
				else
					return 0;
			} catch (JSONException e) {
				return  0;
			}
			
		}
}
//	Collections.sort(tempJsonArrayList,new Comparator<JSONObject>() {
//
//		@Override
//		public int compare(JSONObject lhs, JSONObject rhs) {
//			try {
//				return lhs.getJSONObject("distance").getString("text").compareToIgnoreCase(rhs.getJSONObject("distance").getString("text"));
//			} catch (JSONException e) {
//			}
//			return  -1;
//		}
//	});
	
	@Override
	protected void onResume() {
		try {
			if(isMapActivityReturned == false)
			{
				
				latitude = (double) ifarzSharedPref.getFloat(ConstantsClass.LATI_KEY, 0.0f);
				longitude = (double) ifarzSharedPref.getFloat(ConstantsClass.LONG_KEY, 0.0f);
				
				
				defaultRadusToSearch = 1000;
				nextPageToken =  "";
				isthreadRunnong = true;
				mosquesJsonArrayList.clear();
				new MosquesDataLoadingThread("").execute();
				loadingPrgresBAr.show();
			}
			else
				isMapActivityReturned = false;
		} catch (Exception e) {
		}
		
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
	@Override
	public void onLocationChanged(Location location) {
		
		countChnageLocation ++;
		if(location!=null && countChnageLocation<=2)
			if(ifarzSharedPref.getFloat(ConstantsClass.LATI_KEY, 0.0f)!=location.getLatitude() || ifarzSharedPref.getFloat(ConstantsClass.LONG_KEY, 0.0f)!=location.getLongitude())
			{	
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				ifarzSharedPref.edit().putFloat(ConstantsClass.LATI_KEY, (float) location.getLatitude()).commit();
				ifarzSharedPref.edit().putFloat(ConstantsClass.LONG_KEY, (float) location.getLongitude()).commit();
			}
		if(countChnageLocation>=2)
			{
				
				try {
					gpsTracker.stopUsingGPS();
					gpsTracker.stopSelf();
				} catch (Exception e) {
				}
				try {
					nextPageToken = "";
					isthreadRunnong = true;
					defaultRadusToSearch = 1000;
					new MosquesDataLoadingThread("").execute();
					loadingPrgresBAr.show();
				} catch (Exception e) {
				}
			}
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		if((firstVisibleItem+visibleItemCount)==totalItemCount && totalItemCount!=0)
			btnLoadMore.setVisibility(View.VISIBLE);
//		else if(btnLoadMore.getVisibility() == View.VISIBLE)
//		{
//			btnLoadMore.setVisibility(View.GONE);
//			listViewMosques.invalidate();
//		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onClick(View clickedView) {
		
		 if(clickedView == btnLoadMore)
		{
//			btnLoadMore.setVisibility(View.GONE);
//			btnLoadMore.invalidate();
			
			if(isthreadRunnong==false && nextPageToken!=null)
				{
					loadingPrgresBAr.show();
					new MosquesDataLoadingThread(nextPageToken).execute();
					isthreadRunnong = true;
				}
				
				else if( nextPageToken == null && defaultRadusToSearch<MAX_RADIUS_LIMIT)
				{
					if(loadingPrgresBAr!=null)
						loadingPrgresBAr.show();
					mosquesJsonArrayList.clear();
					nextPageToken = "";
					defaultRadusToSearch *= 10;
					isthreadRunnong = true;
					new MosquesDataLoadingThread("").execute();
				}
		}
	}
//    1F:29:70:11:E2:82:FD:8F:7B:43:5C:48:8F:B8:0A:DC:91:FE:95:A5
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Intent mapActivityIntent = new Intent(context,MapDirectionDisplayActivity.class);
		
		mapActivityIntent.putExtra("point_one_lat", latitude);
		mapActivityIntent.putExtra("point_one_lon", longitude);
		try {
			mapActivityIntent.putExtra("point_location_one_title", ifarzSharedPref.getString(ConstantsClass.LOCATION_NAME, ""));
			mapActivityIntent.putExtra("point_location_two_title", mosquesJsonArrayList.get(position).getString("name"));
			mapActivityIntent.putExtra("point_two_lat", mosquesJsonArrayList.get(position).getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
			mapActivityIntent.putExtra("point_two_lon", mosquesJsonArrayList.get(position).getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
		} catch (JSONException e) {
		}
		isMapActivityReturned  = true;
		startActivityForResult(mapActivityIntent,MAP_ACTIVITY_REQUEST_CODE);
	}

}
