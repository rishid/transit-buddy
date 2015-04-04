package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.common.enumerations.RouteType;
import com.common.types.TransitStop;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.transitbuddy.*;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;

public class TransitStopMapController extends MapActivity implements LocationListener
{	
	static double DEGRESS_TO_MICRO_DEGREES = 1E6;
	
	TransitBuddyModel mModel;
	ArrayList<TransitStop> stopList = new ArrayList<TransitStop>();
	
	MapView mapView;
	List<Overlay> mapOverlays;

	MapController mapController;
	StopItemizedOverlay itemizedOverlay;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    
	    TransitBuddyApp app = (TransitBuddyApp)getApplicationContext();
		mModel = app.getTransitBuddyModel();
	     
	    setContentView(R.layout.transit_stop_map_controller_view);
	    
	    mapView = (MapView) findViewById(R.id.mapView);
	    mapView.setBuiltInZoomControls(true);
	    	    
	    mapOverlays = mapView.getOverlays();
	    
	    mapController = mapView.getController();
	    
	    // Acquire a reference to the system Location Manager
		LocationManager locationManager = 
			(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		/* locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
												1000L, 500.0f, this); */

	    // Getting the list of transit stops from the model
	    updateTransitStops();
	    
	    // Display transit stop markers on the map
	    displayTransitStopMarkers();
	}
    
    @Override
    protected boolean isRouteDisplayed() 
    {
    	return false;
    }
    
    // Gets the transit stops for Nearest Stops or Trip Selected
    private void updateTransitStops()
    {
    	String tripID = mModel.getTripID();
    	
    	stopList.clear();
    	
    	if(mModel.getTransitStops(tripID, stopList) != ResponseStatus.Completed)
		{
			//TODO Handle this error somehow; Toast the user?
		}
    }
    
    // Displays the markers for the transit stops
    private void displayTransitStopMarkers()
    {	
    	// Specifies the marker icon
    	Drawable subwayMarker = this.getResources().getDrawable(R.drawable.subway);
    	
    	itemizedOverlay = new StopItemizedOverlay(subwayMarker, mapView.getContext());
		
		// Copley Square Location
		GeoPoint transitStopLocation = new GeoPoint(42349772,-71076578);;
		
		for(TransitStop stop : stopList)
    	{
			// Gets the current stops coordinates and converts 
			// it from degrees to microdegrees
			transitStopLocation = new GeoPoint
			(
				(int) (stop.getCoordinates().getLatitude() 
						* DEGRESS_TO_MICRO_DEGREES),	
					(int) (stop.getCoordinates().getLongitude() 
						* DEGRESS_TO_MICRO_DEGREES)
			);
				
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
			
			item.setMarker( getMarkerForRouteType() );
			
			// Adds the current overlay item to the list
			itemizedOverlay.addOverlay(item);
	    }
	
		// Adds the overlay items to the map
        mapOverlays.add(itemizedOverlay);
	    
        // Sets the zoom level 
	    mapController.setZoom(14); 
	    
        // Moves the maps focus to the last transit stop in the itemizedOverlay
	    mapController.animateTo(transitStopLocation);
    }
    
    // This method is called when use position will get changed
    public void onLocationChanged(Location location) 
    {
    	if (location != null)
    	{
    		double lat = location.getLatitude();
    		double lng = location.getLongitude();
    		
    		GeoPoint currentLocation = new GeoPoint((int) lat * 1000000, (int) lng * 1000000);
    		// mapController.animateTo( currentLocation );
    	}
    }
    
    public void onProviderDisabled(String provider) 
    {
    	
    }
    
    public void onProviderEnabled(String provider) 
    {
    	
    }
    
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    	
    }
    
    // returns a marker for the particular route type selected
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

    
}