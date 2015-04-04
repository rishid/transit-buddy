package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class StopItemizedOverlay extends ItemizedOverlay<OverlayItem>  
{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	Context mContext;
	
	public StopItemizedOverlay(Drawable defaultMarker) 
	{
		// Adjusts a drawable's bounds so that (0,0)
		// is a pixel in the center of the bottom row of the drawable.
		super(boundCenterBottom(defaultMarker));

	}
	
	public StopItemizedOverlay(Drawable defaultMarker, Context context) 
	{
		super(boundCenterBottom(defaultMarker));

		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}

	@Override
	public int size() 
	{
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) 
	{
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  
	  // Specifies no icon in the dialog
	  dialog.setIcon(0);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  
	  return true;
	}
	
	public void addOverlay(OverlayItem overlay) 
	{
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void addAllOverlay(ArrayList<OverlayItem> overlayItems)
	{
		mOverlays.addAll(overlayItems);
		populate();	
	}
}
