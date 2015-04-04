package com.transitbuddy.TransitBuddy;


import android.app.Application;
import android.content.Intent;

import com.common.types.TransitData;
import com.transitbuddy.SystemSettings.*;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.Controllers.HomeController.*;

public class TransitBuddyApp extends Application 
{
	TransitData mData;
	TransitSettings mSettings;
	TransitBuddyModel mModel;
	
	/**
	 * 
	 */
	public TransitBuddyApp()
	{
		mSettings = new TransitSettings();
		mModel = new TransitBuddyModel();
		
		// TODO Pull the saved transit system from disk.
		mModel.setSystemID("mbta");
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
}
