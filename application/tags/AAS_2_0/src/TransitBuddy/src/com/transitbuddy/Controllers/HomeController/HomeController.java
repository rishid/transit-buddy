package com.transitbuddy.Controllers.HomeController;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.*; 
import com.transitbuddy.Controllers.TransitRouteTypeController.*; 
import com.transitbuddy.Controllers.TransitSettingsController.*; 
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;

import com.transitbuddy.*;

public class HomeController extends ListActivity 
{	
	static final int SETTINGS_ID = Menu.FIRST;
	static final int SYNC_ID     = Menu.FIRST + 1;
	static final String[] home_menu = new String[] {"Routes", "Find Nearest Stop" };
	TransitBuddyModel mModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// Get a reference to the tranistbuddy model.
		TransitBuddyApp app = (TransitBuddyApp)this.getApplicationContext();
		mModel = app.getTransitBuddyModel();
		
		setContentView(R.layout.home_controller_view);

		updateMenuItems();
	}
	
	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) 
	 {	
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		Intent intent = null;
		
		if ( l.getItemAtPosition(position) == "Find Nearest Stop" )
		{
			// Brings up the Nearest Stop View
			intent = new Intent( getApplicationContext(), TransitStopMapController.class);
		} 
		else 
		{
			// Load the route type view.
			intent = new Intent( getApplicationContext(), TransitRouteTypeController.class);
		}
		
		startActivity(intent);
		
	 }
	 
	 // Updates the menu items
	 private void updateMenuItems()
	 {
		 ArrayList<String> systems = new ArrayList<String>();
		 
		 mModel.getTransitSystems(systems);
		 
		 setListAdapter(new ArrayAdapter<String>(this,
		            android.R.layout.simple_list_item_1, home_menu));
		 	 
	 }
	 
	 @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
		 
		MenuItem settingsMenuItem = menu.add(Menu.NONE, SETTINGS_ID, SETTINGS_ID, "Settings");
		MenuItem syncMenuItem = menu.add(Menu.NONE, SYNC_ID, SYNC_ID, "Sync");
	    
		settingsMenuItem.setIcon(android.R.drawable.ic_menu_preferences);
		syncMenuItem.setIcon(android.R.drawable.ic_menu_share);
		
	    // the following line will hide search
	    // when we turn the 2nd parameter to false
	    // menu.setGroupVisible(1, false);
	    
	    return super.onCreateOptionsMenu(menu);
	  }
	 
	 public boolean onOptionsItemSelected (MenuItem item){

		 Intent intent = null;
		 
		 switch (item.getItemId()){

		 	case SETTINGS_ID:
		 		// Actions in case that Settings is pressed 
				intent = new Intent( getApplicationContext(), 
							TransitSettingsController.class);
				
		 	case SYNC_ID:
		 		// Actions in case that Sync is pressed 
		 		intent = new Intent( getApplicationContext(), 
						TransitSettingsController.class);
		 }
		 
		 if ( intent != null )
		 {
			 startActivity(intent);
			 return true;
		 }
		 
		 return false;
	}

}