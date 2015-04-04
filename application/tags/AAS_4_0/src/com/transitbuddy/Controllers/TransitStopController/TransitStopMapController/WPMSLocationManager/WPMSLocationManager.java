package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager.*;

public class WPMSLocationManager extends Activity implements WMSSubscriber, WPSSubscriber{

	private WiFiPositioningSystem mWps;
	private static final int EXIT = 0;
	private static final String NO_DATA = "No Historical Data";
		
	private ArrayAdapter<String> mlistAdapter;  
	private WiFiMappingSystem wms;

	private WiFiPositioningSystem wps;
	    
	private MapStorer mapStorer;
	    
	private ArrayList<String> positionData = new ArrayList<String>();
	
	public WPMSLocationManager()
	{
		positionData.add(NO_DATA);
	}
	 
	public void start()
	{
		// Starts the Wifi Mapping System Subscriber
		startWMSS();
		
		// Starts the Wifi Position System Subscriber
		startWPSS();
	}
	
	public void stop()
	{
		// Stops the Wifi Mapping System Subscriber
		stopWMSS();
		
		// Stops the Wifi Position System Subscriber
		stopWPSS();
	}
	
	private void startWMSS () 
    {
    	// Instantiate a WiFiMapping system if one is not already present
    	if (null == wms)
    	{
    		wms = new WiFiMappingSystem(this);
    		wms.subscribe(this);
    	}
    	
    	// Instantiate a MapStorer if one is not already present
    	if (null == mapStorer)
    	{
    		mapStorer = new MapStorer(this);
    		wms.subscribe(mapStorer);
    	}
    	
    	wms.map();
    }
    
    
    private void stopWMSS () 
    {
    	wms.stopMapping();
    }
    
    
    private void startWPSS () 
    {
    	if (null == wps)
    	{
    		wps = new WiFiPositioningSystem(this);
    		wps.subscribe(this);
    	}

    	wps.startPositioning();
    }
    
    
    private void stopWPSS () 
    {
    	wps.stopPositioning();

    }
    /*TBD Uncomment when classes are available
    public update(List<PositionData> posData){
    	// if posData.NoError() TBD
    	positionDATA.add(posData.toString());
    	((TextView)findViewById(R.id.currentData)).setText(posData.toString());
    	
    	// else position cannot be determined w/ current WAP
    	Context context = getApplicationContext();
	 	int duration = Toast.LENGTH_SHORT;
    	Toast toast;
    	CharSequence toastText = null;
    	toastText = "Cannot determine position based on stored WAP Data.";
    	toast = Toast.makeText(context, toastText, duration);
    	toast.show();
    	
    	positionData tmpPositionData = WPSS.getHistoricalData();
    }
    public update(List<MapData> mapData){
    	positionDATA.add(MapData.toString());
    	((TextView)findViewById(R.id.currentData)).setText(MapData.GPStoString());
    }*/
    
    
    public void update(MapData mapData)
    {
    	if(null != mapData.getScanResults())
    	{
    		List<ScanResult> scanResults = mapData.getScanResults();
    		if (scanResults.isEmpty())
			{
    			// TODO: Magic handle (and should this be setting the other field?
    			// ((TextView)findViewById(R.id.currentData)).setText("No Data Available");
			}
    		else
    		{
    			// Set location value
    			// TODO: Magic handle
    			Location location = mapData.getLocation();
    			if (null != location)
    			{
    				// ((TextView)findViewById(R.id.currentData)).setText(location.getLatitude() + " " + location.getLongitude());
    			}
    			
    			
	    		positionData.clear();
		    	for (ScanResult scanResult: mapData.getScanResults())
		    	{
		    		positionData.add(scanResult.BSSID + " " + scanResult.level);
		    	}
		    	
		    	mlistAdapter.notifyDataSetChanged();
    		}
    	}
    }

    
    // --- WPS Update method
    public void update(WPMSLocation location)
    {
    	
    }
	
	
	
	
	
	
	
}
