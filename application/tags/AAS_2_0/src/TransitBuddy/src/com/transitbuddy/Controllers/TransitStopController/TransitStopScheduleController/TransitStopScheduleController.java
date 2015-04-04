package com.transitbuddy.Controllers.TransitStopController.TransitStopScheduleController;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.common.types.TransitStop;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.transitbuddy.*;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;

public class TransitStopScheduleController extends ListActivity
{
	TransitBuddyModel mModel;
    static final ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  
	  TransitBuddyApp app = (TransitBuddyApp)getApplicationContext();
	  mModel = app.getTransitBuddyModel();
	  
      setContentView(R.layout.schedule_controller_view);

      SimpleAdapter adapter = new SimpleAdapter(
      this,
      list,
      R.layout.schedule_listitem,
      new String[] {"stop","times"},
      new int[] {R.id.transitStopName, R.id.transitStopTimes}
      );

      populateList();
      setListAdapter(adapter);
	  
	}
	
    private void populateList() 
    {
    	ArrayList<TransitStop> stopList = new ArrayList<TransitStop>();
    	
    	list.clear();
    	
    	String tripID = mModel.getTripID();
    	
    	if(mModel.getTransitStops(tripID, stopList) != ResponseStatus.Completed)
		{
			//TODO Handle this error somehow; Toast the user?
		}
    	
    	for(TransitStop stop : stopList)
    	{
    		HashMap<String,String> temp = new HashMap<String,String>();
    		ArrayList<Date> stopTimes = stop.getStopTimes();
    		StringBuilder timeStr = new StringBuilder();
    		
    		for(Date time : stopTimes)
    			timeStr.append(String.format("%02d:%02d:%02d, ", time.getHours(),
    															 time.getMinutes(),
    															 time.getSeconds()));
    		
    		temp.put("stop", stop.getName());
    		temp.put("times", timeStr.toString());
    		
    		list.add(temp);
        }
    }
}
