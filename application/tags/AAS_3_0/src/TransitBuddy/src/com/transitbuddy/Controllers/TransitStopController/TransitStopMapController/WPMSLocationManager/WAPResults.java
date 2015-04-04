package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WAPResults 
{
	private HashMap<Integer, ArrayList<WPMSLocation>> signalStrengthMap;	
	
	
	
	public WAPResults()
	{
		signalStrengthMap = new HashMap<Integer, ArrayList<WPMSLocation>>();
	}
	
	/**
	 * Instantiate WAPResults
	 */
	public WAPResults(List<WPMSScanResult> scanResults)
	{	// TODO: This is just test code, will need to integrate with SSS
		// initialize signalStrengthMap
		signalStrengthMap = new HashMap<Integer, ArrayList<WPMSLocation>>();
		
		
		// Find minimum and maximum levels
		int minimumLevel = Integer.MAX_VALUE;
		int maximumLevel = Integer.MIN_VALUE;
		WPMSLocation location = null;
		
		for (WPMSScanResult scanResult: scanResults)
		{
			location = scanResult.LOCATION;
			
			if (minimumLevel > scanResult.LEVEL)
			{	// New minimum found
				minimumLevel = scanResult.LEVEL;
			}
			
			if (maximumLevel < scanResult.LEVEL)
			{	// New maximum found
				maximumLevel = scanResult.LEVEL;
			}
		}
		
		// TODO: currently we only add one location for testing
		// 	this will have to be modified to add more than one location
		//	once it is connected to the storage subsystem
		addRecord(minimumLevel, maximumLevel, location);
	}
	
	
	
	public void addRecord(int signalStrength, WPMSLocation location)
	{
		addRecord(signalStrength, signalStrength, location);
	}
	
	
	/**
	 * Augments the signal strength map with a range of signal strength
	 * to location mappings based on the high and low range of signals
	 * passed in
	 * @param lowSignal Low value for signal strength to add location to the map from
	 * @param highSignal High value for signal strength to add location to the map to 
	 * @param location Location the data represents
	 */
	private void addRecord(int lowSignal, int highSignal, WPMSLocation location)
	{
		for (int signalStrength = lowSignal; signalStrength <= highSignal; signalStrength++)
		{	// We're using signalStrengthMap as a MultiMap, this algorithm handles the collisions
			ArrayList<WPMSLocation> currentLocationList = signalStrengthMap.get(signalStrength);
			
			if (null == currentLocationList)
			{	// No collisions in the multimap, create a new list and add it to the map
				signalStrengthMap.put(signalStrength, currentLocationList = new ArrayList<WPMSLocation>());
			}
			
			// Add the new location to the multimap
			currentLocationList.add(location);
		}
	}
	
	
	/**
	 * Return the set of positions that could have the inputed signal strength
	 * @param signalStrength
	 * @return Possible locations for this signal strength, empty list if none
	 */
	public List<WPMSLocation> getPossibleLocations(int signalStrength)
	{
		List<WPMSLocation> possibleLocations;
		
		possibleLocations = signalStrengthMap.get(signalStrength);
		
		if (null == possibleLocations)
		{
			return new ArrayList<WPMSLocation>();
		}
		else
		{
			return possibleLocations;
		}
	}
}
