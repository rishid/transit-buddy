package com.transitbuddy.Controllers.TransitRouteController;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.common.enumerations.RouteType;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.transitbuddy.R;
import com.transitbuddy.Controllers.TransitStopController.TransitStopController;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.SystemSettings.TransitSettings;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;
import com.transitbuddy.enumerations.MapType;

public class TransitRouteController extends Activity 
{ 
	TransitBuddyModel mModel;
	TransitSettings mSettings;
	ExpandableListAdapter mAdapter;
	ProgressDialog mProgressDialog;
	DownloadRoutesTask mDownloadRoutesTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    // TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	  
	    setContentView(R.layout.transit_line_controller_view);
	    
	    TransitBuddyApp app = (TransitBuddyApp)getApplicationContext();
	    
	    mModel = app.getTransitBuddyModel();
	    mSettings  = app.getTransitBuddySettings();
	    
	    setTitle(getString(R.string.title_prefix) + mSettings.getSelectedCity());
	    
	    ExpandableListView listView = (ExpandableListView)findViewById(R.id.list);

	    listView.setOnChildClickListener(new OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView list, View view, int groupIndex, int childIndex, long arg4)
            {
            	//First load the routeID
            	mModel.setRouteID(mModel.getRouteID(groupIndex));
            	mModel.setTripID(mModel.getTripID(childIndex));
            	  	
            	// Sets the Map ID Type 
            	mModel.setMapID(MapType.TransitTrip);
            	
            	Intent intent = new Intent(getApplicationContext(), TransitStopController.class);
            	startActivity(intent);
            	
                return true;
            }
        });
	    
	    mAdapter = new ExpandableListAdapter(this, 
				 							 new ArrayList<String>(),
				 							 new ArrayList<ArrayList<String>>());
	    
	    listView.setAdapter(mAdapter);
	    
	    ShowProgressAndDownloadData();
	}
	
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
													if(mDownloadRoutesTask != null)
													{
														mDownloadRoutesTask.cancel(true);
														mDownloadRoutesTask = null;
													}
												}
											});

		mDownloadRoutesTask = new DownloadRoutesTask();
		mDownloadRoutesTask.execute();
	}
    
    private class DownloadRoutesTask extends AsyncTask<Void, Void, RoutesResult>
    {
		@Override
		protected RoutesResult doInBackground(Void... arg0) 
		{
			int index = 0;
			String errorMessage = "";
			
			RouteType routeType = mModel.getRouteType();
			
			ArrayList<String> routeList = new ArrayList<String>();
			
			ResponseStatus status = mModel.getTransitRoutes(routeType, routeList);
			
			// Get the list of routes.
			if(status != ResponseStatus.Completed)
			{
				errorMessage = "Error downloading the route list!";
				return new RoutesResult(status, errorMessage);
			}
			
			for(String route : routeList)
			{
				ArrayList<String> tripList  = new ArrayList<String>();
				
				try
				{
					status = mModel.getTransitTrips(mModel.getRouteID(index), tripList);
				}
				catch(IOException ioe)
				{	 
					status = ResponseStatus.TimedOut;
					errorMessage = ioe.getMessage();
				}
				
				if(status != ResponseStatus.Completed)
				{
					errorMessage += " Error downloading the trip list!";
					return new RoutesResult(status, errorMessage);
				}
				
				mAdapter.addGroupSmart(route, tripList);
				index++;
			}
			
			return new RoutesResult(status, errorMessage);
		}	
		
		protected void onPostExecute(RoutesResult result) 
		{
			if(result.getResponseStatus() == ResponseStatus.Completed)
			{
				mAdapter.notifyDataSetChanged();
			}
			else
			{
				String dialogStr = "";
				
				switch(result.getResponseStatus())
				{
				case TimedOut:
					dialogStr = "Connection timed out!\n" + result.getErrorMessage();
					break;
				case Failed:
					dialogStr = "Connection failed!\n" + result.getErrorMessage();
					break;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(TransitRouteController.this);

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
			
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
    }
    
    private class RoutesResult
    {
    	ResponseStatus mStatus;
    	String mErrorMessage;
    	
    	public RoutesResult(ResponseStatus status, String errorMessage)
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
