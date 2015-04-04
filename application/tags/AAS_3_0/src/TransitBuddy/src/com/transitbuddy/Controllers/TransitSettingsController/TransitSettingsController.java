package com.transitbuddy.Controllers.TransitSettingsController;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.transitbuddy.*;

public class TransitSettingsController extends TabActivity 
{
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.settings_controller_view);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab
		
		// Sets the map selection to ETA Stop Map View
		// controller.GetApp().GetController().SetMapSelection(1);

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, CitySettingsController.class);
		
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("City").setIndicator("City",
	                      res.getDrawable(R.drawable.ic_tab_map))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, ApplicationSettingsController.class);
	   
	    spec = tabHost.newTabSpec("Application").setIndicator("Application",
	                      res.getDrawable(R.drawable.ic_tab_schedule))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	
	    tabHost.setCurrentTab(1);
	}
	
}

