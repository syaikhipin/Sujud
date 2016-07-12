package com.arifin.sujud.nearmosque;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;

public class MosqueFinderTabGroupActivity extends ActivityGroup {

    private static final String ACTIVITY_TAG = "MosqueFinderActivity";
	private ArrayList<String> activityList;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList = new ArrayList<String>();
        startChildActivity(ACTIVITY_TAG, new Intent(this, MosqueFinderActivity.class));
       
    }
    
    public void startChildActivity(String Id, Intent intent)
	{
		Window window = getLocalActivityManager().startActivity(Id, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		if (window != null)
		{
			activityList.add(Id);
			setContentView(window.getDecorView());
		}
	}
    @Override
	public void finishFromChild(Activity child)
	{
		LocalActivityManager manager = getLocalActivityManager();
		int index = activityList.size() - 1;

		if (index < 1)
		{
			finish();
			return;
		}

		manager.destroyActivity(activityList.get(index), true);
		activityList.remove(index);
		index--;
		String lastId = activityList.get(index);
		Intent lastIntent = manager.getActivity(lastId).getIntent();
		Window newWindow = manager.startActivity(lastId, lastIntent);
		setContentView(newWindow.getDecorView());
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			onBackPressed();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}


	
//	@Override
//	public void onBackPressed()
//	{
//		int length = activityList.size();
//		if (length > 1)
//		{
//			Activity current = getLocalActivityManager().getActivity(activityList.get(length - 1));
//			current.finish();
//		}
//	}
}

