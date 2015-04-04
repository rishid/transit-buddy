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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.transitbuddy.R;
import com.transitbuddy.SystemSettings.TransitSettings;
import com.transitbuddy.TransitBuddy.*;

public class ApplicationSettingsController 
	    extends Activity implements OnItemSelectedListener {
	
    // Common functionality variables
    private static final String address = 
        "http://129.10.128.235/~cse/CommonFunctionality/";
    private static final String action        = "action";
    private static final String getUserId     = "getUserId";
    private static final String createUser    = "createUser";
    private static final String getUserData   = "getUserData";
    private static final String setUserData   = "setUserData";
    private static final String email         = "email";
    private static final String password      = "password";
    private static final String userId        = "userId";
    private static final String dataKey       = "dataKey";
    private static final String dataValue     = "dataValue";
    private static final String appTokenName  = "blue9";
    private static final String appToken      = "appToken";
    //private static final String vicinity      = "vicinity";
    private static final String vicinityIndex = "vicinityIndex";
    //private static final String eta           = "eta";
    private static final String etaIndex      = "etaIndex";
    private static final String favCity       = "favCity";
    private static final String favCityCount  = "favCityCount";
    
    // Element variables
	private static Spinner vicinity_spinner   = null;
	private static Spinner eta_spinner        = null;
	private static Button retrieveBtn         = null;
	private static Button loginBtn            = null;
	private static Button logoutBtn           = null;
	private static Button registerBtn         = null;
	private static EditText edittext_username = null;
	private static EditText edittext_password = null;
	
	// Class variables
	private TransitSettings settings = null;
	private TransitBuddyApp app      = null;
	
	private String[] ETAsString;
	private String[] vicinitiesString;
	private int[]    ETAsInt;
	private float[]  vicinitiesFloat;
	
	//static private boolean loggedIn = false;
	
	public void onCreate(Bundle savedInstanceState) {
        
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.tab_application_settings);
	    
	    app = (TransitBuddyApp)getApplicationContext();
        settings = app.getTransitBuddySettings();
	    
        // Populate the arrays for the spinners
        ETAsInt          = settings.getETAIntArray();
        ETAsString       = settings.getETAStringArray();
        vicinitiesString = settings.getVicinityStringArray();
        vicinitiesFloat  = settings.getVicinityFloatArray();
		
        // Setup elements
        vicinity_spinner  = (Spinner) findViewById(R.id.vicinities_spinner);
		eta_spinner       = (Spinner) findViewById(R.id.etas_spinner);
		retrieveBtn       = (Button) findViewById(R.id.retrieve_button);
		loginBtn          = (Button) findViewById(R.id.login_button);
		logoutBtn         = (Button) findViewById(R.id.logout_button);
		registerBtn       = (Button) findViewById(R.id.register_button);
	    edittext_username = (EditText) findViewById(R.id.username_entry);
	    edittext_password = (EditText) findViewById(R.id.password_entry);
		
		// Setup array adapters
		ArrayAdapter<String> vicinity_adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item, vicinitiesString);
		vicinity_adapter.setDropDownViewResource(
		        android.R.layout.simple_spinner_dropdown_item);
		
		ArrayAdapter<String> eta_adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item, ETAsString);
		eta_adapter.setDropDownViewResource(
		        android.R.layout.simple_spinner_dropdown_item);
	    
		// Tell the spinners about our adapters
		vicinity_spinner.setAdapter(vicinity_adapter);
		eta_spinner.setAdapter(eta_adapter);
		
		// Restore vicinity settings
		vicinity_spinner.setSelection(settings.getVicinityIdx());
		settings.setVicinity(
		        vicinitiesFloat[vicinity_spinner.getSelectedItemPosition()]);
		Log.v("IN APP SETTINGS CONTROLLER, VICINITY RCVD: ", 
		        Float.toString(settings.getVicinity()));
		
		// Restore ETA settings
		eta_spinner.setSelection(settings.getNumberOfETAsIdx());
		settings.setNumberOfETAs(
                ETAsInt[eta_spinner.getSelectedItemPosition()]);
		Log.v("IN APP SETTINGS CONTROLLER, ETA RCVD: ", 
                Integer.toString(settings.getNumberOfETAs()));
		
        // Tell the spinner what to do when an item is changed
		vicinity_spinner.setOnItemSelectedListener(this);
		eta_spinner.setOnItemSelectedListener(this);
		
		// Set button states
		setButtonStates();
		retrieveBtn.setEnabled(false);
		retrieveBtn.setVisibility(View.INVISIBLE);
		
		retrieveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
		
		loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                settings.setUserName(
                        edittext_username.getText().toString());
                settings.setPassword(
                        edittext_password.getText().toString());
                
                login(false);
            }
        });
		
		logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (app.getLoginStatus() != false) {
                    app.setLoginStatus(false);
                    loginBtn.setEnabled(true);
                    
                    Log.v("IN APP SETTINGS CTRL, LOG IN STATUS: ", 
                            Boolean.toString(app.getLoginStatus()));
                    Toast.makeText(getApplicationContext(), 
                            "Logout successful!", Toast.LENGTH_LONG).show();
                }
            }
        });
		
		registerBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                register();
            }
        });
		
    } // end onCreate
		    
	public void onItemSelected(AdapterView<?> parent, View view, 
	        int position, long id) {
	    
	    switch (parent.getId()) {
	    
	    case R.id.vicinities_spinner:
    	    // Get the currently selected vicinity from the spinner
    	    String vicinity = (String) vicinity_spinner.getSelectedItem();
    	    
    	    // Get the position of the selected vicinity
    	    int i = vicinity_spinner.getSelectedItemPosition();
    	    
    	    // Pass the vicinity to the TransitSettings class and save it
    	    Log.v("VICINITY FROM SPINNER: ", vicinity);
    	    Log.v("POSITION IN SPINNER: ", Integer.toString(i));
    	    Log.v("SETTING VICINITY TO: ", Float.toString(vicinitiesFloat[i]));
    	    
    	    settings.setVicinity(vicinitiesFloat[i]);
    	    settings.setVicinityIdx(i);
    	    
    	    if (app.getLoginStatus()) {
    	        //saveSettingsToServer(ApplicationSettingsController.vicinity, 
    	        //        Float.toString(vicinitiesFloat[i]));
    	        saveSettingsToServer(vicinityIndex, Integer.toString(i));
    	    }
    	    
    	    break;
	    
	    case R.id.etas_spinner:
    	    // Get the currently selected ETA from the spinner
    	    String eta = (String) eta_spinner.getSelectedItem();   
    	    
    	    // Get the position of the selected ETA
    	    int j = eta_spinner.getSelectedItemPosition();
            
    	    // Pass the ETA to the TransitSettings class and save it
            Log.v("ETA FROM SPINNER: ", eta);
            Log.v("POSITION IN SPINNER: ", Integer.toString(j));
            Log.v("SETTING ETA TO: ", ETAsString[j]);
            
            settings.setNumberOfETAs(ETAsInt[j]);
            settings.setNumberOfETAsIdx(j);
            
            if (app.getLoginStatus() != false) {
                //saveSettingsToServer(ApplicationSettingsController.eta, 
                //        ETAsString[j]);
                saveSettingsToServer(etaIndex, Integer.toString(j));
            }
            
            break;
            
	    }
        //app.setTransitSettings(settings);
	}
	    
	public void onNothingSelected(AdapterView<?> parent ) { 
	
	}
	
	public void login(boolean registering) {
	    
        DefaultHttpClient hc = new DefaultHttpClient();
        
        ResponseHandler<String> res = new BasicResponseHandler();
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(action, getUserId));
        nameValuePairs.add(new BasicNameValuePair(email, 
                settings.getUserName()));
        nameValuePairs.add(new BasicNameValuePair(password, 
                settings.getPassword()));
        
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
                edittext_username.setText("");
                edittext_password.setText("");
                app.setUserId(Integer.parseInt(response));
                
                if (!registering) {
                    app.setLoginStatus(true);
                    setButtonStates();
                    Log.v("IN APP SETTINGS CTRL, LOG IN STATUS: ", 
                            Boolean.toString(app.getLoginStatus()));
                    Toast.makeText(getApplicationContext(), 
                            "Login Successful!  Your Login ID is: " + response, 
                            Toast.LENGTH_LONG).show();
                    getSettingsFromServer(vicinityIndex);
                    getSettingsFromServer(etaIndex);
                    settings.setFavCities(getFavCities());
                }
            } else {
                app.setLoginStatus(false);
                loginBtn.setEnabled(!app.getLoginStatus());
                Log.v("IN APP SETTINGS CTRL, LOG IN STATUS: ", 
                        Boolean.toString(app.getLoginStatus()));
                Toast.makeText(getApplicationContext(), 
                        "Login Failed!  Please check your credentials or " +
                        "register.", Toast.LENGTH_LONG).show();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void register() {
	    
	    DefaultHttpClient hc = new DefaultHttpClient();
        
        ResponseHandler<String> res = new BasicResponseHandler();
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(action, createUser));
        nameValuePairs.add(new BasicNameValuePair(email, 
                edittext_username.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair(password, 
                edittext_password.getText().toString()));
        
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
                Log.v("IN APP SETTINGS CTRL, REGISTER STATUS: ", response);
                
                // Login to setup an initial profile
                settings.setUserName(
                        edittext_username.getText().toString());
                settings.setPassword(
                        edittext_password.getText().toString());
                login(true);
                app.setLoginStatus(false);
                loginBtn.setEnabled(true);
                
                // Save settings to profile
                ArrayList<String> alist = settings.getFavCities();;
                saveSettingsToServer(vicinityIndex, 
                        Integer.toString(settings.getVicinityIdx()));
                saveSettingsToServer(etaIndex, 
                        Integer.toString(settings.getNumberOfETAsIdx()));
                saveFavCities(alist);
                saveSettingsToServer(favCityCount, 
                        Integer.toString(settings.getCurrentFavCityCount()));
                
                Toast.makeText(getApplicationContext(), 
                        "Registration Successful!", 
                        Toast.LENGTH_LONG).show();
            } else {
                Log.v("IN APP SETTINGS CTRL, REGISTER STATUS: ", response);
                Toast.makeText(getApplicationContext(), 
                        "Registration Failed!", Toast.LENGTH_LONG).show();
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
	    
	    Log.v("IN APP SETTINGS CTRL, RESPONSE: ",  res);
	    
	    return res;
	}
	
	public void saveSettingsToServer(String key, String value) {
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
                Log.v("IN APP SETTINGS CTRL, SAVING " + key + " TO SERVER: ", 
                        value);
                Toast.makeText(getApplicationContext(), 
                        "Success!  Saved " + key + " to server!", 
                        Toast.LENGTH_LONG).show();
            } else {
                app.setLoginStatus(false);
                loginBtn.setEnabled(app.getLoginStatus());
                Log.v("IN APP SETTINGS CTRL, LOG IN STATUS: ", 
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
                Log.v("IN APP SETTINGS CTRL, SAVING " + key + " TO SERVER: ", 
                        value);
                Toast.makeText(getApplicationContext(), 
                        "Success!  Saved " + value + " to server!", 
                        Toast.LENGTH_LONG).show();
            } else {
                app.setLoginStatus(false);
                Log.v("IN APP SETTINGS CTRL, LOG IN STATUS: ", 
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
	
	public void getSettingsFromServer(String key) {
        DefaultHttpClient hc = new DefaultHttpClient();
        
        ResponseHandler<String> res = new BasicResponseHandler();
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(action, getUserData));
        nameValuePairs.add(new BasicNameValuePair(userId,  
                Integer.toString(app.getUserId())));
        nameValuePairs.add(new BasicNameValuePair(appToken, appTokenName));
        nameValuePairs.add(new BasicNameValuePair(dataKey, key));
        
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
                if (key.equals(etaIndex)) {
                    Log.v("IN APP SETTINGS CTRL, RETRIEVING " + key + 
                            " FROM SERVER:", response);
                    Toast.makeText(getApplicationContext(), 
                            "Success!  Retrieved " + key + " from server!", 
                            Toast.LENGTH_LONG).show();
                    settings.setNumberOfETAsIdx(Integer.parseInt(response));
                    settings.setNumberOfETAs(
                            ETAsInt[Integer.parseInt(response)]);
                    eta_spinner.setSelection(settings.getNumberOfETAsIdx());
                } else if (key.equals(vicinityIndex)) {
                    Log.v("IN APP SETTINGS CTRL, RETRIEVING " + key + 
                            " FROM SERVER: ", response);
                    Toast.makeText(getApplicationContext(), 
                            "Success!  Retrieved " + key + " from server!", 
                            Toast.LENGTH_LONG).show();
                    settings.setVicinityIdx(Integer.parseInt(response));
                    settings.setVicinity(
                            vicinitiesFloat[Integer.parseInt(response)]);
                    vicinity_spinner.setSelection(settings.getVicinityIdx());
                } else if (key.equals(favCityCount)) {
                    Log.v("IN APP SETTINGS CTRL, RETRIEVING " + key + 
                            " FROM SERVER: ", response);
                    Toast.makeText(getApplicationContext(), 
                            "Success!  Retrieved " + key + " from server!", 
                            Toast.LENGTH_LONG).show();
                    settings.setCurrentFavCityCount(Integer.parseInt(response));
                }
            } else {
                app.setLoginStatus(false);
                Log.v("IN APP SETTINGS CTRL, LOG IN STATUS: ", 
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
	
	public String getFavCitiesFromServer(String key) {
        DefaultHttpClient hc = new DefaultHttpClient();
        
        ResponseHandler<String> res = new BasicResponseHandler();
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(action, getUserData));
        nameValuePairs.add(new BasicNameValuePair(userId,  
                Integer.toString(app.getUserId())));
        nameValuePairs.add(new BasicNameValuePair(appToken, appTokenName));
        nameValuePairs.add(new BasicNameValuePair(dataKey, key));
        
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
            if (!response.equals("NO CITY")) {
                Log.v("IN APP SETTINGS CTRL, RETRIEVING " + key + 
                        " FROM SERVER: ", response);
                Toast.makeText(getApplicationContext(), 
                        "Success!  Retrieved " + response + " from server!", 
                        Toast.LENGTH_LONG).show();
                return response;
            } else {
                app.setLoginStatus(false);
                Log.v("IN APP SETTINGS CTRL, LOG IN STATUS: ", 
                        Boolean.toString(app.getLoginStatus()));
                Toast.makeText(getApplicationContext(), 
                        "Login Failed!", Toast.LENGTH_LONG).show();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "NO CITY";
    }
	
	public void saveFavCities(ArrayList<String> favCities) {
        int count = favCities.size();
        Log.v("IN APP SETTINGS CTRL, SAVING FAV CITY TO SERVER: ", 
                "USING SIZE: " + Integer.toString(favCities.size()));
        for(int i = 0; i < count; i++) {
            saveFavCitiesToServer(favCity + i, favCities.get(i));
        }
    }
	
	public ArrayList<String> getFavCities() {
        getSettingsFromServer(favCityCount);
	    int count = settings.getCurrentFavCityCount();
	    ArrayList<String> alist = new ArrayList<String>();
        Log.v("IN APP SETTINGS CTRL, RETRIEVING FAV CITY FROM SERVER: ", 
                "USING SIZE: " + Integer.toString(count));
        for(int i = 0; i < count; i++) {
            alist.add(getFavCitiesFromServer(favCity + i));
        }
        
        return alist;
    }
	
	public void setButtonStates() {
	    if (app.getLoginStatus()) {
	        loginBtn.setEnabled(!app.getLoginStatus());
	    }
	}
}
