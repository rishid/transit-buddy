package com.transitbuddy.Controllers.TransitRouteTypeController;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.common.enumerations.RouteType;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.transitbuddy.R;
import com.transitbuddy.Controllers.TransitRouteController.TransitRouteController;
import com.transitbuddy.Model.TransitBuddyModel;
import com.transitbuddy.TransitBuddy.TransitBuddyApp;

public class TransitRouteTypeController extends ListActivity 
{
	TransitBuddyModel mModel;
	ProgressDialog mProgressDialog;
	DownloadRouteTypesTask mDownloadRouteTypesTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
	    // TODO Auto-generated method stub
	    super.onCreate(savedInstanceState);
	   
	    // Get a reference to the TranistBuddy model.
	    TransitBuddyApp app = (TransitBuddyApp)this.getApplicationContext();
	    mModel = app.getTransitBuddyModel();
	   
	    setContentView(R.layout.transit_system_controller_view);
	    
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
													if(mDownloadRouteTypesTask != null)
													{
														mDownloadRouteTypesTask.cancel(true);
														mDownloadRouteTypesTask = null;
													}
												}
											});

		mDownloadRouteTypesTask = new DownloadRouteTypesTask();
		mDownloadRouteTypesTask.execute();
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
    
    private class DownloadRouteTypesTask extends AsyncTask<Void, Void, RouteTypeResult>
    {
    	@Override
		protected RouteTypeResult doInBackground(Void... arg0) 
		{
			ResponseStatus status = ResponseStatus.Failed;
			
			ArrayList<String> routeTypes = new ArrayList<String>();
			
			try
			{
				status = mModel.getTransitRouteTypes(mModel.getSystemID(), routeTypes);
			}
			catch(IOException ioe)
			{
				Toast.makeText(getApplicationContext(), ioe.getMessage(), Toast.LENGTH_LONG);
			}
			
			return new RouteTypeResult(status, routeTypes);
		}
		
		protected void onPostExecute(RouteTypeResult result) 
		{
			if(result.getResponseStatus() != ResponseStatus.Completed)
			{
				String dialogStr = "";

				AlertDialog.Builder builder = new AlertDialog.Builder(TransitRouteTypeController.this);

				switch(result.getResponseStatus())
				{
				case Failed:
					dialogStr = "Connection failed!\n";
					break;
				case TimedOut:
					dialogStr = "Connection timed out!\n";
					break;
				}

				dialogStr += "Could not download the Transit Routes!";

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
			else
			{
				setListAdapter(new ArrayAdapter<String>(TransitRouteTypeController.this, 
														android.R.layout.simple_list_item_1, 
														result.getRouteTypes()));
			}
			
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
    }
    
    private class RouteTypeResult
    {
    	ResponseStatus mStatus;
    	ArrayList<String> mRouteTypes;
    	
    	public RouteTypeResult(ResponseStatus status, ArrayList<String> routeTypes)
    	{
    		mStatus = status;
    		mRouteTypes = routeTypes;
    	}
    	
    	public ArrayList<String> getRouteTypes()
    	{
    		return mRouteTypes;
    	}
    	
    	public ResponseStatus getResponseStatus()
    	{
    		return mStatus;
    	}
    }
}
