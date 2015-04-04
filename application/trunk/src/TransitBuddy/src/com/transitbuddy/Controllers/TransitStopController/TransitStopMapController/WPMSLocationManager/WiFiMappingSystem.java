package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

public class WiFiMappingSystem extends WMSPublisher 
{
	private final Context context;
	private final LocationManager locationManager;
	private final WifiManager wifiManager;
	
	private Location location;
	private List<ScanResult> scanResults;
	
	public WiFiMappingSystem(Context context)
	{
		// Save off the instantiating activity for later use
		this.context = context;
		
		// Get the location manager for this context
		this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
		
		// Get the wifi manager for this context
		this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
	}

	public void map()
	{
		// Register event listener for scan completion
		context.registerReceiver(broadcastReceiver, 
				new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			
		// Start scanning for position
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
		
		// Begin scanning for wireless access points
		wifiManager.startScan();
	}
	
	public void stopMapping()
	{
		// Stop listening for WAP Scan Updates
		context.unregisterReceiver(broadcastReceiver);
		
		// Stop listening for Location Updates
		locationManager.removeUpdates(locationListener);
	}
	
	
	private void publish()
	{
		// Only publish if we've established a location
		// Allow the subscriber to determine whether or not the data is good
		// TODO: Remove conditional
//		if (null != location)
		{
			// Update current map data
			currentMapData.setScanResults(scanResults);
			currentMapData.setLocation(location);
			
			// Push data to subscribers
			// TODO: Refactor away from passed parameter
			publish(currentMapData);
		}
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
										
					// Update subscribers
					WiFiMappingSystem.this.publish();
					
					// Request another scan
					wifiManager.startScan();
		    	}
			}
		};
		
	private LocationListener locationListener = new LocationListener()
		{
			@Override
			public void onLocationChanged(Location location) 
			{
				// Called when a new location is found by the network location provider.
				WiFiMappingSystem.this.location = location;
				Toast toast;
	        	toast = Toast.makeText(context, "GPS Location Changed", Toast.LENGTH_SHORT);
	        	toast.show();
			}
			
			@Override
		    public void onStatusChanged(String provider, int status, Bundle extras) {}

			@Override
		    public void onProviderEnabled(String provider) {}

			@Override
		    public void onProviderDisabled(String provider) {}
		};
}
