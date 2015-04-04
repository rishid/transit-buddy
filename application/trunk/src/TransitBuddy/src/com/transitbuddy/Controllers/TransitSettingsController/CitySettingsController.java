package com.transitbuddy.Controllers.TransitSettingsController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.transitbuddy.R;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.SystemSettings.TransitSettings;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;

public class CitySettingsController 
        extends Activity implements OnItemSelectedListener {
    
    private static final String address = 
        "http://129.10.128.235/~cse/CommonFunctionality/";
    private static final String action        = "action";
    private static final String setUserData   = "setUserData";
    private static final String userId        = "userId";
    private static final String dataKey       = "dataKey";
    private static final String dataValue     = "dataValue";
    private static final String appTokenName  = "blue9";
    private static final String appToken      = "appToken";
    private static final String favCity       = "favCity";
    private static final String favCityCount  = "favCityCount";
    
	static Spinner favCitySpinner = null;
	static TextView tv            = null;
	static Button addCityBtn      = null;
	static Button saveCitiesBtn   = null;
	static Button deleteCityBtn   = null;
	
	AlertDialog.Builder addCityBuilder = null;
	AlertDialog.Builder delCityBuilder = null;
	
	private int maxFavCityCount;
	private int currentFavCityCount;

	private String[] allCityArray = {};
	private String[] delCityArray = {};
	
	//String[] favoriteCities = new String[FAV_CITY_MAX_CNT];
	
	ArrayList<String> alFavCities = new ArrayList<String>();
	ArrayList<String> alAllCities = new ArrayList<String>();

	String selectedCity;
	int currentFavIndex = 0;
	
	TransitBuddyApp app = null;
	TransitSettings settings = null;
	TransitBuddyModel model = null;
	static final String PREFS = "prefs";
	
	ArrayAdapter<String> adapter = null;
	
	public void onCreate(Bundle savedInstanceState) {
		Log.v("********** BEGIN ONCREATE() **********", "BEGIN");

	    super.onCreate(savedInstanceState);
	    //setContentView(R.layout.tab_city_settings);
	    app = (TransitBuddyApp)getApplicationContext();
        settings = app.getTransitBuddySettings();
        model = app.getTransitBuddyModel();
	    // Populates the list of all cities
        recallAllCities();
        createAddFavoriteCityDialog();
	    makeTab();
	    
	    Log.v("********** END ONCREATE() **********", "END");
    }
	
	public void onResume() {
	    super.onResume();
	    makeTab();
	}
	
	public void makeTab() {
	    setContentView(R.layout.tab_city_settings);
        
        //app = (TransitBuddyApp)getApplicationContext();
        //settings = app.getTransitBuddySettings();
        //model = app.getTransitBuddyModel();
        
        // setup counters
        maxFavCityCount     = settings.getMaxFavCityCount();
        currentFavCityCount = settings.getCurrentFavCityCount();
        
        // Restore favorite cities
        alFavCities         = settings.getFavCities();
        
        // Setup default first element
        if(alFavCities.size() == 0) {
            setfirst();
        }
        
        // Setup elements
        favCitySpinner = (Spinner) findViewById(R.id.city_spinner);
        addCityBtn     = (Button) findViewById(R.id.add_city_button);
        saveCitiesBtn  = (Button) findViewById(R.id.save_city_button);
        deleteCityBtn  = (Button) findViewById(R.id.delete_city_button);
        tv             = (TextView) findViewById(R.id.city_count);

        // Setup array adapter
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, alFavCities);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        // Tell the spinner about our adapter
        favCitySpinner.setAdapter(adapter);
        Log.v("*************", "***************");
        settings.setSelectedCity(alFavCities.get(
                //favCitySpinner.getSelectedItemPosition()));
                settings.getFavCityIndex()));
        favCitySpinner.setSelection(settings.getFavCityIndex());
        settings.setFavCityIndex(
                favCitySpinner.getSelectedItemPosition());
        
        // Restore selected city
        //favCitySpinner.setSelection(settings.getFavCityIndex());
        
        Log.v("IN TRANSIT CITY CTRL: FAV CITY INDEX RCVD:", 
                Integer.toString(settings.getFavCityIndex()));
        
        //if(alFavCities.size() != 0) {
        //settings.setSelectedCity(alFavCities.get(
        //        favCitySpinner.getSelectedItemPosition()));
        Log.v("IN TRANSIT CITY CTRL: ", "SELECTED CITY RESOTRED!");
        //}
        
        // Tell the spinner what to do when an item is changed
        favCitySpinner.setOnItemSelectedListener(this);
        
        
        // Populates the list of favorite cities
        // recallFavoriteCities();
        
        // Populates the list of all cities
        //recallAllCities();
        //createAddFavoriteCityDialog();
        //createDelFavoriteCityDialog();
        
        updateNumCities();

        // Handler for Save Cities Button Press
        saveCitiesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(app.getLoginStatus()) {
                    saveFavCities(alFavCities);
                    saveFavCitiesToServer(favCityCount, 
                            Integer.toString(alFavCities.size()));
                } else {
                    Toast.makeText(getApplicationContext(), 
                            "Please Login!", Toast.LENGTH_LONG).show();
                }
            }
        });
        
        // Handler for Add City Button Press
        addCityBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!maxFavCities()) {
                    AlertDialog addAlert = addCityBuilder.create();
                    addAlert.show();
                }
            }
        });

        // Handler for Delete City Button press
        deleteCityBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                /*
                Toast.makeText(getApplicationContext(), 
                        alFavCities.get(alFavCities.size() - 1) + 
                        " has been deleted.", Toast.LENGTH_SHORT).show();
                alFavCities.remove(alFavCities.size() - 1);
                Log.v("alFAVCITIES: ", alFavCities.toString());
                settings.setCurrentFavCityCount(--currentFavCityCount);
                settings.setFavCities(alFavCities);
                
                settings.setSelectedCity(alFavCities.get(0));
                settings.setFavCityIndex(0);
                favCitySpinner.setSelection(settings.getFavCityIndex());
                
                updateNumCities();
                setButtonStates();
                */
                createDelFavoriteCityDialog();
                AlertDialog delAlert = delCityBuilder.create();
                delAlert.show();
            }
        });
        
        // set button states, buttons enabled by default
        Log.v("IN CITY SETTINGS CTRL, SETTING BUTTON STATES", "");
        setButtonStates();
	}
	
	private void recallAllCities() {

	    try {
            model.getTransitSystems(alAllCities); 
        } catch(IOException ioe) {
            Toast.makeText(getApplicationContext(), ioe.getMessage(), 
                    Toast.LENGTH_LONG);
        }
	}
	
	private void addNewFavoriteCity(String newSelectedCity) {
	    
	    Log.v("********** BEGIN ADDNEWFAVORITECITY() **********", "BEGIN");
		//int size = favoriteCities.length;
		
		//if (size == FAV_CITY_MAX_CNT )
		if(!maxFavCities())
		/*
		{
			for ( int i = FAV_CITY_MAX_CNT-1; i >= 1; i--)
			{
				// Adds the new city to the front of the list
				favoriteCities[i]=  favoriteCities[i-1];
			}
			
			// Adds the new city to the front of the list
			favoriteCities[0] = newSelectedCity;
		}
		else
		*/
		{
			//favoriteCities[currentFavIndex] = newSelectedCity;
			alFavCities.add(newSelectedCity);
			Log.v("alFAVCITIES: ", alFavCities.toString());
			settings.setCurrentFavCityCount(++currentFavCityCount);
			
			//settings.setFavCityIndex(favCitySpinner.getSelectedItemPosition() + 1);
			
			settings.setFavCities(alFavCities);
			//alFavCities = settings.getFavCities();
			//favCitySpinner.setSelection(settings.getFavCityIndex());
			//settings.setSelectedCity(alFavCities.get(
            //        favCitySpinner.getSelectedItemPosition()));
			
			//adapter = new ArrayAdapter<String>(this,
	        //        android.R.layout.simple_spinner_item, alFavCities);
	        //adapter.setDropDownViewResource(
	        //        android.R.layout.simple_spinner_dropdown_item);
	        //favCitySpinner.setAdapter(adapter);
	        //favCitySpinner.setOnItemSelectedListener(this);
	        Log.v("alFAVCITIES: ", alFavCities.toString());
	        
	        settings.setSelectedCity(alFavCities.get(
	                favCitySpinner.getSelectedItemPosition()));
	                //settings.getFavCityIndex()));
	        settings.setFavCityIndex(favCitySpinner.getSelectedItemPosition());
	        // Tell the spinner about our adapter
	        //favCitySpinner.setAdapter(adapter);
	        Log.v("********** END ADDNEWFAVORITECITY() **********", "END");
		}
	}
	
	private void deleteFavoriteCity(String newSelectedCity) {
	    
	    Log.v("********** BEGIN DELETEFAVORITECITY() **********", "BEGIN");
	    alFavCities.remove(newSelectedCity);
	    Log.v("alFAVCITIES: ", alFavCities.toString());
	    settings.setCurrentFavCityCount(--currentFavCityCount);
        
	    settings.setFavCities(alFavCities);
        settings.setSelectedCity(alFavCities.get(0));
        settings.setFavCityIndex(0);
        favCitySpinner.setAdapter(adapter);
        favCitySpinner.setSelection(settings.getFavCityIndex());
	    
	    Log.v("********** END DELETEFAVORITECITY() **********", "END");
	}
		
	private void createAddFavoriteCityDialog() {
		
	    Log.v("********** BEGIN ADD CITY DIALOG **********", "BEGIN");
	    addCityBuilder = new AlertDialog.Builder(this);
	      
		// Perform action on click
	    addCityBuilder.setTitle("Add a favorite city:");  
	    
	    allCityArray = (String[]) alAllCities.toArray(new String[0]);
	     
	    // Sets up the dialog menu
	    addCityBuilder.setSingleChoiceItems(allCityArray, -1, 
	            new DialogInterface.OnClickListener() {
		        
	        public void onClick(DialogInterface dialog, int item) {
		        selectedCity = alAllCities.get(item); 
		       	// Toast.makeText(getApplicationContext(), allCities.get(item), Toast.LENGTH_SHORT).toString();
		    }
		});
		   	   
		// handler for OK button in dialog
	    addCityBuilder.setPositiveButton(R.string.alert_dialog_ok, 
	            new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            
	            Log.v("********** BEGIN CLICK OK **********", "BEGIN");    
	            // User clicked OK
	            
	            if (!alFavCities.contains(selectedCity)) {
    	            addNewFavoriteCity(selectedCity);
    	            //updateNumCities();
    	            	
    	           	Toast.makeText(getApplicationContext(), selectedCity + 
    	            	        " added.", Toast.LENGTH_SHORT).show();
    	           	updateNumCities();
    	           	setButtonStates();
    	           	//favCitySpinner.setSelection(alFavCities.size() - 1);
	            } else {
	                Toast.makeText(getApplicationContext(), selectedCity + 
                            " is already a favorite city!  Please try again.",
                            Toast.LENGTH_SHORT).show();
	            }
	           	
	            Log.v("********** END CLICK OK **********", "END");
	        }
	    });
	        
	     // handler for Cancel button in dialog
	     addCityBuilder.setNegativeButton(R.string.alert_dialog_cancel, 
	             new DialogInterface.OnClickListener() {
	         
	         public void onClick(DialogInterface dialog, int whichButton) {

	             // User clicked cancel 
	         }
	     });
	         
	     addCityBuilder.create();
	     Log.v("********** END ADD CITY DIALOG **********", "END");
	}
	
	public void createDelFavoriteCityDialog() {
	    
	    delCityBuilder = new AlertDialog.Builder(this);
        
        // Perform action on click
        delCityBuilder.setTitle("Delete a favorite city:");  
        
        delCityArray = (String[]) alFavCities.toArray(new String[0]);
         
        // Sets up the dialog menu
        delCityBuilder.setSingleChoiceItems(delCityArray, -1, 
                new DialogInterface.OnClickListener() {
                
            public void onClick(DialogInterface dialog, int item) {
                selectedCity = alFavCities.get(item);
            }
        });
               
        // handler for OK button in dialog
        delCityBuilder.setPositiveButton(R.string.alert_dialog_ok, 
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                
                Log.v("********** BEGIN DEL CLICK OK **********", "BEGIN");    
                // User clicked OK 
                deleteFavoriteCity(selectedCity);
                
                Toast.makeText(getApplicationContext(), selectedCity + 
                        " deleted.", Toast.LENGTH_SHORT).show();
                
                updateNumCities();
                setButtonStates();
                //favCitySpinner.setSelection(alFavCities.size() - 1);
                Log.v("********** END DEL CLICK OK **********", "END");
                }
         });
            
         // handler for Cancel button in dialog
         delCityBuilder.setNegativeButton(R.string.alert_dialog_cancel, 
                 new DialogInterface.OnClickListener() {
             
             public void onClick(DialogInterface dialog, int whichButton) {

                 // User clicked cancel 
             }
         });
             
         delCityBuilder.create();
	}
	    
	public void onItemSelected(AdapterView<?> parent, View view, int position, 
	        long id) {
	    
	    // Get the currently selected State object from the spinner
	    // String city = (String) spinner.getSelectedItem();
	    Log.v("********** BEGIN ONITEMSELECTED() **********", "BEGIN");
	    //settings.setFavCities(alFavCities);
	    //alFavCities = settings.getFavCities();
	    settings.setSelectedCity(alFavCities.get(
                favCitySpinner.getSelectedItemPosition()));
	            //settings.getFavCityIndex()));
	    settings.setFavCityIndex(
                favCitySpinner.getSelectedItemPosition());
	    
	    //updateNumCities();
	    
	    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_spinner_item, alFavCities);
        //adapter.setDropDownViewResource(
        //        android.R.layout.simple_spinner_dropdown_item);
        
        // Tell the spinner about our adapter
        //favCitySpinner.setAdapter(adapter);
	    Log.v("********** END ONITEMSELECTED() **********", "END");
	} 
	    
	public void onNothingSelected(AdapterView<?> parent ) { 
	
	}
	
	/** 
	 * This class updates the favorite city count and disables the add city 
	 * button if the favorite city count reaches 5.
	 */
	public void updateNumCities() {
	    Log.v("********** BEGIN UPDATENUMCITIES() **********", "BEGIN");
	    //tv.setText("Cities saved: " + favoriteCities.length);
	    tv.setText("Cities saved: " + alFavCities.size());
        Log.v("alFavCities", Integer.toString(alFavCities.size()));
        //favCitySpinner.setSelection(settings.getFavCityIndex());
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_spinner_item, alFavCities);
        //adapter.setDropDownViewResource(
        //        android.R.layout.simple_spinner_dropdown_item);
        
        // Tell the spinner about our adapter
        //favCitySpinner.setAdapter(adapter);
        //favCitySpinner.setOnItemSelectedListener(this);
        Log.v("********** END UPDATENUMCITIES() **********", "END");
	}
	
	public void setButtonStates() {
	    Log.v("********** BEGIN SETBUTTONSTATES() **********", "BEGIN");
	    //if(favoriteCities.length == FAV_CITY_MAX_CNT) {
        if(alFavCities.size() == maxFavCityCount) {    
            addCityBtn.setEnabled(false);
        } else {
            addCityBtn.setEnabled(true);
        }
        
        //if(favoriteCities.length == 0) {
        if(alFavCities.size() <= 1) {
            deleteCityBtn.setEnabled(false);
        } else {
            deleteCityBtn.setEnabled(true);
        }
        
        Log.v("********** END SETBUTTONSTATES() **********", "END");
	}
	
	public boolean maxFavCities() {
	    return (alFavCities.size() == maxFavCityCount);
	}
	
	public void setfirst() {
	    Log.v("********** BEGIN SETFIRST() **********", "BEGIN");
	    alFavCities.add("MBTA");
	    settings.setFavCities(alFavCities);
	    settings.setCurrentFavCityCount(++currentFavCityCount);
	    //alFavCities = settings.getFavCities();
	    Log.v("********** END SETFIRST() **********", "END");
	}
	
	public void saveFavCitiesToServer(String key, String value) {
        DefaultHttpClient hc = new DefaultHttpClient();
        
        ResponseHandler<String> res = new BasicResponseHandler();
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(action, setUserData));
        nameValuePairs.add(new BasicNameValuePair(userId,  
                Integer.toString(app.getUserId())));
        nameValuePairs.add(new BasicNameValuePair(appToken, appTokenName));
        nameValuePairs.add(new BasicNameValuePair(dataKey, key));
        nameValuePairs.add(new BasicNameValuePair(dataValue, value));
        
        HttpPost postMethod = new HttpPost(address);
        
        try {
            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        String response;
        
        try {
            response = hc.execute(postMethod, res);
            response = trimResponse(response);
            if (Integer.parseInt(response.trim()) != -1) {
                Log.v("IN CITY SETTINGS CTRL, SAVING " + key + " TO SERVER: ", 
                        value);
                Toast.makeText(getApplicationContext(), 
                        "Success!  Saved " + value + " to server!", 
                        Toast.LENGTH_LONG).show();
            } else {
                app.setLoginStatus(false);
                Log.v("IN CITY SETTINGS CTRL, LOG IN STATUS: ", 
                        Boolean.toString(app.getLoginStatus()));
                Toast.makeText(getApplicationContext(), 
                        "Login Failed!", Toast.LENGTH_LONG).show();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public String trimResponse(String response) {
        String res = response;
        String strBefore = "<data>";
        String strAfter  = "</data>";
        int before = res.indexOf(strBefore) + strBefore.length();
        int after  = res.indexOf(strAfter, before);
        
        res = res.substring(before, after);
        
        Log.v("IN CITY SETTINGS CTRL, RESPONSE: ",  res);
        
        return res;
    }
	
	public void saveFavCities(ArrayList<String> favCities) {
        int count = favCities.size();
        Log.v("IN CITY SETTINGS CTRL, SAVING FAV CITY TO SERVER: ", 
                "USING SIZE: " + Integer.toString(favCities.size()));
        for(int i = 0; i < count; i++) {
            saveFavCitiesToServer(favCity + i, favCities.get(i));
        }
    }
}
