package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.util.Log;

public class MapStorer extends Storer implements WMSSubscriber
{
	private static double GRID_SIZE = 0.0001;
	
	
	public MapStorer(Context context)
	{
		super(context);
	}
	
	public void storeMap(List<MapData> mapData)
	{
	}
	
	
	/**
	 * Fetch map data from the database
	 * @return Map of BSSID to associated WAPResults
	 */
	public Map<String, WAPResults> fetchMap()
	{		
		Map<String, WAPResults> wifiMap = new HashMap<String, WAPResults>();
		
		// Retrieve map from database
		final String selectAllSQL =
			"SELECT bssid, signal_strength, latitude, longitude " +
			"FROM " + MAP_RESULTS_VIEW + ";";
		
		Cursor wifiMapCursor = fetch(selectAllSQL);
		
		if (wifiMapCursor.moveToFirst())
		{	// Results found, start loading them into the map		
			do
			{
				// TODO: Demagic the numbers
				String bssid = wifiMapCursor.getString(wifiMapCursor.getColumnIndexOrThrow("bssid"));
				int signalStrength = wifiMapCursor.getInt(wifiMapCursor.getColumnIndexOrThrow("signal_strength"));
				int latitude = wifiMapCursor.getInt(wifiMapCursor.getColumnIndexOrThrow("latitude"));
				int longitude = wifiMapCursor.getInt(wifiMapCursor.getColumnIndexOrThrow("longitude"));
				
				WPMSLocation location = new WPMSLocation(latitude, longitude);
				
				WAPResults results = wifiMap.get(bssid);
				
				// Check to see if there's already an entry for this WAP ID
				if (null == results)
				{
					results = new WAPResults();
					wifiMap.put(bssid, results);
				}
				
				// Add this result to the WAPResult
				results.addRecord(signalStrength, location);
				
				Log.d("Test", "Adding record to map: " + bssid + "/" + signalStrength + "/" + latitude + "/" + longitude);
				
			} while (wifiMapCursor.moveToNext());
		}
		
		wifiMapCursor.close();
		
		return wifiMap;
	}

	
	/**
	 * Store data retrieved from subscription to a WMSPublisher
	 */
	@Override
	public void update(MapData mapData) 
	{
		List<ScanResult> scanResults = mapData.getScanResults();
		Location location = mapData.getLocation();

		// Only insert rows if we have both location and scanResults
		if (null != location)
		{
			// If location isn't already in the database add it
			int locationID = insertLocation(location);
			
			for (ScanResult scanResult : scanResults)
			{	
				// If wap isn't already in the database add it
				int wapID = insertWAP(scanResult.BSSID);
				
				// Store all signal measurements
				insertSignalMeasurement(locationID, wapID, scanResult.level);
				
			}
		}
	}


	
	private void insertSignalMeasurement(int locationID, int wapID, int signalStrength)
	{
		Log.d("Test", "Inserting Signal Measurement");
		
		String insertSignalMeasurementSQL =
			"INSERT INTO " + SIGNAL_MEASUREMENT_TABLE +
			"(   signal_strength, " +
			"    wireless_access_point_id, " +
			"    wifi_grid_map_id " +
			")   VALUES " +
			"(" + signalStrength + ", " +
			    + wapID + ", " +
			    + locationID +
			");";
		
		store(insertSignalMeasurementSQL);
		Log.d("Test", "Finished Inserting Signal Measurement");
	}
	
	
	
	/**
	 * If the WAP exists in the database, return its ID
	 * Otherwise enter it into the database and then return the ID
	 * @param bssid Unique WAP identifier
	 * @return ID of database entry for this WAP
	 */
	private int insertWAP(String bssid)
	{
		Log.d("Test", "Inserting WAP");
		
		// See if this BSSID has already been entered in the database
		String wapSelectSQL =
			"SELECT _id " +
			"FROM " + WIRELESS_ACCESS_POINT_TABLE + " " +
			"WHERE bssid = '" + bssid + "';";
		
		Cursor wapExistsCursor = fetch(wapSelectSQL);
		
		if (0 == wapExistsCursor.getCount())
		{	// No results, WAP doesn't exist in DB
			// Add WAP
			String wapInsertSQL =
				"INSERT INTO " + WIRELESS_ACCESS_POINT_TABLE +
				"(bssid) VALUES " +
				"('" + bssid + "');";
			
			store(wapInsertSQL);
			
			wapExistsCursor = fetch(wapSelectSQL);
		}
		
		wapExistsCursor.moveToFirst();
		
		int id = wapExistsCursor.getInt(0);
		wapExistsCursor.close();
		
		Log.d("Test", "WAP ID = " + id);
		Log.d("Test", "Finished Inserting WAP");
		return id;
	}
	
	
	/**
	 * If the Location exists in the database, return its ID
	 * Otherwise enter it into the database and then return the ID
	 * @param location GPS Location
	 * @return ID of database entry for this location
	 */	
	private int insertLocation(Location location)
	{
		Log.d("Test", "Inserting Location");
		
		// TODO: Refactor to a common location?
		long nw_corner_latitude = Math.round(location.getLatitude() / GRID_SIZE);
		long nw_corner_longitude = Math.round(location.getLongitude() / GRID_SIZE);
		
		// Since our nw_corner units are now in GRID_SIZE, se_corner is 1 unit away
		long se_corner_latitude = nw_corner_latitude + 1;
		long se_corner_longitude = nw_corner_longitude + 1;
		
		
		// See if this location has already been entered into the database
		String locationSelectSQL = 
			"SELECT _id " +
			"FROM " + WIFI_GRID_MAP_TABLE + " " + 
			"WHERE nw_corner_latitude = " + nw_corner_latitude +
			"  AND nw_corner_longitude = " + nw_corner_longitude + ";";
			
		Cursor locationExistsCursor = fetch(locationSelectSQL);
		
		if (0 == locationExistsCursor.getCount())
		{	// No results, position doesn't exist
			// Add Position
			Log.d("Test", "Location not found, adding to database");
			
			String locationInsertSQL =
				"INSERT INTO " + WIFI_GRID_MAP_TABLE + " " +
				"( nw_corner_latitude," +
				"  nw_corner_longitude," +
				"  se_corner_latitude," +
				"  se_corner_longitude" +
				") VALUES " +
				"( " + nw_corner_latitude + ", "
				     + nw_corner_longitude + ", "
				     + se_corner_latitude + ", "
				     + se_corner_longitude +
			    ")";
			
			Log.d("Test", locationInsertSQL);
			
			store(locationInsertSQL);
			
			locationExistsCursor.close();
			
			// Re-execute location check query to get the id
			locationExistsCursor = fetch(locationInsertSQL);
		}
		
		locationExistsCursor.moveToFirst();
		
		int id = locationExistsCursor.getInt(0);
		locationExistsCursor.close();
		
		Log.d("Test", "Done Inserting Location");
		return id;
	}	
}
