package com.transitbuddy.Controllers.TransitRouteController;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.common.enumerations.RouteType;
import com.common.utilities.ResponseResult.ResponseStatus; 
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;
import com.transitbuddy.Controllers.TransitRouteController.ExpandableListAdapter;
import com.transitbuddy.Controllers.TransitStopController.TransitStopController;

import com.transitbuddy.*;

public class TransitRouteController extends Activity 
{ 
	TransitBuddyModel mModel;
	ExpandableListView mExpandableListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    // TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	  
	    setContentView(R.layout.transit_line_controller_view);
	    
	    TransitBuddyApp app = (TransitBuddyApp)getApplicationContext();
	    
	    mModel = app.getTransitBuddyModel();
	    
	    mExpandableListView = (ExpandableListView)findViewById(R.id.list);

	    mExpandableListView.setOnChildClickListener(new OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView list, View view, int groupIndex, int childIndex, long arg4)
            {
            	//First load the routeID
            	mModel.setRouteID(mModel.getRouteID(groupIndex));
            	mModel.setTripID(mModel.getTripID(childIndex));
            	
            	Intent intent = new Intent(getApplicationContext(), TransitStopController.class);
            	startActivity(intent);
            	
                return true;
            }
        });
	    
	    loadExpandableListViewItems();
	}
		
	/**
	  * 
	  */
	private void loadExpandableListViewItems() 
	{
		int index = 1;
		
		ExpandableListAdapter adapter = new ExpandableListAdapter(this, 
																  new ArrayList<String>(),
																  new ArrayList<ArrayList<String>>());
		
		RouteType routeType = mModel.getRouteType();
		
		ArrayList<String> routeList = new ArrayList<String>();
		
		// Get the list of routes.
		if(mModel.getTransitRoutes(routeType, routeList) != ResponseStatus.Completed)
		{
			//TODO Handle this error somehow; Toast the user?
		}
		
		for(String route : routeList)
		{
			ArrayList<String> tripList  = new ArrayList<String>();
			
			if(mModel.getTransitTrips(Integer.toString(index), tripList) != ResponseStatus.Completed)
			{
				//TODO Handle this error somehow; Toast the user?
			}
			
			adapter.addGroup(route, tripList);
			index++;
		}
		 
		mExpandableListView.setAdapter(adapter);
	}
}
