package com.transitbuddy.Controllers.TransitRouteTypeController;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.common.enumerations.RouteType;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.transitbuddy.Controllers.TransitRouteController.*;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;

import com.transitbuddy.*;

public class TransitRouteTypeController extends ListActivity 
{
	TransitBuddyModel mModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
	   // TODO Auto-generated method stub
	   super.onCreate(savedInstanceState);
	   
	   // Get a reference to the TranistBuddy model.
	   TransitBuddyApp app = (TransitBuddyApp)this.getApplicationContext();
	   mModel = app.getTransitBuddyModel();
	  
	   setContentView(R.layout.transit_system_controller_view);
	 
	   updateMenuItems(); 
	}
	
	@Override
	protected void onListItemClick(ListView list, View v, int position, long id) 
	{
		// TODO Auto-generated method stub
		super.onListItemClick(list, v, position, id);
		
		mModel.setRouteType(RouteType.lookup((String)list.getItemAtPosition(position)));
		
		Intent intent = new Intent( getApplicationContext(), TransitRouteController.class);
		
		// Starts the Transit Line Activity
		startActivity(intent);	
	}
	 
	private void updateMenuItems()
	{
		ArrayList<String> routeTypes = new ArrayList<String>();
		
		String systemID = mModel.getSystemID();
		
		if(mModel.getTransitRouteTypes(systemID, routeTypes) == ResponseStatus.Completed)
		{
			setListAdapter(new ArrayAdapter<String>(this,
							android.R.layout.simple_list_item_1, routeTypes));	 
		}
	}	
}
