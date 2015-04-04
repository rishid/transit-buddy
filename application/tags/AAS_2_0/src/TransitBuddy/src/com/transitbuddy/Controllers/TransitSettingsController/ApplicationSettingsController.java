package com.transitbuddy.Controllers.TransitSettingsController;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.transitbuddy.R;

public class ApplicationSettingsController 
	extends Activity implements OnItemSelectedListener 
	{
		
		static Spinner vicinity_spinner = null;
		static Spinner eta_spinner = null;
		static Button loginBtn = null;
		
		private EditText edittext_username = null;
		private EditText edittext_password = null;
		
		static final String[] vicinities = new String[] {
			 "1/8 Mile", "1/4 Mile", "1/2 Mile", "3/4 Mile", "1 Mile" };
		
		static final String[] etas = new String[] {
			 "1", "2", "3", "4", "5" };
		   
	
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);


        setContentView(R.layout.tab_application_settings);
			vicinity_spinner = (Spinner) findViewById(R.id.vicinities_spinner);
			eta_spinner = (Spinner) findViewById(R.id.etas_spinner);
			
			ArrayAdapter<String> vicinity_adapter = new ArrayAdapter<String>(this,
		              android.R.layout.simple_spinner_item, vicinities);
			vicinity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			ArrayAdapter<String> eta_adapter = new ArrayAdapter<String>(this,
		              android.R.layout.simple_spinner_item, etas);
			eta_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    
			 // Tell the spinner about our adapter
			vicinity_spinner.setAdapter(vicinity_adapter);
			eta_spinner.setAdapter(eta_adapter);
	        
	        // Tell the spinner what to do when an item is changed
			vicinity_spinner.setOnItemSelectedListener(this);
			eta_spinner.setOnItemSelectedListener(this);
			
			edittext_username = (EditText) findViewById(R.id.username_entry);
			edittext_username.setOnKeyListener(new OnKeyListener() {
			    public boolean onKey(View v, int keyCode, KeyEvent event) {
			        // If the event is a key-down event on the "enter" button
			        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
			            (keyCode == KeyEvent.KEYCODE_ENTER)) {
			         
			          // Perform action on key press
			          // Toast.makeText(this, edittext_username.getText(), Toast.LENGTH_SHORT).show();
			          return true;
			        }
			        return false;
			    }
			});
		
			edittext_password = (EditText) findViewById(R.id.password_entry);
			edittext_password.setOnKeyListener(new OnKeyListener() {
			    public boolean onKey(View v, int keyCode, KeyEvent event) {
			        // If the event is a key-down event on the "enter" button
			        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
			            (keyCode == KeyEvent.KEYCODE_ENTER)) {
			         
			          // Perform action on key press
			          // Toast.makeText(this, edittext_username.getText(), Toast.LENGTH_SHORT).show();
			          return true;
			        }
			        return false;
			    }
			});
			
	    }
		    
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
		{
		    	// Get the currently selected State object from the spinner
		    	String vicinity = (String) vicinity_spinner.getSelectedItem();
		    	
		    	String eta = (String) eta_spinner.getSelectedItem();   
		} 
		    
		public void onNothingSelected(AdapterView<?> parent ) 
		{ 
		}
    
    /* Code here needs to be brought online when group 5 gets their login
     * stuff up and running 
     */
    /*DefaultHttpClient hc = new DefaultHttpClient();
    
    ResponseHandler<String> res = new BasicResponseHandler();
    
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    nameValuePairs.add(new BasicNameValuePair("action", "getUserId"));
    nameValuePairs.add(new BasicNameValuePair("username", 
            "amit@tamba.name"));
    nameValuePairs.add(new BasicNameValuePair("password", "password"));
    
    HttpPost postMethod = new HttpPost(
            "http://129.10.128.235/~cse/CommonFunctionality/");
    
    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    String response = hc.execute(postMethod, res);
    showDialog(response.trim());*/
}
