package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;


public class WiFiPositioningSystem extends WPSPublisher 
{
	private final Context context;
	private final WifiManager wifiManager;
	
	private Map<String, WAPResults> wifiMap;
	
	private WPMSLocation location;
	private List<ScanResult> scanResults;

	
	public WiFiPositioningSystem(Context context)
	{	
		// Save off the instantiating activity for later use
		this.context = context;
		
		Log.d("Test", "Context set");
		
		// Get the wifi manager for this context
		this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		
		// Initialize wifiMap
		this.wifiMap = new HashMap<String, WAPResults>();
	}
	
	
	/**
	 * Determine the possible locations based on the current scanResults
	 */
	private void calculatePosition()
	{
		// List that will collect all the possible locations duplicates 
		// possible as each scanResult could produce the same location
		ArrayList<WPMSLocation> possibleLocations = new ArrayList<WPMSLocation>();
		
		// Iterate over current scan results creating a set of valid positions for each
		for (ScanResult scanResult: scanResults)
		{
			WAPResults wapResults = wifiMap.get(scanResult.BSSID);
			
			if (null != wapResults)
			{
				possibleLocations.addAll(wapResults.getPossibleLocations(scanResult.level));				
			}
		}
		
		// Get unique locations
		// Remove duplicates by creating a set of locations to iterate over
		HashSet<WPMSLocation> distinctPossibleLocations = 
			new HashSet<WPMSLocation>(possibleLocations);
		
		// Now calculate the bestLocation based on the number of WAPS that each
		// location qualifies for
		WPMSLocation bestLocation = null;
		int maxLocationCount = 0;
		
		for (WPMSLocation distinctPossibleLocation: distinctPossibleLocations)
		{
			int thisLocationCount = 
				Collections.frequency(possibleLocations, distinctPossibleLocation);
			
			if (maxLocationCount < thisLocationCount)
			{
				maxLocationCount = thisLocationCount;
				bestLocation = distinctPossibleLocation;
			}
		}
		
		location = bestLocation;
	}
	

	/**
	 * Begin active position calculation
	 */
	public void startPositioning()
	{
		// TODO: Remove
		Log.d("Test", "Entering startPositioning");
		
		
		// Load Map of Wifi Strengths
		loadMap();
		
		Log.d("Test", "After loadMap");
		
		// Register event listener for scan completion
		context.registerReceiver(broadcastReceiver, 
				new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		Log.d("Test", "After registerReceiver");
		
		// Begin scanning for wireless access points
		wifiManager.startScan();
		
		Log.d("Test", "Exiting startPositioning");
	}

	
	/**
	 * Cease active position calculations
	 */
	public void stopPositioning()
	{
		// Stop listening for WAP Scan Updates
		context.unregisterReceiver(broadcastReceiver);
	}
	
	
	
	private void publish()
	{
		// Only publish if we've established a location
		if (null != location)
		{
			publish(location);
		}
	}

	
	
	/**
	 * Loads a map of Wifi signal strengths into memory
	 */
	private void loadMap()
	{
		// TODO: Integrate this with the storage subsystem instead of dummy data
		wifiMap.clear();
		
		// BMD: MapStore declaration / initialization.
		MapStorer mapStorer = new MapStorer(context);

		wifiMap = mapStorer.fetchMap();
	}
	
	
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
	    	if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
	    	{	// WiFi Scan Results Received
				// Process event
				scanResults = wifiManager.getScanResults();
				
				// Determine position
				calculatePosition();
				
				// Update subscribers
				WiFiPositioningSystem.this.publish();
				
				// Request another scan
				wifiManager.startScan();
	    	}
		}
	};
}

