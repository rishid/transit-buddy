package com.transitbuddy.SystemSettings;

import java.util.ArrayList;

import com.transitbuddy.TransitBuddy.TransitBuddyApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class TransitSettings {
    
    // Default values
    private static final int DEFAULT_ETA_INDEX          = 0;
    private static final int DEFAULT_VICINITY_INDEX     = 0;
    private static final int DEFAULT_FAV_CITY_COUNT     = 0;
    private static final int DEFAULT_FAV_CITY_INDEX     = 0;
    private static final int DEFAULT_MAX_FAV_CITY_COUNT = 5;
    private static final int DEFAULT_USER_ID            = -1;
    
    private static final String DEFAULT_USERNAME = "NO SUCH USER";
    private static final String DEFAULT_PASSWORD = "NO SUCH PASSWORD";
    private static final String DEFAULT_CITY = "NO CITY";
    
    // Strings for keys in preferences file
    private static final String PREFS          = "tbPrefs";
    private static final String ETA            = "eta";
    private static final String ETA_INDEX      = "etaIndex";
    private static final String VICINITY       = "vicinity";
    private static final String VICINITY_INDEX = "vicinityIndex";
    private static final String FAV_CITY_INDEX = "favCityIndex";
    private static final String FAV_CITY_COUNT = "favCityCount";
    private static final String FAV_CITY       = "favCity";
    private static final String SELECTED_CITY  = "selectedCity";
    private static final String USERNAME       = "username";
    private static final String PASSWORD       = "password";
    private static final String USERID         = "userId";
    
    private static final int[] DEFAULT_ETA = {1, 2, 3, 4, 5};
    private static final float[] DEFAULT_VICINITY = {
        0.03125f,
        0.06250f,
        0.12500f,
        0.25000f,
        0.50000f
    };
    
    private static final String[] DEFAULT_VICINITY_STRING = {
        "1/32 Mile",
        "1/16 Mile",
        "1/8 Mile",
        "1/4 Mile",
        "1/2 Mile"
    };

	// The index into the availableTransitSystems array
	// indicating which transit system
	private String mSelectedTransitSystem;
	
	//private int mNumberOfETAs;
	//private int mNumberOfETAsIdx;
	private int[]    mETAsInt;
	private String[] mETAsString;
	private float[]  mVicinitiesFloat;
	private String[] mVicinitiesString;
	
	//private float  mVicinityInMiles;
	//private int    mVicinityIdx;
	//private String mSelectedCity;
	
	// The user's username, used to keep profile information
	//private String mUsername;
	
	// The user's password
	//private String mPassword;
	
	private TransitBuddyApp tbApp = null;
	
	/**
	 * 
	 * 
	 */
	public TransitSettings(TransitBuddyApp app) {
	    tbApp = app;
	    mETAsInt = DEFAULT_ETA;
	    mVicinitiesFloat = DEFAULT_VICINITY;
	    mVicinitiesString = DEFAULT_VICINITY_STRING;
	}
	
	/**
	 * 
	 */
	public void setSelectedCity(String city) {
	    SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
	    Log.v("IN TRANSITSETTINGS, SETTING SELECTED CITY TO: ", city);
        prefs.putString(SELECTED_CITY, city);
        prefs.commit();
	}
	
	/**
	 * 
	 */
	public String getSelectedCity() {
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, SELECTED CITY SENT: ", 
                prefs.getString(SELECTED_CITY, DEFAULT_CITY)
        );
        
        return prefs.getString(SELECTED_CITY, DEFAULT_CITY);
	}
	
	/**
	 * 
	 * @param
	 */
	public void setFavCities(ArrayList<String> favCities) {
        SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
        int count = favCities.size();
        Log.v("IN TRANSITSETTINGS, SETTING FAV CITY TO: ", 
                "USING SIZE: " + Integer.toString(favCities.size()));
        for(int i = 0; i < count; i++) {
            Log.v("IN TRANSITSETTINGS, SETTING FAV CITY TO: ", 
                    FAV_CITY + i + ", " + favCities.get(i));
            prefs.putString(FAV_CITY + i, favCities.get(i));
        }
        
        prefs.commit();
	}
	
	/**
	 * 
	 */
	public ArrayList<String> getFavCities() {
	    ArrayList<String> alist = new ArrayList<String>();
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
	    int count = prefs.getInt(FAV_CITY_COUNT, DEFAULT_FAV_CITY_COUNT);
	    Log.v("IN TRANSITSETTINGS, FAV CITY SENT: ", 
                "FAV CITY USING SIZE: " + 
                Integer.toString(prefs.getInt(FAV_CITY_COUNT, 
                        DEFAULT_FAV_CITY_COUNT)));
        for(int i = 0; i < count; i++) {
            Log.v("IN TRANSITSETTINGS, FAV CITY SENT: ", 
                    prefs.getString(FAV_CITY + i, DEFAULT_CITY));
            alist.add(prefs.getString(FAV_CITY + i, DEFAULT_CITY));
        }
	    return alist;
	}
	
	/**
	 * 
	 */
	public void setFavCityIndex(int index) {
	    SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
        prefs.putInt(FAV_CITY_INDEX, index);
        Log.v("IN TRANSITSETTINGS, SETTING FAV CITY INDEX TO: ", 
                Integer.toString(index));
        prefs.commit();
	}
	
	/**
	 * 
	 */
	public int getFavCityIndex() {
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, FAV CITY INDEX SENT: ", 
                Integer.toString(prefs.getInt(FAV_CITY_INDEX, 
                        DEFAULT_FAV_CITY_INDEX)
                )
        );
        
        return prefs.getInt(FAV_CITY_INDEX, DEFAULT_FAV_CITY_INDEX);
	}
	
	/**
	 * 
	 */
	public void setCurrentFavCityCount(int count) {
	    SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
	    Log.v("IN TRANSITSETTINGS, SETTING FAV CITY COUNT TO: ", 
                Integer.toString(count));
        prefs.putInt(FAV_CITY_COUNT, count);
        prefs.commit();
	}
	
	/**
	 * 
	 */
	public int getCurrentFavCityCount() {
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, FAV CITY COUNT SENT: ", 
                Integer.toString(prefs.getInt(FAV_CITY_COUNT, 
                        DEFAULT_FAV_CITY_COUNT)
                )
        );
        
        return prefs.getInt(FAV_CITY_COUNT, DEFAULT_FAV_CITY_COUNT);
	}
	
	/**
	 * 
	 */
	public int getMaxFavCityCount() {
	    return DEFAULT_MAX_FAV_CITY_COUNT;
	}
	
	/**
	 * 
	 * @param numETAs
	 */
	public void setNumberOfETAs(int numETAs)
	{
		//mNumberOfETAs = numETAs;
		SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
		        Context.MODE_PRIVATE).edit();
		Log.v("IN TRANSITSETTINGS, SETTING ETAs TO: ", 
                Integer.toString(numETAs));
		prefs.putInt(ETA, numETAs);
		prefs.commit();
	}
	
	/**
	 * 
	 * @return Returns the number of ETAs to display
	 */
	public int getNumberOfETAs() {
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
	            Context.MODE_PRIVATE);
	    Log.v("IN TRANSITSETTINGS, ETAs SENT: ", 
                Integer.toString(prefs.getInt(ETA, 
                        DEFAULT_ETA[DEFAULT_ETA_INDEX])
                )
        );
	    
	    return prefs.getInt(ETA, DEFAULT_ETA[DEFAULT_ETA_INDEX]);
	    //return mNumberOfETAs;
	}
	
	/**
	 * 
	 * @param etasIdx
	 */
	public void setNumberOfETAsIdx(int index) {
        //mNumberOfETAsIdx = index;
	    SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
	    Log.v("IN TRANSITSETTINGS, SETTING ETA INDEX TO: ", 
                Integer.toString(index));
        prefs.putInt(ETA_INDEX, index);
        prefs.commit();
    }
	
	public int getNumberOfETAsIdx() {
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, ETA INDEX SENT: ", 
                Integer.toString(prefs.getInt(ETA_INDEX, 
                        DEFAULT_ETA_INDEX)
                )
        );
        
        return prefs.getInt(ETA_INDEX, DEFAULT_ETA_INDEX);
	    //return mNumberOfETAsIdx;
	}
	
	public int[] getETAIntArray() {
	    return mETAsInt;
	}
	
	public String[] getETAStringArray() {
	    
	    mETAsString = new String[mETAsInt.length];
	    for(int i = 0; i < mETAsInt.length; i++) {
	        mETAsString[i] = Integer.toString(mETAsInt[i]);
	    }
	    
	    return mETAsString;
	}
	
	/**
     * 
     * @param miles
     */
    public void setVicinity(float miles)
    {
        //mVicinityInMiles = miles;
        SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
        Log.v("IN TRANSITSETTINGS, SETTING VICINITY TO: ", 
                Float.toString(miles));
        prefs.putFloat(VICINITY, miles);
        prefs.commit();
    }
    
    /**
     * 
     * @return Returns the vicinity in miles
     */
    public float getVicinity()
    {
        SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, VICINITY SENT: ", 
                Float.toString(prefs.getFloat(VICINITY, 
                        DEFAULT_VICINITY[DEFAULT_VICINITY_INDEX])
                )
        );
        
        return prefs.getFloat(VICINITY, 
                DEFAULT_VICINITY[DEFAULT_VICINITY_INDEX]);
        //return mVicinityInMiles;
    }
    
    /**
     * 
     * @param idx
     */
    public void setVicinityIdx(int index) {
        //mVicinityIdx = idx;
        SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
        Log.v("IN TRANSITSETTINGS, SETTING VICINITY INDEX TO: ", 
                Integer.toString(index));
        prefs.putInt(VICINITY_INDEX, index);
        prefs.commit();
    }
    
    /**
     * 
     * @return
     */
    public int getVicinityIdx() {
        SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, VICINITY INDEX SENT: ", 
                Integer.toString(prefs.getInt(VICINITY_INDEX, 
                        DEFAULT_VICINITY_INDEX)
                )
        );
        
        return prefs.getInt(VICINITY_INDEX, DEFAULT_VICINITY_INDEX);
        //return mVicinityIdx;
    }
	
	public float[] getVicinityFloatArray() {
	    return mVicinitiesFloat;
	}
	
	public String[] getVicinityStringArray() {
	    return mVicinitiesString;
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
	 * @param username
	 */
	public void setUserName(String username)
	{
	    SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
        Log.v("IN TRANSITSETTINGS, SETTING USERNAME TO: ", username);
        prefs.putString(USERNAME, username);
        prefs.commit();
		//mUsername = username;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUserName()
	{
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, USERNAME SENT: ", 
                prefs.getString(USERNAME, DEFAULT_USERNAME));
        
        return prefs.getString(USERNAME, DEFAULT_USERNAME);
		//return mUsername;
	}
	
	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password)
	{
	    SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
        Log.v("IN TRANSITSETTINGS, SETTING PASSWORD TO: ", password);
        prefs.putString(PASSWORD, password);
        prefs.commit();
		//mPassword = password;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPassword()
	{
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, PASSWORD SENT: ", 
                prefs.getString(PASSWORD, DEFAULT_PASSWORD));
        
        return prefs.getString(PASSWORD, DEFAULT_PASSWORD);
		//return mPassword;
	}
	
	public void setUserId(int userId) {
	    SharedPreferences.Editor prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE).edit();
        Log.v("IN TRANSITSETTINGS, SETTING USER ID TO: ", 
                Integer.toString(userId));
        prefs.putInt(USERID, userId);
        prefs.commit();
	}
	
	public int getUserId() {
	    SharedPreferences prefs = tbApp.getSharedPreferences(PREFS, 
                Context.MODE_PRIVATE);
        Log.v("IN TRANSITSETTINGS, USER ID SENT: ", 
                Integer.toString(prefs.getInt(USERID, DEFAULT_USER_ID)));
        
        return prefs.getInt(USERID, DEFAULT_USER_ID);
	}
	
	public String getPrefFile() {
	    return PREFS;
	}
}
