package com.transitbuddy.Controllers.TransitSettingsController;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.transitbuddy.SystemSettings.TransitSettings;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;

public class CitySettingsController 
        extends Activity implements OnItemSelectedListener {
    
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
	static final String PREFS = "prefs";
	
	ArrayAdapter<String> adapter = null;
	
	public void onCreate(Bundle savedInstanceState) {
		Log.v("********** BEGIN ONCREATE() **********", "BEGIN");
	    // *******************************************************************//
	    SharedPreferences prefs = getSharedPreferences(PREFS, 0);
	    
        // *******************************************************************//
	    
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.tab_city_settings);
	    
	    app = (TransitBuddyApp)getApplicationContext();
        settings = app.getTransitBuddySettings();
        
	    // setup counters
	    maxFavCityCount     = settings.getMaxFavCityCount();
	    currentFavCityCount = settings.getCurrentFavCityCount();
	    
	    // Restore favorite cities
	    alFavCities         = settings.getFavCities();
	    
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
	    recallAllCities();
	    createAddFavoriteCityDialog();
	    //createDelFavoriteCityDialog();
	    
	    updateNumCities();

	    // Handler for Add City Button Press
	    addCityBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             // Check for maximum no. of favorite cities
                //updateNumCities();
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
                Toast.makeText(getApplicationContext(), 
                        alFavCities.get(alFavCities.size() - 1) + 
                        "has been deleted.", Toast.LENGTH_SHORT).show();
                alFavCities.remove(alFavCities.size() - 1);
                Log.v("alFAVCITIES: ", alFavCities.toString());
                settings.setCurrentFavCityCount(--currentFavCityCount);
                settings.setFavCities(alFavCities);
                
                settings.setSelectedCity(alFavCities.get(0));
                settings.setFavCityIndex(0);
                favCitySpinner.setSelection(settings.getFavCityIndex());
                
                updateNumCities();
                setButtonStates();
                
                //createDelFavoriteCityDialog();
                //AlertDialog delAlert = delCityBuilder.create();
                //delAlert.show();
            }
        });

	    // Handler for Save Cities Button Press
	    saveCitiesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });
	    
	    // set button states, buttons enabled by default
	    Log.v("IN CITY SETTINGS CTRL, SETTING BUTTON STATES", "");
	    setButtonStates();
	    Log.v("********** END ONCREATE() **********", "END");
    }
	
	private void setInitialCity() {
        alFavCities.add("Boston");
        settings.setFavCities(alFavCities);
    }

    private void recallFavoriteCities() {
	    /*
		favoriteCities[0] = "Boston";
		favoriteCities[1] = "NYC"; 
		favoriteCities[2] = "San Fran";
		favoriteCities[3] = "DC";
		favoriteCities[4] = "Chicago";
		*/
	    
	    alFavCities.add("Boston");
	    alFavCities.add("NYC");
	    alFavCities.add("DC");
	    alFavCities.add("London");
	    
	    if(alFavCities.size() < maxFavCityCount) {
	        //currentFavIndex = FAV_CITY_MAX_CNT;
	        currentFavIndex = alFavCities.size() + 1;
	    }
	}
	
	private void recallAllCities() {
	    
		alAllCities.add("Boston"); 
		alAllCities.add("NYC");
		alAllCities.add("San Fran");
		alAllCities.add("DC");
		alAllCities.add("Chicago");
		alAllCities.add("Dallas");
		alAllCities.add("Jersey"); 
		alAllCities.add("London");
		alAllCities.add("Montreal"); 
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
			Log.v("line 204", "line 204");
			settings.setCurrentFavCityCount(++currentFavCityCount);
			Log.v("line 206", "line 206");
			
			//settings.setFavCityIndex(favCitySpinner.getSelectedItemPosition() + 1);
			
			Log.v("line 208", "line 208");
			settings.setFavCities(alFavCities);
			Log.v("line 210", "line 210");
			//alFavCities = settings.getFavCities();
			Log.v("line 212", "line 212");
			//favCitySpinner.setSelection(settings.getFavCityIndex());
			Log.v("line 214", "line 214");
			//settings.setSelectedCity(alFavCities.get(
            //        favCitySpinner.getSelectedItemPosition()));
			Log.v("line 215", "line 215");
			
			//adapter = new ArrayAdapter<String>(this,
	        //        android.R.layout.simple_spinner_item, alFavCities);
	        //adapter.setDropDownViewResource(
	        //        android.R.layout.simple_spinner_dropdown_item);
			Log.v("IN CITY SETTINGS CTRL, SELECTED CITY BEFORE ADAPTER SET: ", alFavCities.get(
                    favCitySpinner.getSelectedItemPosition()));
	        //favCitySpinner.setAdapter(adapter);
	        Log.v("IN CITY SETTINGS CTRL, SELECTED CITY AFTER ADAPTER SET: ", alFavCities.get(
	                favCitySpinner.getSelectedItemPosition()));
	        //favCitySpinner.setOnItemSelectedListener(this);
	        Log.v("alFAVCITIES: ", alFavCities.toString());
	        
	        settings.setSelectedCity(alFavCities.get(
	                favCitySpinner.getSelectedItemPosition()));
	                //settings.getFavCityIndex()));
	        settings.setFavCityIndex(
	                favCitySpinner.getSelectedItemPosition());
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
	    
	    settings.setSelectedCity(alFavCities.get(
                favCitySpinner.getSelectedItemPosition()));
                //settings.getFavCityIndex()));
        settings.setFavCityIndex(
                favCitySpinner.getSelectedItemPosition());
	    
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
	            addNewFavoriteCity(selectedCity);
	            //updateNumCities();
	            	
	           	Toast.makeText(getApplicationContext(), selectedCity + 
	            	        " added.", Toast.LENGTH_SHORT).show();
	           	updateNumCities();
	           	setButtonStates();
	           	//favCitySpinner.setSelection(alFavCities.size() - 1);
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
                // Toast.makeText(getApplicationContext(), allCities.get(item), Toast.LENGTH_SHORT).toString();
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
	    //return (favoriteCities.length == FAV_CITY_MAX_CNT);
	    return (alFavCities.size() == maxFavCityCount);
	}
	
	public void setfirst() {
	    Log.v("********** BEGIN SETFIRST() **********", "BEGIN");
	    alFavCities.add("Boston");
	    settings.setFavCities(alFavCities);
	    settings.setCurrentFavCityCount(++currentFavCityCount);
	    //alFavCities = settings.getFavCities();
	    Log.v("********** END SETFIRST() **********", "END");
	}

}
