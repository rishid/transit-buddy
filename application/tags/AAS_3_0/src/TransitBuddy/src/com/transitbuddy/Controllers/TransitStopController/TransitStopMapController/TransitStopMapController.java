package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.common.commands.GetStops;
import com.common.enumerations.RouteType;
import com.common.types.Coordinate;
import com.common.types.TransitStop;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.transitbuddy.*;
import com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager.WPMSLocationManager;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.SystemSettings.TransitSettings;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;
import com.transitbuddy.enumerations.MapType;

public class TransitStopMapController extends MapActivity 
{	
	static double DEGRESS_TO_MICRO_DEGREES = 1E6;
	static double MILES_TO_METERS          = 1609.344;
	static double H_PADDING = 0.1;
	static double V_PADDING = 0.2;
	static int ACQUISITION_TIMEOUT  = 60000;
	static int COUNTDOWN_TIMER_TICK = 1000;
	static int REACQUSITION_INTERVAL = 30000;
	
	// Maximum number of transit stops to display on Find Nearest Stops
	static int MAX_TRANSIT_STOPS = 25;
	
	TransitBuddyModel mModel = null;
	TransitSettings   mSettings = null;
	ArrayList<TransitStop> stopList = new ArrayList<TransitStop>();
	
	MapView mMapView = null;
	MapController mMapController = null;
	LocationManager mLocationManager = null;
	
	List<Overlay> mMapOverlays = null;
	StopItemizedOverlay mItemizedOverlay = null;
	MyLocationOverlay mUserLocationOverlay = null;
	
	ProgressDialog mAcquistionProgressDialog = null;
	ProgressDialog mDownloadProgressDialog = null;
	
	DownloadStopsTask mDownloadTask = null;
	
	Timer mTimer = new Timer();
	CountDownTimer acquistionCountDownTimer = null;
	WPMSLocationManager wpmsLocationManager = null;
	
	Handler mDownloadHandler = null;
	Handler mWPMSHandler     = null;
	boolean positionAcquired = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.transit_stop_map_controller_view);
	    
	    mMapView = (MapView) findViewById(R.id.mapView); 
	    mMapView.setBuiltInZoomControls(true);
	     
	    mMapOverlays = mMapView.getOverlays();
	    
	    TransitBuddyApp app = (TransitBuddyApp) getApplicationContext();
		mModel = app.getTransitBuddyModel();
	    mSettings = app.getTransitBuddySettings();
	            
	    mMapController = mMapView.getController();
	    
	    // This must be called before the setupLocationManager and checkLocationProvider
	    // Since they publish messages
	    initHandlers();
	    
	    // Sets up and starts the location manager
	    setupLocationManager();
	       
	    // Ensures that our application has location permissions enabled
	    checkLocationProvider();
	    
	    // Need to do some task sycnhronization handling here 
	    // before we start this
	    // startService();
	}
	
	
	private void initHandlers()
	{
		mDownloadHandler = new Handler() 
	    {
	    	@Override
	    	public void handleMessage(Message msg)
	    	{
	    		 ShowProgressAndDownloadData();
	    	}
	    };
	    
	    mWPMSHandler = new Handler() 
	    {
	    	@Override
	    	public void handleMessage(Message msg)
	    	{
	    		// Starts the WPMS
	    		// wpmsLocationManager.start();
	    	}
	    };
	}
	
	/*
	 * Prompts the user to see if they want to enable a location provider
	 */
	private void promptEnableLocationProvider()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Location Provider not enabled. Would you like to enable one?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		       {
		           public void onClick(DialogInterface dialog, int id) 
		           {
		        	   // bring up the Android Location Settings 
		        		Intent intent = new Intent(
		    				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		        		
		        		startActivity(intent);	
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() 
		       {
		           public void onClick(DialogInterface dialog, int id) 
		           {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

	/*
	 * Checks to see if a location provider is enabled
	 */
	private void checkLocationProvider()
	{
		if ( (!mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )) &&
    			(!mLocationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )))
    	{
			promptEnableLocationProvider();
    	}
		else
		{
			startAcquiring();
		}
	}
	
	/*
	 * Tells the user we have begun to acquire a location
	 */
	private void startAcquiring()
	{
		mAcquistionProgressDialog = ProgressDialog.show(this, 
				  "TransitBuddy" , 
				  "Acquiring Location. Please wait ...", 
				  true,
				  true,
				  new DialogInterface.OnCancelListener() 
				  {
					@Override
					public void onCancel(DialogInterface dialog) 
					{
						if(acquistionCountDownTimer != null)
						{
							acquistionCountDownTimer.cancel();
							acquistionCountDownTimer = null;
						}
					}
				});
		
		// Starts the count down timer
		acquistionCountDownTimer = new CountDownTimer(ACQUISITION_TIMEOUT, COUNTDOWN_TIMER_TICK) 
		{

		     public void onTick(long millisUntilFinished) 
		     {
		    	 
		     }

		     public void onFinish() 
		     {
		    	 // Remove the Acquistion Progress Dialog
		    	if ( mAcquistionProgressDialog != null )
 			 	{
		    		mAcquistionProgressDialog.dismiss();
 			 	}
		        
		    	 Toast.makeText(getApplicationContext(), "Location Acquistion timed out. Attemtting to use WPMS.",
 						Toast.LENGTH_SHORT).show();
		     }
		  }.start();

	}
	
	// service business logic
	private void startService() 
	{
	    TimerTask timerTask = new TimerTask(){
	        public void run() {
	        	 mDownloadHandler.sendMessage(new Message());
	        }
	    };
	   
	    mTimer.scheduleAtFixedRate(timerTask, 0, REACQUSITION_INTERVAL);
	}
		
	/**
	 * Configures and starts the location manager
	 */
	private void setupLocationManager()
	{ 
		//  Acquire a reference to the system Location Manager
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Starts the location manager
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
		// Adds the user's position to the map
	    initUserLocation();   
	}
	
	 /**
     * Initializes the user's position
     */
    private void initUserLocation() 
    {  	
    	mUserLocationOverlay = new MyLocationOverlay(this, mMapView);
    	mUserLocationOverlay.enableMyLocation();
    	mMapView.getOverlays().add(mUserLocationOverlay);
    
    	// Waits till we have a first fix to do anything
    	mUserLocationOverlay.runOnFirstFix(new Runnable()
    	{
    		 @Override
    		   public void run() 
    		   { 	
    			 	if ( acquistionCountDownTimer != null )
    			 	{
    			 		// Stops the acquistion countdown timer 
    			 		acquistionCountDownTimer.cancel();
    			 	}
    			 	
    			    if ( mAcquistionProgressDialog != null )
    			    {
    			    	mAcquistionProgressDialog.dismiss();
    			    	mAcquistionProgressDialog = null;
    			    }
    			 	
    			 	if ( positionAcquired == false )
    			 	{
    			 		// Sends a message to download the stops
    			 		mDownloadHandler.sendMessage(new Message());
    			 	}
    			 	
    			 	positionAcquired = true;
    		   }
    	}); 
	}
	
    /**
     * Downloads transit stop data from the transit information server
     */
	private void ShowProgressAndDownloadData()
	{
		mDownloadProgressDialog = ProgressDialog.show(this, 
				  							  "TransitBuddy" , 
				  							  "Loading. Please wait ...", 
				  							  true,
				  							  true,
				  							  new DialogInterface.OnCancelListener() 
											  {
												@Override
												public void onCancel(DialogInterface dialog) 
												{
													if(mDownloadTask != null)
													{
														mDownloadTask.cancel(true);
														mDownloadTask = null;
													}
												}
											});

		mDownloadTask = new DownloadStopsTask();
		mDownloadTask.execute();
	}

    @Override
    protected boolean isRouteDisplayed() 
    {
    	return false;
    }
    
    @Override
    public void onPause()
    {      	
    	if ( mUserLocationOverlay != null )
    	{
    		mUserLocationOverlay.disableMyLocation();
    	}
    	
    	// disables location listening while app is sleeping
    	if ( mLocationManager != null )
    	{
    		mLocationManager.removeUpdates(locationListener);
    	}
    	
    	super.onPause();
    }
    
    @Override
    public void onResume()
    {   		
    	initHandlers();
    	
    	// starts the location listener on app resume 
    	// This is causing the appp to crash
    	setupLocationManager();
    	
    	super.onResume();
    }
     
    private class DownloadStopsTask extends AsyncTask<Void, Void, StopsResult>
    {
		@Override
		protected StopsResult doInBackground(Void... arg0) 
		{
			String errorMessage = "";
			ResponseStatus status = null;
			
			// Clears the list of transit stops
		    stopList.clear();
		    	 	   	  	
		    // Need to decide between Get Nearest Stops and Get Trip Stops    	
		    if ( mModel.getMapID() == MapType.TransitTrip )
		    {
		        // Map handler for displaying transit trips     		
		    	String tripID = mModel.getTripID();
		    	
		    	try
		    	{
		    		// Gets the real time static schedule information if available    		
		    		status = mModel.getTransitStops(tripID, stopList, GetStops.ScheduleType.NEXT_FIVE);
		    	}
		    	catch(IOException ioe)
				{
		    		Toast.makeText(getApplicationContext(), ioe.getMessage(), Toast.LENGTH_LONG);
				}
		    	
				// Get the list of routes.
				if(status != ResponseStatus.Completed)
				{
					errorMessage = "Error downloading the transit trip stops list!";
				}
		    }
		    else
		    {
		        // Map handler for displaying nearby stops		    			    	
		    	double lat = mUserLocationOverlay.getMyLocation().getLatitudeE6();
			    double lon = mUserLocationOverlay.getMyLocation().getLongitudeE6();
			    	
		    	Coordinate currentLocation = new Coordinate((float) (lat / 1E6), (float) (lon / 1E6));
		    	
		    	// Converts the setting's vicinity in miles to meters 
		    	int vicnity = (int) 10000; 
		    	// int vicnity = (int) (mSettings.getVicinity() * MILES_TO_METERS);
		    	
		    	try
		    	{
		    		// Gets MAX_TRANSIT_STOPS nearby stops around currentLocation
		    		status = mModel.getNearbyStops(vicnity, currentLocation, MAX_TRANSIT_STOPS, stopList);
		    	}
		    	catch(IOException ioe)
				{
		    		Toast.makeText(getApplicationContext(), ioe.getMessage(), Toast.LENGTH_LONG);
				}
		    	
		    	// Get the list of transit stops
				if(status != ResponseStatus.Completed)
				{
					errorMessage = "Error downloading the nearby transit stops list!";;
					Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
				}
		    }
			
			return new StopsResult(status, errorMessage);
		}
		
		protected void onPostExecute(StopsResult result) 
		{
			if(result.getResponseStatus() == ResponseStatus.Completed)
			{
				displayTransitStopMarkers();
				
				if ( mDownloadProgressDialog != null )
				{
					mDownloadProgressDialog.dismiss();
					mDownloadProgressDialog = null;
				}
			}
			else
			{
				String dialogStr = "";
				
				switch(result.getResponseStatus())
				{
				case TimedOut:
					dialogStr = "Connection timed out!\n" + result.getErrorMessage();
				case Failed:
					dialogStr = "Connection failed!\n" + result.getErrorMessage();
					break;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(TransitStopMapController.this);

				builder.setMessage(dialogStr)
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
		}	
    } 
       
    /**
     * Displays the markers for the transit stops
     */
    private void displayTransitStopMarkers()
    {	
    	// Specifies the marker icon
    	Drawable subwayMarker = this.getResources().getDrawable(R.drawable.subway);
    	
    	mItemizedOverlay = new StopItemizedOverlay(subwayMarker, mMapView);
			
		for(TransitStop stop : stopList)
    	{
			// Gets the current stops coordinates and converts 
			// it from degrees to microdegrees
			
			int lat = (int) (stop.getCoordinates().getLatitude() * DEGRESS_TO_MICRO_DEGREES);
			int lon = (int) (stop.getCoordinates().getLongitude() * DEGRESS_TO_MICRO_DEGREES);
					
			GeoPoint transitStopLocation = new GeoPoint(lat, lon);
				
			// Gets the stop times for the current transit stop
			ArrayList<Date> stopTimes = stop.getStopTimes();
			
			StringBuilder timeStr = new StringBuilder();
    		
    		// Creates a string of all the stop times for the selected transit stop
    		for(Date time : stopTimes)
    		{
    			timeStr.append(String.format("%02d:%02d:%02d, ", time.getHours(),
    															 time.getMinutes(),
    															 time.getSeconds()));
    		}
    		
    		// Creates an overlay item for the current transit stop
			OverlayItem item = new OverlayItem(transitStopLocation, 
					stop.getName(), timeStr.toString() );
			
			// Gets the marker image for a particular route type
			item.setMarker( getMarkerForRouteType() );
			
			// Adds the current overlay item to the list
			mItemizedOverlay.addOverlay(item);	
	    }
	
		// If there are no stops in the mItemizedOverlay 
		// this will cause app to crash
		if ( mItemizedOverlay.size() > 0 )
		{
			// Adds the overlay items to the map
			mMapOverlays.add(mItemizedOverlay);
		}
        	
        // adjusts the zoom level and map center based on current item's
        centerOverlays();
    }
    
    /**
     * Adapts the zoom and animation based on the current max/min latitude and longtitude
     */ 
    private void centerOverlays()
    {
        int minLat = Integer.MAX_VALUE;
        int maxLat = Integer.MIN_VALUE;
        int minLon = Integer.MAX_VALUE;
        int maxLon = Integer.MIN_VALUE;
        
        for (int i = 0; i < mItemizedOverlay.size(); i++) {
        	
            OverlayItem s = mItemizedOverlay.getItem(i);
            
            minLat = (int) ((minLat > (s.getPoint().getLatitudeE6())) ? s.getPoint().getLatitudeE6()   : minLat);
            maxLat = (int) ((maxLat < (s.getPoint().getLatitudeE6())) ? s.getPoint().getLatitudeE6()   : maxLat);
            minLon = (int) ((minLon > (s.getPoint().getLongitudeE6())) ? s.getPoint().getLongitudeE6() : minLon);
            maxLon = (int) ((maxLon < (s.getPoint().getLongitudeE6())) ? s.getPoint().getLongitudeE6() : maxLon);
        }

        // Gets the user's position
        GeoPoint gp = mUserLocationOverlay.getMyLocation();

        minLat = (minLat > gp.getLatitudeE6()) ? gp.getLatitudeE6() : minLat;
        maxLat = (maxLat < gp.getLatitudeE6()) ? gp.getLatitudeE6() : maxLat;
        minLon = (minLon > gp.getLongitudeE6()) ? gp.getLongitudeE6() : minLon;
        maxLon = (maxLon < gp.getLongitudeE6()) ? gp.getLongitudeE6() : maxLon;

        // Leave some padding from corners
        maxLat = maxLat + (int)((minLat - minLat) * H_PADDING);
        minLat = minLat - (int)((minLat - minLat) * H_PADDING);

        maxLon = maxLon + (int)((maxLon - minLon) * V_PADDING);
        minLon = minLon - (int)((maxLon - minLon) * V_PADDING);
        
        mMapController.zoomToSpan((maxLat - minLat), (maxLon - minLon));
        mMapController.animateTo(new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2));
    }
    
    /**
     *  Returns a marker for the particular route type selected
     */
    private Drawable getMarkerForRouteType()
    {
    	// Sets the default
    	Drawable marker = this.getResources().getDrawable(R.drawable.subway);
    	
    	RouteType routeType = mModel.getRouteType();
    	
    	// Determines the marker icon to use based on the RouteType
    	if ( routeType == RouteType.Tram )
    	{
    		marker = this.getResources().getDrawable(R.drawable.tram);
    	}
    	else if ( routeType == RouteType.Subway )
    	{
    		marker = this.getResources().getDrawable(R.drawable.subway);
    	} 
    	else if ( routeType == RouteType.Rail )
    	{
    		marker = this.getResources().getDrawable(R.drawable.rail);
    	}
    	else if ( routeType == RouteType.Bus )
    	{
    		marker = this.getResources().getDrawable(R.drawable.bus);
    	}
    	else if ( routeType == RouteType.Ferry )
    	{
    		marker = this.getResources().getDrawable(R.drawable.ferry);
    	}
    	else if ( routeType == RouteType.CableCar )
    	{
    		marker = this.getResources().getDrawable(R.drawable.cablecar);
    	}
    	else if ( routeType == RouteType.Gondola )
    	{
    		marker = this.getResources().getDrawable(R.drawable.gondola);
    	}
    	else if ( routeType == RouteType.Funicular )
    	{
    		marker = this.getResources().getDrawable(R.drawable.funicular);
    	}
    	
    	// Sets the marker bounds appropriately
    	// this is the equivalent of boundCenterBottom() 
    	// this has to be done in order for the icon to show up
		marker.setBounds(marker.getIntrinsicWidth() / -2,
						-marker.getIntrinsicHeight(),
						 marker.getIntrinsicWidth() / 2, 
						 0);
		
		return marker;
    }
    
    /**
     * Listens for location updates
     */
    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
        	double lat, lon;
        	
        	if (location != null) 
            { 
        		lat = location.getLatitude();
        		lon = location.getLongitude();
        		Log.d("LOCATION CHANGED", lat + "");
        		Log.d("LOCATION CHANGED", lon + "");
            
        		/* Toast.makeText(getApplicationContext(),
        				lat + "  " + lon,
        				Toast.LENGTH_LONG).show();    */  
        	}
        }
             
        @Override
    	public void onProviderDisabled(String provider) 
        {
    		
        }

    	@Override
    	public void onProviderEnabled(String provider) 
    	{
    	
    	}
        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) 
        {
        	/* This is called when the GPS status alters */
    		switch (status) 
    		{
    			case LocationProvider.OUT_OF_SERVICE:
    			
    				Toast.makeText(getApplicationContext(), "Status Changed: Out of Service",
    						Toast.LENGTH_SHORT).show();
    				break;
    				
    			case LocationProvider.TEMPORARILY_UNAVAILABLE:
    		
    				Toast.makeText(getApplicationContext(), "Status Changed: Temporarily Unavailable",
    						Toast.LENGTH_SHORT).show();
    				break;
    			
    			case LocationProvider.AVAILABLE:
    			
    				Toast.makeText(getApplicationContext(), "Status Changed: Available",
    						Toast.LENGTH_SHORT).show();
    				break;
    		}
    	}
    };
   
    private class StopsResult
    {
    	ResponseStatus mStatus;
    	String mErrorMessage;
    	
    	public StopsResult(ResponseStatus status, String errorMessage)
    	{
    		mStatus = status;
    		mErrorMessage = errorMessage;
    	}
    	
    	public String getErrorMessage()
    	{
    		return mErrorMessage;
    	}
    	
    	public ResponseStatus getResponseStatus()
    	{
    		return mStatus;
    	}
    }
}