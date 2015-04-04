package com.transitbuddy.Controllers.TransitStopController.TransitStopScheduleController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.common.commands.GetStops;
import com.common.types.TransitStop;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.transitbuddy.*;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;

public class TransitStopScheduleController extends ListActivity
{
	TransitBuddyModel mModel;
	ProgressDialog    mProgressDialog;
    static final ArrayList<HashMap<String,String>> mSchedule = new ArrayList<HashMap<String,String>>();
    SimpleAdapter mAdapter = null;
    
    DownloadStopsTask mDownloadStopsTask = null;
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  
	  TransitBuddyApp app = (TransitBuddyApp)getApplicationContext();
	  mModel = app.getTransitBuddyModel();
	  
      setContentView(R.layout.schedule_controller_view);

      mAdapter = new SimpleAdapter(
      this,
      mSchedule,
      R.layout.schedule_listitem,
      new String[] {"stop","times"},
      new int[] {R.id.transitStopName, R.id.transitStopTimes}
      );

      ShowProgressAndDownloadData();
	}
	
	/**
     * Downloads transit stop data from the transit information server
     */
	private void ShowProgressAndDownloadData()
	{
		mProgressDialog = ProgressDialog.show(this, 
				  							  "TransitBuddy" , 
				  							  "Loading. Please wait ...", 
				  							  true,
				  							  true,
				  							  new DialogInterface.OnCancelListener() 
				  							  {
				  								@Override
				  								public void onCancel(DialogInterface dialog) 
				  								{
				  									if(mDownloadStopsTask != null)
				  									{
				  										mDownloadStopsTask.cancel(true);
				  										mDownloadStopsTask = null;
				  									}
				  								}
				  							});

		mDownloadStopsTask = new DownloadStopsTask();
		mDownloadStopsTask.execute();
	}

    private class DownloadStopsTask extends AsyncTask<Void, Void, StopsResult>
    {
		@Override
		protected StopsResult doInBackground(Void... arg0) 
		{
			String errorMessage = "";
			ResponseStatus status = null;
			
		    ArrayList<TransitStop> stopList = new ArrayList<TransitStop>();
	    	
		    // Clears the schedule
	    	mSchedule.clear();
	    	
	    	String tripID = mModel.getTripID();
	    	
	    	try
	    	{
	    		// Get the list of transit stop
	    		status = mModel.getTransitStops(tripID, stopList, GetStops.ScheduleType.NEXT_FIVE);
	    	}
	    	catch(IOException ioe)
			{
	    		Toast.makeText(getApplicationContext(), ioe.getMessage(), Toast.LENGTH_LONG);
			}
	    		
			if(status != ResponseStatus.Completed)
			{
					errorMessage = "Error downloading the transit trip stops list!";
			}
			else 
			{
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
	    		
					mSchedule.add(temp);
				}
			}
			
			return new StopsResult(status, errorMessage);
		}
		
		protected void onPostExecute(StopsResult result) 
		{
			if(result.getResponseStatus() == ResponseStatus.Completed)
			{
				setListAdapter(mAdapter);
				
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			else
			{
				String dialogStr = "";
				
				switch(result.getResponseStatus())
				{
				case TimedOut:
					dialogStr = "Connection timed out!\n" + result.getErrorMessage();
				case Failed:
					dialogStr = "Connection failed!\n" + result.getErrorMessage();
					break;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(TransitStopScheduleController.this);

				builder.setMessage(dialogStr)
			       	   .setCancelable(false)
			       	   .setPositiveButton("Retry", new DialogInterface.OnClickListener() 
			       	   {
			       		   public void onClick(DialogInterface dialog, int id) 
				           {
				        	   ShowProgressAndDownloadData();
				           }
				       })
				       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
				       {
				           public void onClick(DialogInterface dialog, int id) 
				           {
				                dialog.cancel();
				           }
				       });
				
				AlertDialog alert = builder.create();
				alert.show();
			}
		}		
    }
	
    private class StopsResult
    {
    	ResponseStatus mStatus;
    	String mErrorMessage;
    	
    	public StopsResult(ResponseStatus status, String errorMessage)
    	{
    		mStatus = status;
    		mErrorMessage = errorMessage;
    	}
    	
    	public String getErrorMessage()
    	{
    		return mErrorMessage;
    	}
    	
    	public ResponseStatus getResponseStatus()
    	{
    		return mStatus;
    	}
    }
    
}
