package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

public class WPMSLocation 
{
	private final double latitude;
	private final double longitude;
	
	
	public WPMSLocation(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	
	@Override
	public boolean equals(Object object)
	{
       if (object == this)
       {
           return true;
       }
       
       if (object == null)
       {
           return false;
       }
       
       if (getClass() != object.getClass())
       {
           return false;
       }
       
       // Object is of the WPMSLocation type
       WPMSLocation other = (WPMSLocation)object;
       
       if (this.getLatitude() != other.getLatitude())
       {
           return false;
       }
       
       if (this.getLongitude() != other.getLongitude())
       {
    	   return false;
       }
       
       return true;
	}
}
