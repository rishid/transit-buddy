package com.transitbuddy.SystemSettings;


public class TransitSettings {

	// The index into the availableTransitSystems array
	// indicating which transit system
	private String mSelectedTransitSystem;
	
	// The number of times to display to user 
	// Min - 1
	// Max - 5
	private int mNumberOfETAs;
	
	// The square mileages to show around the user's location
	// Min - 1/8 mile
	// Max - 5 Miles
	private float mVicinityInMiles;
	
	// The user's username, used to keep profile information
	private String mUsername;
	
	// The user's password
	private String mPassword;
	
	/**
	 * 
	 * @param numETAs
	 */
	public void setNumberOfETAs(int numETAs)
	{
		mNumberOfETAs = numETAs;
	}
	
	public void setSelectedTransitSystem(String system)
	{
		mSelectedTransitSystem = system;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSelectedTransitSystem()
	{
		return mSelectedTransitSystem;
	}
	
	/**
	 * 
	 * @param miles
	 */
	public void setVicinity(float miles)
	{
		mVicinityInMiles = miles;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getVicinity()
	{
		return mVicinityInMiles;
	}
	
	/**
	 * 
	 * @param username
	 */
	public void setUserName(String username)
	{
		mUsername = username;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUserName()
	{
		return mUsername;
	}
	
	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password)
	{
		mPassword = password;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPassword()
	{
		return mPassword;
	}
	
	
}
