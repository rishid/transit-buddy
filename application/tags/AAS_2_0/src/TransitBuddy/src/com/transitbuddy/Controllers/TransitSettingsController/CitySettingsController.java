package com.transitbuddy.Controllers.TransitSettingsController;

import java.util.ArrayList;
import java.util.Collections;

import com.transitbuddy.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CitySettingsController extends Activity implements OnItemSelectedListener {
	
    static int FAV_CITY_MAX_CNT = 5;
    
	static Spinner spinner      = null;
	static TextView tv          = null;
	static Button addCityBtn    = null;
	static Button saveCitiesBtn = null;
	AlertDialog.Builder builder = null;

	private String[] allArr = {};
	
	String[] favoriteCities = new String[FAV_CITY_MAX_CNT];
	
	ArrayList<String> allCities = new ArrayList<String>();

	String selectedCity;
	int currentFavIndex = 0;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_city_settings);

	    // Populates the list of favorite cities
	    recallFavoriteCities();
	    
	    // Populates the list of all cities
	    recallAllCities();
	    
	    createAddFavoriteCityDialog();
	    
	    spinner       = (Spinner) findViewById(R.id.city_spinner);
	    addCityBtn    = (Button) findViewById(R.id.add_city_button);
	    saveCitiesBtn = (Button) findViewById(R.id.save_city_button);

	    // Handler for Add City Button Press
	    addCityBtn.setOnClickListener(new View.OnClickListener()
	    {
            public void onClick(View v) {
            	
            	AlertDialog alert = builder.create();
            	alert.show();
            }
        });

	    // Handler for Save Cities Button Press
	    saveCitiesBtn.setOnClickListener(new View.OnClickListener() 
	    {
            public void onClick(View v) 
            {
                // Perform action on click
            }
        });
	    
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	              android.R.layout.simple_spinner_item, favoriteCities);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    
		 // Tell the spinner about our adapter
        spinner.setAdapter(adapter);
        
        // Tell the spinner what to do when an item is changed
        spinner.setOnItemSelectedListener(this);
        
        
        // Track favorite city count
        tv = (TextView) findViewById(R.id.city_count);
        updateNumCities();
    }
	
	private void recallFavoriteCities()
	{
		favoriteCities[0] = "Boston";
		favoriteCities[1] = "NYC"; 
		favoriteCities[2] = "San Fran";
		favoriteCities[3] = "DC";
		favoriteCities[4] = "Chicago"; 
		
		currentFavIndex = FAV_CITY_MAX_CNT;
	}
	
	private void recallAllCities()
	{
		allCities.add("Boston"); 
		allCities.add("NYC");
		allCities.add("San Fran");
		allCities.add("DC");
		allCities.add("Chicago");
		allCities.add("Dallas");
		allCities.add("Jersey"); 
		allCities.add("London");
		allCities.add("Montreal"); 
	}
	
	private void addNewFavoriteCity(String newSelectedCity)
	{
		int size = favoriteCities.length;
		
		if (size == FAV_CITY_MAX_CNT )
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
		{
			favoriteCities[currentFavIndex] = newSelectedCity;
			
			currentFavIndex++;
		}
	}
		
	private void createAddFavoriteCityDialog()
	{
		 builder = new AlertDialog.Builder(this);
	      
		 // Perform action on click
	     builder.setTitle("Select a new favorite city");  
	    
	     allArr = (String[]) allCities.toArray(new String[0]);
	     
	     // Sets up the dialog menu
		 builder.setSingleChoiceItems(allArr, -1, new DialogInterface.OnClickListener()
		 {
		        public void onClick(DialogInterface dialog, int item) 
		        {
		        	selectedCity = allCities.get(item); // Toast.makeText(getApplicationContext(), allCities.get(item), Toast.LENGTH_SHORT).toString();
		        }
		 });
		   	   
		 // handler for OK button in dialog
	     builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() 
	     {
	            public void onClick(DialogInterface dialog, int whichButton) 
	            {
	                // User clicked OK 
	            	addNewFavoriteCity(selectedCity);
	            	
	            	Toast.makeText(getApplicationContext(), selectedCity + " added.", Toast.LENGTH_SHORT).show();
	            }
	     });
	        
	     // handler for Cancel button in dialog
	     builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener()
	     {
	            public void onClick(DialogInterface dialog, int whichButton) 
	            {

	                // User clicked cancel 
	            }
	     });
	         
	       builder.create();
	}
	    
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
	{
	    	// Get the currently selected State object from the spinner
	    	String city = (String) spinner.getSelectedItem();
	} 
	    
	public void onNothingSelected(AdapterView<?> parent ) 
	{ 
	}
	
	/* 
	 * This class update the favorite city count and disables the add city 
	 * button if the favorite city count reaches 5.
	 */
	public void updateNumCities() {
	    tv.setText("Cities saved: " + favoriteCities.length);
	    
	   /* if(cities.length == FAV_CITY_MAX_CNT) {
	        addCityBtn.setEnabled(false);
	    }*/
	}

}
