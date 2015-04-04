package com.transitbuddy.Controllers.TransitStopController;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.transitbuddy.*;
import com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.*;
import com.transitbuddy.Controllers.TransitStopController.TransitStopScheduleController.TransitStopScheduleController;

public class TransitStopController extends TabActivity 
{	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.transit_info_controller_view);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab
		
		// Sets the map selection to ETA Stop Map View
		// controller.GetApp().GetController().SetMapSelection(1);

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, TransitStopMapController.class);
	
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("map").setIndicator("map",
	                      res.getDrawable(R.drawable.ic_tab_map)).setContent(intent);
	    tabHost.addTab(spec);
	
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, TransitStopScheduleController.class);
	    
	    spec = tabHost.newTabSpec("schedule").setIndicator("schedule",
	                      res.getDrawable(R.drawable.ic_tab_schedule))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	
	    tabHost.setCurrentTab(1);
	}
}
