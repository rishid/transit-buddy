package com.transitbuddy.TransitBuddy;


import java.io.IOException;
import java.util.ArrayList;

import android.app.Application;
import android.widget.Toast;

import com.common.types.TransitData;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.SystemSettings.TransitSettings;

public class TransitBuddyApp extends Application 
{
	TransitData mData;
	TransitSettings mSettings;
	TransitBuddyModel mModel;
	
	final int 		PORT_NUM 		= 1100;
	final int 		COMMAND_TIMEOUT = 5000;	// milliseconds
	final String 	HOST_ADDR 		= "129.10.128.235";
	
	boolean loggedIn = false;
	
	/**
	 * 
	 */
	public TransitBuddyApp()
	{
		mSettings = new TransitSettings(this);
		mModel = new TransitBuddyModel(HOST_ADDR,
									   PORT_NUM,
									   COMMAND_TIMEOUT);
		
		ArrayList<String> systems = new ArrayList<String>();
		 
		try
		{
			mModel.getTransitSystems(systems); 
		}
		catch(IOException ioe)
		{
			Toast.makeText(getApplicationContext(), ioe.getMessage(), Toast.LENGTH_LONG);
		}
		 
		// TODO Pull the saved transit system from disk.
		mModel.setSystemID(mModel.getSystemID(1));
	}

	/**
	 * 
	 * @return
	 */
	public TransitBuddyModel getTransitBuddyModel()
	{
		return mModel;
	}
	
	/**
	 * 
	 * @return
	 */
	public TransitSettings getTransitBuddySettings()
	{
		return mSettings;
	}
	
	/**
	 * 
	 * @param s
	 */
	public void setTransitSettings(TransitSettings s)
	{
		mSettings = s;
	}
	
	public void setLoginStatus(boolean status) {
	    loggedIn = status;
	}
	
	public boolean getLoginStatus() {
	    return loggedIn;
	}
}
