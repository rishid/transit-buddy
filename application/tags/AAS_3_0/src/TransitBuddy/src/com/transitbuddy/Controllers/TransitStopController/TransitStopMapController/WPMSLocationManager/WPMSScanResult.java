package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

public class WPMSScanResult 
{
	public final String BSSID;
	public final int LEVEL;
	public final WPMSLocation LOCATION;
			
	
	public WPMSScanResult(String bssid, int level, WPMSLocation location) 
	{
		this.BSSID = bssid;
		this.LEVEL = level;
		// TODO: Safe copy
		this.LOCATION = location;
	}
}
