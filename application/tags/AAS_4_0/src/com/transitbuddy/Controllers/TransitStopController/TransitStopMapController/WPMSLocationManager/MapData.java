package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

import java.util.List;

import android.location.Location;
import android.net.wifi.ScanResult;

public class MapData
{
	private List<ScanResult> scanResults;
	private Location location;

	
	public MapData()
	{
		
	}
	
	
	// TODO: Make safe copy
	public MapData(List<ScanResult> scanResults, Location location)
	{
		this.scanResults = scanResults;
		this.location = location;
	}
	
	
	// TODO: return safe copy
	public List<ScanResult> getScanResults()
	{
		return this.scanResults;
	}
	
	// TODO: MAke safe copy
	public void setScanResults(List<ScanResult> scanResults)
	{
		this.scanResults = scanResults; 
	}
	
	// TODO: make safe copy
	public Location getLocation()
	{
		return this.location;
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
}
