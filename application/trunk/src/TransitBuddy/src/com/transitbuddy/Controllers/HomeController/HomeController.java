package com.transitbuddy.Controllers.HomeController;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.common.utilities.ResponseResult.ResponseStatus;
import com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.*;
import com.transitbuddy.Controllers.TransitRouteTypeController.*; 
import com.transitbuddy.Controllers.TransitSettingsController.*; 
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.SystemSettings.TransitSettings;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;
import com.transitbuddy.enumerations.MapType;

import com.transitbuddy.*;

public class HomeController extends ListActivity 
{	
	static final int SETTINGS_ID = Menu.FIRST;
	static final int SYNC_ID     = Menu.FIRST + 1;
	static final String[] home_menu = new String[] {"Routes", "Find Nearest Stop" };
	TransitBuddyModel mModel;
	TransitSettings mSettings;
	ProgressDialog mProgressDialog;
	DownloadSystemsTask mDownloadSystemsTask = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
			
		// Get a reference to the tranistbuddy model.
		TransitBuddyApp app = (TransitBuddyApp)this.getApplicationContext();
		mModel = app.getTransitBuddyModel();
		mSettings = app.getTransitBuddySettings();
		
		setTitle( getString(R.string.title_prefix) + " " + mSettings.getSelectedCity());
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
			
		ShowProgressAndDownloadData();	
	}
	
	private void ShowProgressAndDownloadData()
	{
		mProgressDialog = ProgressDialog.show(this, 
				  							  "TransitBuddy" , 
				  							  "Loading. Please wait ...",
				  							  true,
				  							  true,
				  							  new DialogInterface.OnCancelListener() 
											  {
												@Override
												public void onCancel(DialogInterface dialog) 
												{
													if(mDownloadSystemsTask != null)
													{
														mDownloadSystemsTask.cancel(true);
														mDownloadSystemsTask = null;
													}
												}
											});

		mDownloadSystemsTask = new DownloadSystemsTask();
		mDownloadSystemsTask.execute();
	}
	
	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) 
	 {	
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		Intent intent = null;
		
		if ( l.getItemAtPosition(position) == home_menu[1])
		{
			// Sets the Map ID Type 
			mModel.setMapID(MapType.NearbyStops);
			
			// Brings up the Nearest Stop View
			intent = new Intent(getApplicationContext(), TransitStopMapController.class);
		} 
		else 
		{
			// Load the route type view.
			intent = new Intent(getApplicationContext(), TransitRouteTypeController.class);
		}
		
		startActivity(intent);
	 }
	 
	 // Updates the menu items
	 private void updateMenuItems()
	 {
		 setListAdapter(new ArrayAdapter<String>(this,
		            android.R.layout.simple_list_item_1, home_menu));
		 	 
	 }
	 
	 @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
		 
		MenuItem settingsMenuItem = menu.add(Menu.NONE, SETTINGS_ID, SETTINGS_ID, "Settings");   
		settingsMenuItem.setIcon(android.R.drawable.ic_menu_preferences);
	 
	    return super.onCreateOptionsMenu(menu);
	  }
	 
	 public boolean onOptionsItemSelected (MenuItem item){

		 Intent intent = null;
		 
		 switch (item.getItemId()){

		 	case SETTINGS_ID:
		 		// Actions in case that Settings is pressed 
				intent = new Intent(getApplicationContext(), 
							TransitSettingsController.class);
		 }
		 
		 if ( intent != null )
		 {
			 startActivity(intent);
			 return true;
		 }
		 
		 return false;
	}

	private class DownloadSystemsTask extends AsyncTask<Void, Void, SystemsResult>
	{
		@Override
		protected SystemsResult doInBackground(Void... arg0) 
		{
			String errorMessage = "";
			
			/*
	         * Set the selected city either by prompting the user to set it or getting the 
	         * selected city from disk.
	         */
	        String selectedSystem = mSettings.getSelectedCity();
	        boolean loadSettings = true;
	        boolean error = true;
	        
	        ArrayList<String> systems = new ArrayList<String>();
	        ResponseStatus status = ResponseStatus.Failed; 
	        
	        try
	        {
	            status = mModel.getTransitSystems(systems); 
	        }
	        catch(IOException ioe)
	        {
	        	status = ResponseStatus.TimedOut;
	            errorMessage = ioe.getMessage();
	        }
	        
	        if(status != ResponseStatus.Completed)
	        {
	        	error = true;
	        	errorMessage += " Error retrieving supported systems.";
	        	
	        	switch(status)
				{
				case TimedOut:
					errorMessage += " Connection timed out!";
					break;
				case Failed:
					errorMessage += " Connection failed!";
					break;
				}
	        }
	        else
	        {
	        	error = false;
	        	
		        // Has the city been set?
		        if((selectedSystem == null) 			||
		           ((systems != null) && (systems.contains(selectedSystem) == false)))
		        {
		        	loadSettings = true;
		        }
		        else
		        {
		        	loadSettings = false;
		        }
	        }
			
			return new SystemsResult(loadSettings, error, errorMessage, selectedSystem);
		}	
		
		protected void onPostExecute(SystemsResult result) 
		{
			if(result.getError() == false)
			{
				if(result.getLoadSettings())
				{
					// Load the settings controller so the user can select their city.
		        	Intent intent = null;
		    		 
		    		intent = new Intent(HomeController.this, TransitSettingsController.class);
		    		TransitSettingsController.setSelectedTab(TransitSettingsController.CITY_TAB);
		    		
		    		startActivity(intent);
				}
				else
				{
					mModel.setSystemID(mModel.getSystemID(result.getSelectedSystem()));
		            
		            setContentView(R.layout.home_controller_view);
	
		    		updateMenuItems();
				}
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(HomeController.this);
	
				builder.setMessage(result.getErrorMessage())
			       	   .setCancelable(false)
			       	   .setPositiveButton("Retry", new DialogInterface.OnClickListener() 
			       	   {
			       		   public void onClick(DialogInterface dialog, int id) 
				           {
				        	   ShowProgressAndDownloadData();
				           }
				       })
				       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
				       {
				           public void onClick(DialogInterface dialog, int id) 
				           {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}

			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	private class SystemsResult
	{
		boolean mLoadSettings;
		boolean mError;
		String mErrorMessage;
		String mSelectedSystem;
		
		public SystemsResult(boolean loadSettings,
							 boolean error,
							 String errorMessage, 
							 String selectedSystem)
		{
			mLoadSettings 	= loadSettings;
			mErrorMessage 	= errorMessage;
			mSelectedSystem = selectedSystem;
			mError 			= error;
		}
		
		public String getSelectedSystem()
		{
			return mSelectedSystem;
		}
		
		public boolean getError()
		{
			return mError;
		}
		
		public String getErrorMessage()
		{
			return mErrorMessage;
		}
		
		public boolean getLoadSettings()
		{
			return mLoadSettings;
		}
	}
}