package com.transitbuddy.Controllers.TransitStopController;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.transitbuddy.*;
import com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.*;
import com.transitbuddy.Controllers.TransitStopController.TransitStopScheduleController.TransitStopScheduleController;

public class TransitStopController extends TabActivity 
{	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.transit_info_controller_view);

		displayTabs();
	}
	
	/**
	 * Displays the tabs
	 */
	private void displayTabs()
	{
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		
		// Schedule Tab
		TabSpec tabSpec1 = tabHost.newTabSpec("schedule");
		tabSpec1.setIndicator("schedule", res.getDrawable(R.drawable.ic_tab_schedule));
		tabSpec1.setContent(new Intent(this.getApplicationContext(), TransitStopScheduleController.class));
		tabHost.addTab(tabSpec1);
		
		// Transit Stop Map Controller Tab
		TabSpec tabSpec2 = tabHost.newTabSpec("map");
		tabSpec2.setIndicator("map", res.getDrawable(R.drawable.ic_tab_map));
		tabSpec2.setContent(new Intent(this.getApplicationContext(), TransitStopMapController.class));
		tabHost.addTab(tabSpec2);
			
	    tabHost.setCurrentTab(0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	} 
}
